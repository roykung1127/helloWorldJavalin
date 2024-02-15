package app.beans.controllers;

import app.beans.CdnRawObj;
import app.commons.ApiController;
import app.utils.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.Cookie;
import io.javalin.http.Handler;
import kong.unirest.Unirest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


public class CdnsController extends ApiController {
    //save to Cache with 5 minutes
    public static int COLLECTION_CACHE_TIME = 60 * 30;
    public static int CACHE_TIME = 60 * 3;

    public static Handler getOverflowConfigDataHandler = ctx -> {
        CompletableFuture<JsonObject> resFuture = CompletableFuture.supplyAsync(() -> renderAPIJson(ctx, getOverflowConfigData(ctx)));
        ctx.header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, OPTION")
                .header("Access-Control-Allow-Headers", "Content-Type");
        ctx.future(ResponseFuture.json(ctx,resFuture));
    };

    // 20220629 redirect to profileNameManaged with all
    // public static Handler apiManagedHandler = ctx -> {
    //     ExecutorService executorService = Executors.newFixedThreadPool(64);
    //     CompletableFuture<JsonObject> resFuture = CompletableFuture.supplyAsync(() -> renderAPIJson(ctx, apiManaged(ctx)), executorService);
    //     ctx.header("Access-Control-Allow-Origin", "*")
    //             .header("Access-Control-Allow-Methods", "GET, POST, OPTION")
    //             .header("Access-Control-Allow-Headers", "Content-Type").future(resFuture).contentType(ContentType.JSON);
    // };

    // public static Handler apiManagedHandler = ctx -> {
    //     String tenant = ctx.pathParam("tenant");
    //     String option = ctx.pathParam("options");
    //     String profileName = "all";
    //     ctx.redirect("/2/managed" + "/" + tenant + "/" + profileName + "/" + option);
    // };    


    public static Handler profileNameManagedHandler = ctx -> {
        CompletableFuture<JsonObject> resFuture = CompletableFuture.supplyAsync(() -> renderAPIJson(ctx, profileNameManaged(ctx)));
        ctx.header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, OPTION")
                .header("Access-Control-Allow-Headers", "Content-Type");
        ctx.future(ResponseFuture.json(ctx,resFuture));

    };

    public static Handler cmsPublishHandler = ctx -> {
        CompletableFuture<JsonObject> resFuture = CompletableFuture.supplyAsync(() -> renderAPIJson(ctx, cmsPublish(ctx)));
        ctx.header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, OPTION")
                .header("Access-Control-Allow-Headers", "Content-Type");
        ctx.future(ResponseFuture.json(ctx,resFuture));
    };

    private static JsonObject getOverflowConfigData(Context ctx) {
        String tenant = ctx.pathParam("tenant");
        return getCachedCdnData(tenant);
    }

