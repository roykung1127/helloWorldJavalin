package app.utils;

import app.beans.APIRequest;
import io.javalin.http.Context;
import kong.unirest.Unirest;
import org.jetbrains.annotations.NotNull;

public class RequestUtil {

    @NotNull
    public static String getLocale(@NotNull Context ctx) {
        String locale = ctx.cookie("locale");
        if (locale == null) {
            ctx.cookie("locale", "zh", Config.maxAge);
            return "zh";
        }
        return locale;
    }

    public static String getBridgeEngUrl(String name) {
    	String baseURL = Config.getInstance().getValue("BridgeEng_BaseUrl");
    	String path = Config.getInstance().getValue(name);
    	return baseURL + path;
    }

    public static String getWebTVApiUrl(String callFunction) {
        String baseURL = Config.getInstance().getValue("WebTVAPI_BaseUrl");
        String path = Config.getInstance().getValue(callFunction);
        return  baseURL + path;
    }

    public static String getOTTApiUrl(String callFunction) {
        String baseURL = Config.getInstance().getValue("OTTAPI_BaseUrl");
        String path = Config.getInstance().getValue(callFunction);
        return  baseURL + path;
    }

    public static String getWSGApiUrl(String callFunction) {
        String baseURL = Config.getInstance().getValue("WSG_BaseUrl");
        String path = Config.getInstance().getValue(callFunction);
        return  baseURL + path;
    }

    public static String httpPostRequest(Context ctx, String url, String postContent) {
        Log.startLog(String.format("URL: %s, postContent: %s", url, postContent));
        String responseObj = Unirest.post(url)
                .header("Content-Type", "application/json")
                .headerReplace("user-agent", ctx.userAgent())
                .body(postContent)
                .asString()
                .getBody();
        Log.endLog("responseObj: "+responseObj);
        return responseObj;
    }

}
