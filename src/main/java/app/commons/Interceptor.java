package app.commons;

import app.utils.APIResponse;
import app.utils.Log;
import app.utils.ServerUtils;
import com.google.gson.JsonObject;
import io.javalin.http.Handler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;


public class Interceptor extends ApiController {

    public static Handler preProcessor  = ctx -> {
        long startTime = System.currentTimeMillis();
        String serverReferenceNo = "[" + ServerUtils.getServerName() + "]" + startTime;
        MDC.put("serverReferenceNo", serverReferenceNo);
        MDC.put("callerReferenceNo", "");
        Log.info("incoming request url " + ctx.method() + " : " + ctx.path());

        JsonObject requestJson = new JsonObject();

        try {
            if (ctx.req().getMethod().toLowerCase(Locale.ROOT).equals("get")) {
                Log.info("GET queryString :" + ctx.queryString());
                for (Entry<String, List<String>> entry : ctx.queryParamMap().entrySet()) {
                    requestJson.addProperty(entry.getKey(), ctx.queryParam(entry.getKey()));
                }
                requestJson.addProperty("callerReferenceNo", StringUtils.defaultIfEmpty(ctx.queryParam("callerReferenceNo"), ""));
            } else if (StringUtils.isNotBlank(ctx.body())){
                requestJson = ctx.bodyAsClass(JsonObject.class);
            } else {
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("responseCode", APIResponse.MISSING_INPUT.toString());
                ctx.json(renderAPIJson(ctx, responseJson));
            }
        } catch (Exception e) {
            Log.error("cannot parse request body, set request body to empty json");
            e.printStackTrace();
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("responseCode", APIResponse.INVALID_INPUT.toString());
            ctx.json(renderAPIJson(ctx, responseJson));
        }

        Log.info("parsed request body : " + requestJson.toString());
        //String callerReferenceNo = (requestJson.get("callerReferenceNo").isJsonNull() ? "" : requestJson.get("callerReferenceNo").getAsString());
        String callerReferenceNo="";
        try { callerReferenceNo=requestJson.get("callerReferenceNo").getAsString(); } catch (Exception e) {}
        MDC.put("callerReferenceNo", callerReferenceNo);
        ctx.attribute("callerReferenceNo", callerReferenceNo);
        ctx.attribute("serverReferenceNo", serverReferenceNo);
        ctx.attribute("startTime", startTime);
        ctx.attribute("requestJson", requestJson);
    };




}