    private static JsonObject cmsPublish(Context ctx) {
        JsonObject responseJson = new JsonObject();
        String tenant = ctx.pathParam("tenant");
        String tRef = getCmsPublishRef(tenant); //CacheUtils.getData("forceUpdate_" + tenant);
        String sR = ResponseCode.COMMIT_ALREADY.toString();
        if (isAllowedPublished(tRef, 10)){
             tRef = "" + System.currentTimeMillis();
            setCmsPublishRef(tenant, tRef);

            // String all_query = ConfigUtils.getConfig("cockpit.filter.overflow.all");
            // String all_query_url = ConfigUtils.getConfig("cockpit.url") + tenant + all_query;
            // String all_result =  Unirest.get(all_query_url).asString().getBody();

            // CdnMongoDbUtil.setTenantCollections(tenant + "_Collection_all", all_result);
            // CacheUtils.setData(tenant + "_Collection_all" , all_result, COLLECTION_CACHE_TIME );


            // retrieve enableProfileName and query latest result from cockpit
            String enableProfileNameStr = getProfileNameData(tenant + "_Collection_profileName");
            Gson gson = new Gson();
            Type dataListType = new TypeToken<List<CdnRawObj>>(){}.getType();
            List<CdnRawObj> enableProfileNameList = gson.fromJson(enableProfileNameStr, dataListType);
            String profileName_result = null; 
            
            if(!enableProfileNameList.isEmpty()) {
                StringBuilder profileName_query_sb = new StringBuilder();
                String profileName_query_tpl = ConfigUtils.getConfig("cockpit.filter.overflow.PublishQuery.ProfileNameTpl");
                
                for(int i=0; i< enableProfileNameList.size(); i++) {
                    var connector = "";
                    if(i==0) {
                        connector = "?";
                    }
                    else {
                        connector = "&";
                    }
                    profileName_query_sb.append(connector + profileName_query_tpl + enableProfileNameList.get(i).getName());
                }
                
                String profileName_query_url = ConfigUtils.getConfig("cockpit.url") + tenant + profileName_query_sb.toString();
                profileName_result =  Unirest.get(profileName_query_url).asString().getBody();
                // Log.info("the profileName result is " + profileName_result);

                // update mongodb
                CdnMongoDbUtil.setProfileNameCollectionSyncCockpit(tenant + "_Collection_profileName", enableProfileNameList);
                CacheUtils.setData(tenant + "_Collection_profileName" , profileName_result , COLLECTION_CACHE_TIME);
            }


            String fs_query = ConfigUtils.getConfig("cockpit.filter.forced.scheduled");
            String fs_query_url = ConfigUtils.getConfig("cockpit.url") + tenant + fs_query;
            String fs_result =  Unirest.get(fs_query_url).asString().getBody();

            CdnMongoDbUtil.setTenantCollections(tenant + "_Collection_fs", fs_result);
            CacheUtils.setData(tenant + "_Collection_fs" , fs_result , COLLECTION_CACHE_TIME);

            getCdnData_v2(tenant);
            sR = ResponseCode.SUCCESS.toString();
        }
        responseJson.addProperty("status", sR);
        responseJson.addProperty("forceUpdateRefNo", tRef);
        return responseJson;
    }



    private static JsonObject apiManaged(Context ctx) {
        JsonObject responseJson = new JsonObject();
        String options = ctx.pathParam("options");
        String tenant = ctx.pathParam("tenant");
        if (options.equalsIgnoreCase("enable")) {
            String tRef = "" + System.currentTimeMillis();
//            String cacheRef = CacheUtils.getData("apiManaged_" + tenant);
//            if (StringUtils.isBlank(cacheRef) || getApiManagedRef(tenant).equalsIgnoreCase("-1")){
                setApiManagedRef(tenant, tRef);
                getCdnData(tenant);
//           } else {
//                setApiManagedRef(tenant, tRef);
//            }
            responseJson.addProperty("managedReferenceNo", tRef);
        } else if (options.equalsIgnoreCase("disable")) {
            setApiManagedRef(tenant, "-1");
            getCdnData(tenant);
        } else if (options.equalsIgnoreCase("status")) {
            String cacheRef = getApiManagedRef(tenant);
            if (StringUtils.isNotBlank(cacheRef) && !cacheRef.equalsIgnoreCase("-1")){
                responseJson.addProperty("status", "enable");
                responseJson.addProperty("managedReferenceNo", cacheRef);
            } else if (StringUtils.isBlank(cacheRef) || cacheRef.equalsIgnoreCase("-1")){
                responseJson.addProperty("status", "disable");
                responseJson.addProperty("managedReferenceNo", cacheRef);
            } else {
                responseJson.addProperty("status", "disable");
                responseJson.addProperty("managedReferenceNo", cacheRef);
            }
        }
        responseJson.addProperty("apiManaged", options);
        return responseJson;
    }

