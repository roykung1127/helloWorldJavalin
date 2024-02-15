package app.utils;

import io.javalin.http.Context;
import io.javalin.http.Cookie;
import org.jetbrains.annotations.NotNull;

public class SessionCookies {

    public static String get(@NotNull Context ctx, String key) {
        return ctx.cookie(key);
    }

    public static void put(@NotNull Context ctx, String key, String value) {
        /*
        * Cookie(
    var name: String,
    var value: String,
    var path: String = "/",
    var maxAge: Int = -1,
    var secure: Boolean = false,
    var version: Int = 0,
    var isHttpOnly: Boolean = false,
    var comment: String? = null,
    var domain: String? = null,
    var sameSite: SameSite? = null
)
        *
        * */
        if (value == null) {value = "";}
        io.javalin.http.Cookie ck=new Cookie(key, value, "/", Config.maxAge, false, 0, false, null, ".nowe.com", null);
        ctx.cookie(ck);
    }

    public static void remove(@NotNull Context ctx, String key) {
        ctx.removeCookie(key);
    }
}
