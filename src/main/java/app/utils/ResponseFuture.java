package app.utils;

import com.google.gson.JsonObject;
import io.javalin.http.ContentType;
import io.javalin.http.Context;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ResponseFuture {

    public static Supplier json (Context ctx, CompletableFuture<JsonObject> resFuture) {
        Supplier s = () -> resFuture.thenAccept(r -> ctx.json(r)).exceptionally(ex -> {ctx.result("Error" + ex.getMessage()).contentType(ContentType.TEXT_PLAIN);return null;});
        return s;
    }

    public static Supplier plaintext (Context ctx, CompletableFuture<String> resFuture) {
        Supplier s = () -> resFuture.thenAccept(r -> ctx.result(r).contentType(ContentType.TEXT_PLAIN)).exceptionally(ex -> {ctx.result("Error" + ex.getMessage()).contentType(ContentType.TEXT_PLAIN);return null;});
        return s;
    }

    public static Supplier result (Context ctx, CompletableFuture<String> resFuture, String contentType) {
        Supplier s = () -> resFuture.thenAccept(r -> ctx.result(r).contentType(contentType)).exceptionally(ex -> {ctx.result("Error" + ex.getMessage()).contentType(ContentType.TEXT_PLAIN);return null;});
        return s;
    }

}