    private static JsonObject profileNameManaged(Context ctx) {
        JsonObject responseJson = new JsonObject();
        String options = ctx.pathParam("options");
        String tenant = ctx.pathParam("tenant");
        String tRef = "" + System.currentTimeMillis();
        setProfileNameRef(tenant, tRef);

        // Determine profileName
        String theProfileName = null;
        Map<String, String> paramMap = ctx.pathParamMap();
        if(paramMap.containsKey("profileName")) {     
            theProfileName = paramMap.get("profileName");
        }
        if(theProfileName == null) {
            theProfileName = "all";
        }


        if (options.equalsIgnoreCase( "enable")) {
            JsonObject setEnableResult = setProfileNameData("enable", theProfileName, ctx);
            Log.info(setEnableResult.toString());
            // 20220729 if no profileName set in cockpit, return record not found
            if (!setEnableResult.get("status").getAsString().equalsIgnoreCase("RECORD_NOT_FOUND")) {
                getCdnData_v2(tenant);
            } else {
                responseJson.addProperty("responseCode", ResponseCode.RECORD_NOT_FOUND_IN_CMS.toString());
            }
            responseJson.addProperty("managedReferenceNo", tRef);
        } else if (options.equalsIgnoreCase("disable")) {
            JsonObject setDisableResult = setProfileNameData("disable", theProfileName, ctx); 
            // 20220729 if no profileName set in cockpit, return record not found
            if (!setDisableResult.get("status").getAsString().equalsIgnoreCase("RECORD_NOT_FOUND")) {
                getCdnData_v2(tenant);
            } else {
                responseJson.addProperty("responseCode", ResponseCode.RECORD_NOT_FOUND_IN_CMS.toString());
            }    
        } else if (options.equalsIgnoreCase("status")) {    

            JsonObject getStatusResult = setProfileNameData("status", theProfileName, ctx);
            if (!getStatusResult.get("status").getAsString().equalsIgnoreCase("RECORD_NOT_FOUND")) {
                String dataProfileName = getProfileNameData(tenant + "_Collection_profileName");
                String searchKey = ("all".equalsIgnoreCase(theProfileName))? "Name" : "\"Name\": \"" + theProfileName + "\"";
                Log.info("searchKey: " + searchKey );
                if(dataProfileName.contains(searchKey)) {
                    responseJson.addProperty("status", "enable");
                    responseJson.addProperty("managedReferenceNo", getProfileNameRef(tenant));
                } else {
                    responseJson.addProperty("status", "disable");
                }
            } else {
                responseJson.addProperty("responseCode", ResponseCode.RECORD_NOT_FOUND_IN_CMS.toString());
            }
        }



        // responseJson.addProperty("profileName", options);
        responseJson.addProperty("profileName", theProfileName);
        return responseJson;
    }


    
    private static JsonObject getCachedCdnData(String tenant) {
        String retVal = CacheUtils.getData("tenant_" + tenant);
        if(StringUtils.isNotBlank(retVal)){
            return new Gson().fromJson(retVal, JsonObject.class);
        } else {
            return getCdnData_v2(tenant);
        }
    }

    private static String getTenantCollectionData(String tenantCollectionKey) {
        String data = CacheUtils.getData(tenantCollectionKey);
        if (StringUtils.isBlank(data)) {
            //query = ConfigUtils.getConfig("cockpit.filter.overflow.all");
            data = CdnMongoDbUtil.getTenantCollections(tenantCollectionKey);
        }
        return data;
    }

