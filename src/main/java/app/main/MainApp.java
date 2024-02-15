package app.main;

import java.io.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import app.commons.Interceptor;
import app.commons.MongoDB;
import app.commons.RouterHandler;
import app.beans.controllers.CdnsController;
import app.beans.controllers.SimpleCdnController;
import app.utils.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import io.javalin.Javalin;
import io.javalin.json.JsonMapper;
import kong.unirest.Unirest;
import kong.unirest.gson.GsonObjectMapper;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.NotNull;

import static io.javalin.apibuilder.ApiBuilder.*;

public class MainApp {
    public static boolean isDevSystem = true;
    public static String ENV = "local";
    public static String sfx = "";

    public static void main(String[] args) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        if (args.length > 0) {
            isDevSystem = false;
            switch (args[0].toLowerCase()) {
                case "qa" -> {
                    ENV = "qa";
                }
                case "pp" -> {
                    ENV = "pp";
                }
                case "prod" -> {
                    ENV = "prod";
                }
                default -> {
                    ENV = "local";
                    sfx = "-stdout";
                }
            }
        } else {
            isDevSystem = true;
            sfx = "-stdout";
//				config.enableDevLogging(); 			 // enable extensive development logging for http and websocket
        }
        ClassLoader classLoader = MainApp.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("log4j2" + sfx + ".xml");
        ConfigurationSource source = new ConfigurationSource(inputStream);
        Configurator.initialize(null, source);

        Javalin app = Javalin.create(config -> {
            Log.info("Running Env: " + ENV);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.S")
                    .registerTypeAdapter(int.class, new IntTypeAdapter())
                    .registerTypeAdapter(Integer.class, new IntTypeAdapter())
                    .registerTypeAdapter(long.class, new LongTypeAdapter())
                    .registerTypeAdapter(Long.class, new LongTypeAdapter())
                    .registerTypeAdapter(Double.class, (JsonSerializer<Double>) (value, theType, context) -> {
                        if (value.isNaN()) {
                            return new JsonPrimitive(0);
                        } else if (value.isInfinite() || value < 0.01) {
                            return new JsonPrimitive(value);
                        } else {
                            return new JsonPrimitive((new BigDecimal(value)).setScale(1, RoundingMode.HALF_UP));
                        }
                    }).create();

            config.jsonMapper(new JsonMapper() {
                @NotNull
                @Override
                public <T> T fromJsonStream(@NotNull InputStream json, @NotNull Type targetType) {
                    Reader reader = new InputStreamReader(json);
                    return gson.fromJson(reader, targetType);
                }

                @NotNull
                @Override
                public <T> T fromJsonString(@NotNull String json, @NotNull Type targetType) {
                    return gson.fromJson(json, targetType);
                }

                @NotNull
                @Override
                public InputStream toJsonStream(@NotNull Object obj, @NotNull Type type) {
                    return new ByteArrayInputStream(gson.toJson(obj).getBytes());
                }

                @NotNull
                @Override
                public String toJsonString(@NotNull Object obj, @NotNull Type type) {
                    return gson.toJson(obj);
                }
            });

            Unirest.config()
                    .setObjectMapper(new GsonObjectMapper(gson))
                    .socketTimeout(10000)
                    .connectTimeout(10000)
                    .concurrency(400, 200);
        }).start(8081);





        app.routes(() -> {
            // http interceptor
            get("/_webinfo", RouterHandler.webInfo);
            get("/ping", RouterHandler.ping);
            get("/demo", ctx -> {
                ctx.result("Hello World");
            });
        });

        app.error(404, ctx -> { ctx.result("404"); });
       // app.exception(Exception.class, RouterHandler.exceptionHandler);

        app.events(event -> {
            event.serverStopping(() -> {
                Log.info("Server is stopping");
                RedisUtils.close();
                MongoDB.close();
                Unirest.shutDown();
            });
            event.serverStopped(() -> { /* Your code here */ });
        });

    }

}
