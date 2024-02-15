package app.commons;


import app.utils.*;
import com.google.gson.JsonObject;
import io.javalin.http.ContentType;
import io.javalin.http.ExceptionHandler;
import io.javalin.http.Handler;

import java.util.concurrent.CompletableFuture;

public class RouterHandler extends ApiController {

    public static Handler webInfo = ctx -> {
        CompletableFuture<String> resFuture = CompletableFuture.supplyAsync(() -> Config.getInstance().getValue("settings_version") + AppUtils.getInstance().getSettingValue("git-build.ver"));
        ctx.future(ResponseFuture.plaintext(ctx, resFuture));
    };

    public static Handler ping = ctx -> {
        CompletableFuture<String> resFuture = CompletableFuture.supplyAsync(() -> "OK");
        ctx.future(ResponseFuture.plaintext(ctx, resFuture));
    };

    public static Handler serverRunning = ctx -> {
        CompletableFuture<String> resFuture = CompletableFuture.supplyAsync(() -> "Server is running");
        ctx.future(ResponseFuture.plaintext(ctx, resFuture));
    };

    public static ExceptionHandler exceptionHandler = (e, ctx) -> {
        Log.error("Exception From: " + ctx.path() + " Error :" + e.getStackTrace().toString());
        ctx.result("Internal Error.");
    };

    public static Handler apiNotFound = ctx -> {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("responseCode", APIResponse.API_NOT_FOUND.toString());
        ctx.json(responseJson);
    };
}