    private static String getProfileNameData(String tenantCollectionKey) {
        String data = CacheUtils.getData(tenantCollectionKey);
          if (StringUtils.isBlank(data)) {
            data = CdnMongoDbUtil.getProfileNameCollection(tenantCollectionKey);
        }
        return data;
    }    

    
    private static JsonObject setProfileNameData (String action, String profileName, Context ctx) {
        JsonObject responseJson = new JsonObject();
        String tenant = ctx.pathParam("tenant");
        String tRef = getProfileNameRef(tenant); //CacheUtils.getData("forceUpdate_" + tenant);
        String sR = ResponseCode.COMMIT_ALREADY.toString();

        // if (isAllowedPublished(tRef, 10)){

             tRef = "" + System.currentTimeMillis();
             setProfileNameRef(tenant, tRef);

            if(profileName.equalsIgnoreCase("all")) {

          
                // get all api managed
                String profileName_query = ConfigUtils.getConfig("cockpit.filter.overflow.profileNameAll");
                String profileName_query_url = ConfigUtils.getConfig("cockpit.url") + tenant + profileName_query;
                String profileName_result =  Unirest.get(profileName_query_url).asString().getBody();
                
                Gson gson = new Gson();
                Type dataListType = new TypeToken<List<CdnRawObj>>(){}.getType();
                List<CdnRawObj> resp = gson.fromJson(profileName_result, dataListType);
                if(action.equals("enable")) {
                    CdnMongoDbUtil.setProfileNameCollectionSyncCockpit(tenant + "_Collection_profileName", resp);
                }  else if(action.equals("disable")) {
                    CdnMongoDbUtil.setProfileNameCollectionSyncCockpit(tenant + "_Collection_profileName", null);
                }



            } else {
                String profileName_query = ConfigUtils.getConfig("cockpit.filter.overflow.profileName");
                String profileName_query_url = ConfigUtils.getConfig("cockpit.url") + tenant + profileName_query + profileName;
                String profileName_result =  Unirest.get(profileName_query_url).asString().getBody();
    
                Gson gson = new Gson();
                Type dataListType = new TypeToken<List<CdnRawObj>>(){}.getType();
                List<CdnRawObj> resp = gson.fromJson(profileName_result, dataListType);
    
                CdnRawObj resp_obj = new CdnRawObj();
                
                // handle no record found exception
                if(resp.isEmpty()) {
                    Log.info("No profileName found in cockpit");
                    sR = ResponseCode.RECORD_NOT_FOUND.toString();
                    responseJson.addProperty("status", sR);
                    responseJson.addProperty("responseCode", sR);
                    return responseJson;
                }

                // action = status handling
                if("status".equalsIgnoreCase(action)) {
                    if(resp.isEmpty()) {
                        Log.info("No profileName found in cockpit");
                        sR = ResponseCode.RECORD_NOT_FOUND.toString();
                        responseJson.addProperty("status", sR);
                        responseJson.addProperty("responseCode", sR);
                        return responseJson;
                    } else {
                        sR = ResponseCode.RECORD_EXIST.toString();
                        responseJson.addProperty("status", sR);
                        responseJson.addProperty("responseCode", sR);
                        return responseJson;
                    }
                }


                if(resp != null) {
                    resp_obj = resp.get(0);
                }
    
                if(CollectionUtils.isEmpty(resp)){
                    action = "disable";
                } 
                CdnMongoDbUtil.setProfileNameCollection(action, tenant + "_Collection_profileName", resp_obj);
            }

            

            // get updated result from mongo db and update cache
            String updatedProfileName = CdnMongoDbUtil.getProfileNameCollection(tenant + "_Collection_profileName");
            CacheUtils.setData(tenant + "_Collection_profileName" , updatedProfileName, COLLECTION_CACHE_TIME );
            // Log.info("get updated result after set profileName" + updatedProfileName);

            sR = ResponseCode.SUCCESS.toString();
        // }
        responseJson.addProperty("status", sR);
        responseJson.addProperty("forceUpdateRefNo", tRef);
        return responseJson;
    }


    private static void setApiManagedRef (String tenant, String data) {
        CacheUtils.setData("apiManaged_" + tenant, data, CACHE_TIME);
        CdnMongoDbUtil.setApiManagedRef("apiManaged_" + tenant, data);
    }

    private static String getApiManagedRef(String tenant) {
        String data = CacheUtils.getData("apiManaged_" + tenant);
        if (StringUtils.isBlank(data)) {
            data = CdnMongoDbUtil.getApiManagedRef("apiManaged_" + tenant);
            CacheUtils.setData("apiManaged_" + tenant, data , CACHE_TIME);
        }
        return data;
    }

    private static void setCmsPublishRef (String tenant, String data) {
        CdnMongoDbUtil.setApiManagedRef("cmsPublish_" + tenant, data);
    }

    private static String getCmsPublishRef(String tenant) {
        return CdnMongoDbUtil.getApiManagedRef("cmsPublish_" + tenant);
    }

    private static void setProfileNameRef (String tenant, String data) {
        CdnMongoDbUtil.setApiManagedRef("profileName_" + tenant, data);
    }

    private static String getProfileNameRef (String tenant) {
        return CdnMongoDbUtil.getApiManagedRef("profileName_" + tenant);
    }

    private static JsonObject getCdnData(String tenant) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("tenant", tenant);

