package app.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerUtils {
	public static String getServerName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "";
		}
	}
}

