package app.utils;

import io.javalin.http.Context;
import org.apache.commons.lang3.StringUtils;

public class LoginUtils {
	
	public static boolean isLogin(Context ctx) {
		return StringUtils.isNotBlank(ctx.cookie(Config.OTTSESSIONID));
	}
}