        //Check Overflow mode
        String overflowMode_url = ConfigUtils.getConfig("cockpit.overflowMode") + tenant;
        JsonObject overflowMode_result = Unirest.get(overflowMode_url).asObject(JsonObject.class).getBody();
        String retVal_OpsMode = overflowMode_result.get("OpsMode").getAsString();
        if (StringUtils.isBlank(retVal_OpsMode)) {
                retVal_OpsMode = "1";
        }
        jsonObj.addProperty("overflowMode", retVal_OpsMode);
        //jsonObj.addProperty("overflowMode", overflowModeResponse(retVal_OpsMode));
        if (!retVal_OpsMode.equalsIgnoreCase("3")) {
            CacheUtils.setData("tenant_" + tenant, jsonObj.toString(), CACHE_TIME);
            return jsonObj;
        }

        String data = "";
        String tRef = getApiManagedRef(tenant);
        Log.log("getCdnData >> apiManaged Ref: " + tRef, LogType.INFO);
        if (StringUtils.isNotBlank(tRef) && !tRef.equals("-1")) {
            data = getTenantCollectionData(tenant + "_Collection_all");
        } else {
            data = getTenantCollectionData( tenant + "_Collection_fs");
        }

        if (!retVal_OpsMode.equalsIgnoreCase("3") || StringUtils.isBlank(data)) {
            CacheUtils.setData("tenant_" + tenant, jsonObj.toString(), CACHE_TIME);
            return jsonObj;
        }

        Gson gson = new Gson();
        Type dataListType = new TypeToken<List<CdnRawObj>>(){}.getType();
        List<CdnRawObj> resp = gson.fromJson(data, dataListType);

        // String api = ConfigUtils.getConfig("cockpit.url") + tenant + query;
        //List<CdnRawObj> resp = Unirest.get(api).asObject( new GenericType<List<CdnRawObj>>(){}).getBody();

        JsonArray libid_array = new JsonArray();
        JsonArray pid_array = new JsonArray();
        JsonArray ch_array = new JsonArray();
        JsonArray so_array = new JsonArray();

        for (CdnRawObj obj : resp) {
            if (obj.getOverflow().equalsIgnoreCase(CdnsConstants.OVERFLOW_SCHEDULED)
                    && (isScheduledNotStarted(obj.getScheduled())
                    || isScheduleNotOperated(getUnixTimestamp(obj.getScheduled()), obj.getDuration()))){
               // break;
                continue;
            }
            if (obj.getOverflow().equalsIgnoreCase(CdnsConstants.OVERFLOW_API_MANAGED)
                    && getDurationData(obj.getDuration()) > 0
                    && isScheduleNotOperated(getLongValue(tRef), obj.getDuration()))
                    {
                        if (StringUtils.isBlank(tRef))//|| !tRef.equals("-1"))
                            setApiManagedRef(tenant, "-1");
                        continue;
            }
            List<String> Ids = Arrays.asList(obj.getId().split("\\s*,\\s*"));
            switch (obj.getType()) {
                case CdnsConstants.TYPE_VOD_Library -> cdnMetaDataObj(libid_array, obj);
                case CdnsConstants.TYPE_VOD_PID -> cdnMetaDataObj(pid_array, obj);
                case CdnsConstants.TYPE_Live -> cdnMetaDataObj(ch_array, obj);
                case CdnsConstants.TYPE_Startover -> cdnMetaDataObj(so_array, obj);
            }
        }
        jsonObj.add(CdnsConstants.TYPE_VOD_Library, libid_array);
        jsonObj.add(CdnsConstants.TYPE_VOD_PID, pid_array);
        jsonObj.add(CdnsConstants.TYPE_Live, ch_array);
        jsonObj.add(CdnsConstants.TYPE_Live, so_array);


