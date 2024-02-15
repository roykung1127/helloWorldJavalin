package app.services;

import app.utils.DataGridUtils;
import app.utils.Log;
import app.utils.LogType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kong.unirest.Unirest;
import org.apache.commons.lang3.StringUtils;


public class YellowViuService {
	public static JsonParser jsonParser = new JsonParser();
	public static final String VIU_CONTENT_PREFIX="VIU";
	public static final String LIB_ID="L50002";
	
	public static boolean isViuContent(String contentId){
		boolean result = false;
		do{
			if(StringUtils.isEmpty(contentId)){
				break;
			}
			if(contentId.startsWith(VIU_CONTENT_PREFIX)){
				result=true;
			}
		}while(false);
		Log.log("isViuContent: " + contentId + "=" + result, LogType.INFO);
		return result;
	}
	
//	public static String[] getYellowViuProductList() {
//		String getCategoryProductListServlet = "/DataCacheAPI/QA1/v2.2.1.3/API/ott/getCategoryProductListServlet";
//		JsonObject responseJson = DataGridUtils.getDataGridAPI(getCategoryProductListServlet);
//		String yellowViuList = responseJson.get("data").getAsJsonObject().get("YellowViu").getAsString();
//		String[] yeallowViuListArray = yellowViuList.split(",");
//		return yeallowViuListArray;
//	}

	public static String getAssetUrl(String contentId) {
		//String viuConetentId = conetentId.substring(3);
		String[] viuContetIdPath = contentId.split("_");
		if(viuContetIdPath.length<3) {
			return null;
		}
		String viuContentId = viuContetIdPath[2];
		String assetName = "/viucontent/" + viuContentId ;
		String path = "/" +viuContentId + ".m3u8";
		return assetName+path;
	}
}
