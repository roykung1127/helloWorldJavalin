package app.utils;

import kong.unirest.ContentType;
import kong.unirest.Unirest;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class DataGridUtils {
	public static final String 	DATAGRID_API = Config.getInstance().getValue("dataGridAddress");
	public static final int RETRY_COUNT = 1;
	public static JsonParser jsonParser = new JsonParser();
	
	public static JsonObject getDataGridAPI(String url){
		return getDataGridAPI(url, null);
	}
	
	public static JsonObject getDataGridAPI(String url, String post){
		long startTime = System.currentTimeMillis();
		
		JsonObject returnJson = null;
		String dataGridUrl = "http://"+DATAGRID_API + url;
		Log.info("Get DataGrid API url : "+dataGridUrl+" post:"+post);
		JsonObject responseJson = null;
		for(int count=0;count<RETRY_COUNT;++count){
			try{
				if(post==null||"".equals(post)){
					responseJson = Unirest.get(dataGridUrl).asObject(JsonObject.class).getBody();
				}else{
					responseJson = Unirest.post(dataGridUrl)
							.contentType("application/x-www-form-urlencoded")
							.accept("application/json")
							.body(post)
							.asObject(JsonObject.class).getBody();
				}
			}catch(Exception e){
				Log.info("[Trial"+count+"]DataGrid timeout : " + e +" url :"+ dataGridUrl + " post:"+post);
				continue;
			}
			Log.log("API Response content(frist 500 byte) :"+StringUtils.substring(responseJson.toString(), 0, 500), LogType.INFO);
			try{
				String message = responseJson.getAsJsonObject("status").get("errorMsg").getAsString();
				int code = responseJson.getAsJsonObject("status").get("errorCode").getAsInt();
				String errorCode = responseJson.getAsJsonObject("status").get("uiResolveUserErrorMsg").getAsString();
				if(!StringUtils.containsIgnoreCase(message, "success") || code!=0){
					Log.log("[Trial"+count+"]DataGrid API return Error : " + message + " Code : " + code + " Error :　"+errorCode, LogType.ERROR);
					continue;
				}
			} catch (Exception e){
				Log.log("[Trial"+count+"]Parse Json Error url :"+ dataGridUrl + " post:"+post, LogType.ERROR, e);
					continue;
			}
			returnJson = responseJson;
			break;
		}
		long endTime = System.currentTimeMillis();
		Log.log("Processing URL time:"+(endTime-startTime)+" for URL:"+url, LogType.INFO);
		return returnJson;
	}
}