        CacheUtils.setData("tenant_" + tenant, jsonObj.toString(), CACHE_TIME);
        return jsonObj;
    }

    private static JsonObject getCdnData_v2 (String tenant) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("tenant", tenant);

        //Check Overflow mode
        String overflowMode_url = ConfigUtils.getConfig("cockpit.overflowMode") + tenant;
        JsonObject overflowMode_result = Unirest.get(overflowMode_url).asObject(JsonObject.class).getBody();
        String retVal_OpsMode = overflowMode_result.get("OpsMode").getAsString();
        if (StringUtils.isBlank(retVal_OpsMode)) {
                retVal_OpsMode = "1";
        }
        jsonObj.addProperty("overflowMode", retVal_OpsMode);
        if (!retVal_OpsMode.equalsIgnoreCase("3")) {
            CacheUtils.setData("tenant_" + tenant, jsonObj.toString(), CACHE_TIME);
            return jsonObj;
        }

        String data = "";
        String dataProfileName = "";
        String tRef = getProfileNameRef(tenant);
        Log.log("getCdnData >> profileName Ref: " + tRef, LogType.INFO);
        
        dataProfileName = getProfileNameData(tenant + "_Collection_profileName");
        data = getTenantCollectionData( tenant + "_Collection_fs");

        // Log.info("ln 383");
        // Log.info(data);
        // Log.info(dataProfileName);
        Gson gson = new Gson();
        Type dataListType = new TypeToken<List<CdnRawObj>>(){}.getType();
        List<CdnRawObj> respfs = gson.fromJson(data, dataListType);
        List<CdnRawObj> respProfileName = gson.fromJson(dataProfileName, dataListType);

        List<CdnRawObj> resp = new ArrayList<>();
        if(respfs!=null && !respfs.isEmpty()) {resp.addAll(respfs);};
        if(respProfileName!=null && !respProfileName.isEmpty()) {resp.addAll(respProfileName);};

        JsonArray libid_array = new JsonArray();
        JsonArray pid_array = new JsonArray();
        JsonArray ch_array = new JsonArray();
        JsonArray so_array = new JsonArray();

        for (CdnRawObj obj : resp) {
            Log.info("Process Obj Name="+obj.getName()+"|Type="+obj.getType()+"|CDN="+obj.getCdn()+"|Scheduled="+obj.getScheduled()+"|Duration="+obj.getDuration());
            if (obj.getOverflow().equalsIgnoreCase(CdnsConstants.OVERFLOW_SCHEDULED)
                    && ( isScheduledNotStarted(obj.getScheduled())
                    || isScheduleNotOperated(getUnixTimestamp(obj.getScheduled()), obj.getDuration()))){
               // break;
                continue;
            }
            if (obj.getOverflow().equalsIgnoreCase(CdnsConstants.OVERFLOW_API_MANAGED)
                    && getDurationData(obj.getDuration()) > 0
                    && isScheduleNotOperated(getLongValue(tRef), obj.getDuration()))
                    {
                        if (StringUtils.isBlank(tRef))//|| !tRef.equals("-1"))
                            setApiManagedRef(tenant, "-1");
                        continue;
            }
            switch (obj.getType()) {
                case CdnsConstants.TYPE_VOD_Library -> cdnMetaDataObj(libid_array, obj);
                case CdnsConstants.TYPE_VOD_PID -> cdnMetaDataObj(pid_array, obj);
                case CdnsConstants.TYPE_Live -> cdnMetaDataObj(ch_array, obj);
                case CdnsConstants.TYPE_Startover -> cdnMetaDataObj(so_array, obj);

            }
        }
        jsonObj.add(CdnsConstants.TYPE_VOD_Library, libid_array);
        jsonObj.add(CdnsConstants.TYPE_VOD_PID, pid_array);
        jsonObj.add(CdnsConstants.TYPE_Live, ch_array);
        jsonObj.add(CdnsConstants.TYPE_Startover, so_array);


        CacheUtils.setData("tenant_" + tenant, jsonObj.toString(), CACHE_TIME);

        // Log.info("ln 394");
        // CacheUtils.getData("tenant_" + tenant);
        // Log.info("ln 396");

        return jsonObj;
    }

    private static void cdnMetaDataObj (JsonArray metaDataList, CdnRawObj obj) {
        List<String> Ids = Arrays.asList(obj.getId().split("\\s*,\\s*"));
        for (String id : Ids) {
            for(int i = 0; i < metaDataList.size(); i++) {  // iterate through the JsonArray
                if(metaDataList.get(i).getAsJsonObject().get("id").getAsString().equals(id)) {
                    //check overflowMode
                    if (metaDataList.get(i).getAsJsonObject().get("overflowMode").getAsString().equals(CdnsConstants.OVERFLOW_FORCED)) {
                        metaDataList.remove(i);
                        break;
                    } else if (metaDataList.get(i).getAsJsonObject().get("overflowMode").getAsString().equals(CdnsConstants.OVERFLOW_SCHEDULED) && obj.getOverflow().equalsIgnoreCase(CdnsConstants.OVERFLOW_SCHEDULED) && obj.getOverflow().equalsIgnoreCase(CdnsConstants.OVERFLOW_API_MANAGED)) {
                        metaDataList.remove(i);
                        break;
                    } else if (metaDataList.get(i).getAsJsonObject().get("overflowMode").getAsString().equals(CdnsConstants.OVERFLOW_API_MANAGED) && obj.getOverflow().equalsIgnoreCase(CdnsConstants.OVERFLOW_API_MANAGED)) {
                        metaDataList.remove(i);
                        break;
                    }
               }
            }
            JsonObject cdnObj = new JsonObject();
            cdnObj.addProperty("CDN", obj.getCdn());
            cdnObj.addProperty("id", id);
            cdnObj.addProperty("overflowMode", obj.getOverflow());
            metaDataList.add(cdnObj);
        }
    }

    private static long getUnixTimestamp(String timestampStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long timestamp = 0L;
        Date date = null;
        try {
            format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
            date = format.parse(timestampStr);
            timestamp = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    private static Boolean isScheduledNotStarted(String scheduledTimeStr) {
        long scheduledTime = getUnixTimestamp(scheduledTimeStr);
        long systime = System.currentTimeMillis();
        String OpMode = "Started";
        Boolean retVal = true;
        try {
            long sys_duration = scheduledTime - systime;
            if (sys_duration > 0L) {
                OpMode = "Not Started";
                retVal = true;
            } else {
                OpMode = "Started";
                retVal = false;
            }
            Log.log(OpMode +"|sys_duration(ms)=" + sys_duration, LogType.INFO);
            return retVal;

        } catch (Exception e) {
            return true;
        }
    }

    private static Boolean isScheduleNotOperated(long scheduledTime, String durationStr) {
        int duration = getDurationData(durationStr);
        try {
            long sys_duration = System.currentTimeMillis() - scheduledTime;
            long duration_millisecond = TimeUnit.MINUTES.toMillis(duration);
            long overtime = sys_duration - duration_millisecond;
            String OpMode = "";
            boolean retVal = true;
            if (sys_duration > 0L && sys_duration < duration_millisecond) {
                OpMode = "Operated";
                retVal = false;
            } else if (overtime > 0) {
                OpMode = "Expired";
                retVal = true;
            } else {
                OpMode = "Not operated";
                retVal = true;
            }
            Log.log(OpMode +"|sys_duration(ms)=" + sys_duration + "|cms_duration(ms):" + duration_millisecond + "|overtime(ms)=" + overtime, LogType.INFO);
            return retVal;
        } catch (Exception e) {
            return true;
        }
    }

    private static Boolean isAllowedPublished(String tRef, int duration) {
        long timeRef = getLongValue(tRef);
        try {
            long sys_duration = System.currentTimeMillis() - timeRef;
            if (sys_duration > TimeUnit.SECONDS.toMillis(duration)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private static int getDurationData(String durationStr){
        if (StringUtils.isNotBlank(durationStr)) {
            return NumberUtils.toInt(durationStr, -1) ;
        }
        return -1;
    }

    private static long getLongValue(String value){
        if (StringUtils.isNotBlank(value)) {
            return NumberUtils.toLong(value, -1);
        }
        return -1;
    }

    /*private static String overflowModeResponse(String mode) {

        switch (mode) {
            case CdnsConstants.OPSMODE_NO_OVERFLOW_MODE -> {
                return ResponseCode.NO_OVERFLOW.toString();
            }
            case CdnsConstants.OPSMODE_WEBTVAPI_CONFIG_FILE_OVERFLOW_MODE -> {
                return ResponseCode.CONFIG_FILE_OVERFLOW.toString();
            }
            case CdnsConstants.OPSMODE_CDNS_OVERFLOW_MODE -> {
                return ResponseCode.CDNS_OVERFLOW.toString();
            }
            default -> {
                return ResponseCode.NO_OVERFLOW.toString();
            }
        }

    }*/

}
