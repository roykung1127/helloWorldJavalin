package app.beans.controllers;

import app.commons.ApiController;
import app.utils.AppUtils;
import app.utils.FlatFile;
import app.utils.JSONKey;
import app.utils.ResponseFuture;
import com.google.gson.JsonObject;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleCdnController extends ApiController {

    public static Handler enableVodsOverflowHandler = ctx -> {
        CompletableFuture<JsonObject> resFuture = CompletableFuture.supplyAsync(() -> renderAPIJson(ctx, enableVodsOverflow(ctx)));
        ctx.header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, OPTION")
                .header("Access-Control-Allow-Headers", "Content-Type");
        ctx.future(ResponseFuture.json(ctx,resFuture));
    };

    public static Handler disableVodsOverflowHandler = ctx -> {
        CompletableFuture<JsonObject> resFuture = CompletableFuture.supplyAsync(() -> renderAPIJson(ctx, disableVodsOverflow(ctx)));
        ctx.header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, OPTION")
                .header("Access-Control-Allow-Headers", "Content-Type");
        ctx.future(ResponseFuture.json(ctx,resFuture));
    };

    public static Handler configOverflowHandler = ctx -> {
        CompletableFuture<JsonObject> resFuture = CompletableFuture.supplyAsync(() -> renderAPIJson(ctx, configOverflowOverflow(ctx)));
        ctx.header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, OPTION")
                .header("Access-Control-Allow-Headers", "Content-Type");
        ctx.future(ResponseFuture.json(ctx,resFuture));
    };

    public static Handler getOverflowHandler = ctx -> {
        CompletableFuture<JsonObject> resFuture = CompletableFuture.supplyAsync(() -> renderAPIJson(ctx, getOverflow(ctx)));
        ctx.future(() -> resFuture.thenAccept(r -> ctx.json(r)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET, POST, OPTION")
                        .header("Access-Control-Allow-Headers", "Content-Type"))
                .exceptionally(ex -> {ctx.result("Error" + ex.getMessage())
                        .contentType(ContentType.TEXT_PLAIN);return null;}));
    };

    private static JsonObject enableVodsOverflow (Context ctx) {
        JsonObject responseJson = new JsonObject();
//        try {
//            MongoDBUtil.updateOverflowStatusCode(1);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        AppUtils.setCachedOverflowData("1");
        FlatFile.setData("1");
        return responseJson;
    }

    private static JsonObject disableVodsOverflow (Context ctx) {
        JsonObject responseJson = new JsonObject();
//        try {
//            MongoDBUtil.updateOverflowStatusCode(2);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        AppUtils.setCachedOverflowData("2");
        FlatFile.setData("2");
        return responseJson;
    }

    private static JsonObject configOverflowOverflow (Context ctx)  {
        JsonObject responseJson = new JsonObject();
//        try {
//            MongoDBUtil.updateOverflowStatusCode(0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        AppUtils.setCachedOverflowData("0");
        FlatFile.setData("0");
        return responseJson;
    }

    private static JsonObject getOverflow (Context ctx) {
        JsonObject responseJson = new JsonObject();
//        try {
//            s = MongoDBUtil.getOverflowStatusCode();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        Integer s = NumberUtils.toInt(AppUtils.getCachedOverflowData(), 0);
        responseJson.addProperty(JSONKey.OverFlowStatusCode, s );
        return responseJson;
    }

}
