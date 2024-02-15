package app.commons;

import app.utils.APIResponse;
import app.utils.ConfigUtils;
import app.utils.Log;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.javalin.http.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class ApiController {

    //private static final int API_RESPONSE_MAX_DUMP_LOG_SIZE = ConfigUtils.getConfigAsInt("api.response.max.dumplog.size", 200);
   // private static final int API_RESPONSE_KEEP_START_END_SIZE = ConfigUtils.getConfigAsInt("api.response.keep.startend.size", 100);


    public static String getRequestLang(JsonObject requestJson){
        String lang = requestJson.has("lang") ? requestJson.get("lang").getAsString() : "en_us";
        return lang;
    }

    public static String getRequestFieldAsString(JsonObject requestJson, String field){
        String fieldValue = null;
        try{
            fieldValue = requestJson.has(field) ? requestJson.get(field).getAsString() : null;
        } catch (Exception ex) {
            Log.info("fail to get request field as String, return null, field name = " + field );
        }
        return fieldValue;
    }


    public static int getRequestFieldAsInt(JsonObject requestJson, String field, int ...defaultValue){
        int intValue = 0;
        try {
            if (defaultValue!=null && defaultValue.length>0) {
                intValue = defaultValue[0];
            }
        } catch (Exception ex) {
            Log.info("fail to get defaultValue field = " + field );
        }
        try {

            intValue = requestJson.has(field) ? requestJson.get(field).getAsInt():intValue;
        } catch (Exception ex) {
            Log.info("fail to get request field as integer, return 0, field name = " + field );
        }
        return intValue;
    }

    public static ArrayList<String> getRequestFieldAsList(JsonObject requestJson, String field){
        ArrayList<String> resultArrayList = new ArrayList<>();
        JsonElement requestElement = requestJson.has(field) ? requestJson.get(field) : null;

        if (requestElement==null) {
            return null;
        }

        try {
            if (requestElement!=null && requestElement.isJsonPrimitive()) {
                String requestString = requestElement.getAsString();
                resultArrayList.addAll(Arrays.asList(requestString.split(",")));
            } else if (requestElement!=null && requestElement.isJsonArray()) {
                requestElement.getAsJsonArray().forEach(element->{
                    resultArrayList.add(element.getAsString());
                });
            }
        } catch (Exception ex) {
            Log.error("error when getRequestFieldAsList fieldname="+field, ex);
        }
        return resultArrayList;
    }


    public static JsonObject renderAPIJson(Context ctx, JsonObject responseJson) {

        if (responseJson==null) {
            responseJson = new JsonObject();
            responseJson.addProperty("responseCode", APIResponse.INTERNAL_ERROR.toString());
        }

        try {
            if (!responseJson.has("responseCode")) {
                responseJson.addProperty("responseCode", APIResponse.SUCCESS.toString());
            }
        } catch(Exception ex) {}

        long endTime = System.currentTimeMillis();
        long startTime = (long)ctx.attribute("startTime");
        long elapsedTime = endTime-startTime;
        try {
            responseJson.addProperty("usedTime", elapsedTime);
            responseJson.addProperty("callerReferenceNo", (String)ctx.attribute("callerReferenceNo"));
            responseJson.addProperty("serverReferenceNo", (String)ctx.attribute("serverReferenceNo"));
            responseJson.addProperty("usedTime", elapsedTime);
        } catch(Exception e) {
            e.printStackTrace();
        }
        Log.info("used time : " + elapsedTime );
        String responseString = responseJson==null? null:responseJson.toString();

//        if (StringUtils.length(responseString)>API_RESPONSE_MAX_DUMP_LOG_SIZE) {
//            responseString = responseString.substring(0, API_RESPONSE_KEEP_START_END_SIZE) + "::::middle of response discarded::::" + responseString.substring(responseString.length()-API_RESPONSE_KEEP_START_END_SIZE);
//        }

        Log.info("response Json : " + responseString);
        return responseJson;
    }
}
