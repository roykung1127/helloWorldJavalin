package app.services;



import app.utils.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.javalin.http.Context;

public class ConvoyService {
	public static JsonParser jsonParser = new JsonParser();
	
	public static final String DISABLE_WEB_CHECKOUT_HTTPS = Config.getInstance().getValue("WebCheckoutHttps.disable");
	
	 public static String getConvoyIp(Context ctx, String asset, String deviceType) throws Exception{
	    	String ip = null;
	    	/*
	    	 {
	        "host": "rr.cdn.example.com",
	        "asset": "/Content/HLS/LIVE/Channel(HLS_CH621)/Stream(04)/index.m3u8",
	        "client_address": "0.0.0.0",
	        "routing_address": "0.0.0.0"
			}
	    	 */
	    	String convoyServer = "http://convoyrr:8000/1/http_session";
	    	String postContent="{\"host\":\"rr.cdn.example.com\",\"asset\":\""+asset+"\",\"client_address\":\"0.0.0.0\",\"routing_address\":\"0.0.0.0\"}";
	    	String result = null;
	    	boolean disableHttps=false;
			if("true".equals(DISABLE_WEB_CHECKOUT_HTTPS)) {
				Log.log("Disable Web HTTPS Checkout", LogType.INFO);
				disableHttps=true;
			}
	    	do{
	    		try {
	    			Log.log("Server:"+convoyServer+" Post:"+postContent, LogType.INFO);
	    			result = RequestUtil.httpPostRequest(ctx, convoyServer, postContent);
	    		} catch (Exception e) {
	    			Log.log("Error is found while call Convoy asset:"+asset+" "+e, LogType.ERROR, e);
	    			throw new Exception(ResponseCode.CONVOY_ERROR.toString());
	    		}
	    		try {
	    			Log.log("Convoy Response:"+result, LogType.INFO);
	    			JsonElement resultJson = jsonParser.parse(result);
	    			String url = resultJson.getAsJsonObject().get("http_session_url").getAsString();
	    			String parseUrl[]=url.split("/");
	    			String ipWithPort=parseUrl[2];
	    			String parseip[]=ipWithPort.split(":");
	    			ip=parseip[0];
	    			
	    			if("WEB".equals(deviceType)&&!disableHttps) {
	    				ip="https://"+ip;
	    			}else {
	    				ip="http://"+ip;
	    			}
	    		} catch (Exception e) {
	    			Log.log("Error is found while get IP from Convoy result:"+result+" "+e, LogType.ERROR, e);
	    			throw new Exception(ResponseCode.CONVOY_ERROR.toString());
	    		}
	    	}while(false);
	    	return ip;
	    }
	    
}
