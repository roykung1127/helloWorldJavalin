package app.beans.controllers;

import app.commons.ApiController;
import app.services.OttStreamingService;
import app.utils.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CdnUrlController extends ApiController {
    public static final String[] FORMAT_APP = {AssetDetail.DASH,AssetDetail.HLS_PMO};
    public static final String[] FORMAT_CHROMECAST = {AssetDetail.DASH,AssetDetail.HLS_PMO};
    public static final String[] FORMAT_WEB = {AssetDetail.DASH,AssetDetail.HLS_PMO};
    public static final String[] FORMAT_WEB_SAFARI = {AssetDetail.HLS_VOS,AssetDetail.HLS_PMO};
    public static final String CASTTYPE_AIRPLAY = "airplay";
    public static final String CASTTYPE_CHROMECAST = "chromecast";

    public static Handler getUrlHandler = ctx -> {
        ExecutorService executorService = Executors.newFixedThreadPool(64);
        CompletableFuture<JsonObject> resFuture = CompletableFuture.supplyAsync(() -> renderAPIJson(ctx, getCdnUrl(ctx)));
        ctx.header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, OPTION")
                .header("Access-Control-Allow-Headers", "Content-Type");
        ctx.future(ResponseFuture.json(ctx,resFuture));
    };

    private static JsonObject getCdnUrl(Context ctx) {
        String responseCode = ResponseCode.INTERNAL_ERROR.toString();
        JsonObject returnJson = new JsonObject();
        JsonObject requestJson = ctx.attribute("requestJson");
        String appId = getRequestFieldAsString(requestJson, JSONKey.APP_ID);
        String contentId = getRequestFieldAsString(requestJson, JSONKey.CONTENT_ID);
        String contentType = Constants.ContentType.CHANNEL;
        String deviceId = getRequestFieldAsString(requestJson, JSONKey.DEVICE_ID);
        String deviceType = getRequestFieldAsString(requestJson, JSONKey.DEVICE_TYPE);
        String clientIp = getRequestFieldAsString(requestJson, JSONKey.CLIENT_IP);
        String castType = getRequestFieldAsString(requestJson, JSONKey.CAST_TYPE);
        String uid = getRequestFieldAsString(requestJson, JSONKey.UID);
        String email = getRequestFieldAsString(requestJson, JSONKey.EMAIL);
        String contentSource = getRequestFieldAsString(requestJson,JSONKey.CONTENT_SOURCE);

        //If Client not provide, just use the request IP
        if(clientIp==null||clientIp.isEmpty()) {
            clientIp=ctx.ip();
        }

        String userAgent = ctx.userAgent();
        String[] checkoutFallback=null;
        boolean disablePin=false;

        if(CASTTYPE_CHROMECAST.equalsIgnoreCase(castType)){
            checkoutFallback = FORMAT_APP;
            disablePin=true;
        }else if(CASTTYPE_AIRPLAY.equalsIgnoreCase(castType)) {
            checkoutFallback = FORMAT_WEB_SAFARI;
            disablePin=true;
        }else if(StringUtils.isBlank(deviceType)) {
            checkoutFallback = FORMAT_APP;
        }else if(deviceType.equalsIgnoreCase("WEB")){
            //check Safari.....
            String userAgentForCheck = ctx.userAgent().toLowerCase();
            if((userAgentForCheck.contains("macintosh")||userAgentForCheck.contains("iphone")||userAgentForCheck.contains("ipad"))&&!(userAgentForCheck.contains("chrome")||userAgentForCheck.contains("crios"))&&userAgentForCheck.contains("safari")) {
                checkoutFallback = FORMAT_WEB_SAFARI;
            }else {
                checkoutFallback = FORMAT_WEB;
            }
        }else if(deviceType.startsWith("IOS_")){
            checkoutFallback = FORMAT_WEB_SAFARI;
        }else {
            checkoutFallback = FORMAT_APP;
        }

        //Channel ID hack
        if(Constants.ContentType.CHANNEL.equals(contentType)) {
            while(contentId!=null&&contentId.length()<3) {
                contentId="0"+contentId;
            }
        }

        // Get Streaming URL
        HashMap<String, String> checkoutParams = new HashMap();
        checkoutParams.put(Constants.CheckoutArgs.APP_ID, appId);
        checkoutParams.put(Constants.CheckoutArgs.CONTENT_ID, contentId);
        checkoutParams.put(Constants.CheckoutArgs.CHANNEL_NO, contentId);
        //	checkoutParams.put(Constants.CheckoutArgs.NPVR_ID, npvrId);
        //	checkoutParams.put(Constants.CheckoutArgs.ASSET_ID, assetId);
        checkoutParams.put(Constants.CheckoutArgs.DEVICE_ID, deviceId);
        checkoutParams.put(Constants.CheckoutArgs.DEVICE_TYPE, deviceType);
        checkoutParams.put(Constants.CheckoutArgs.CONTENT_TYPE, contentType);
        checkoutParams.put(Constants.CheckoutArgs.CONTENT_SOURCE, contentSource);
        checkoutParams.put(Constants.CheckoutArgs.UID, uid);
        checkoutParams.put(Constants.CheckoutArgs.EMAIL, email);

        //01-%s
        //[  "random" ]
        String sessionFormat = Config.getInstance().getValue("stream.session.format");
        String[] sessionArgs = ((String)Config.getInstance().getValue("stream.session.args")).split(",");
        String sessionId = OttStreamingService.formatSessionId(checkoutParams);
        String streamingURL = null;
        String slateURL = null;

        do{
            String geoChecking=GeoChecker.checkoutIPChecking(clientIp);
            if(geoChecking!=null) {
                responseCode = geoChecking;
                break;
            }

            try {
                HashMap<String, String> checkoutInfo = OttStreamingService.generateAssetUrlObject(ctx, clientIp, sessionId, checkoutParams, checkoutFallback);
                String checkoutError = checkoutInfo.get(JSONKey.ERROR);
                if(checkoutError!=null) {
                    responseCode = checkoutError;
                    Log.log("Checkout Error :"+checkoutError,  LogType.ERROR);
                    break;
                }
                slateURL = checkoutInfo.get(JSONKey.SLATE);
                streamingURL = checkoutInfo.get(JSONKey.ASSET);

            } catch (Exception e){
                responseCode = ResponseCode.GENERATE_TOKEN_ERROR.toString();
                Log.log("Error:"+e,  LogType.ERROR, e);
                break;
            }

            if(streamingURL==null){
                responseCode = ResponseCode.PRODUCT_INFORMATION_INCOMPLETE.toString();
                Log.log("Generate Streaming URL Error", LogType.ERROR);
                break;
            }

            //Set Streaming URL
            returnJson.addProperty("asset", streamingURL);
            if(slateURL!=null) {
                returnJson.addProperty("slate", slateURL);
            }

            responseCode = ResponseCode.SUCCESS.toString();
            if(!StringUtils.isBlank(deviceType)&&((deviceType.equalsIgnoreCase("WEB_IPHONE")||deviceType.startsWith("IOS_")))){
                returnJson = JsonParser.parseString(returnJson.toString().replaceAll("/\\*", "%2f\\*")).getAsJsonObject();
            }
        }while(false);

        Log.log(String.format("**CHECKOUT_REPORT:ResponseCode=%s, UID=%s, App ID=%s,IP Address=%s, Device ID=%s, Device Type=%s, Content Identifier=%s,Content Type ID=%s,Response JSON=%s",
                responseCode, uid, appId, clientIp, deviceId,deviceType, contentId, contentType, returnJson), LogType.INFO);
        Log.endLog(String.format("responseCode: %s", responseCode));
        returnJson.addProperty("responseCode", responseCode );

        return returnJson;
    }

}
