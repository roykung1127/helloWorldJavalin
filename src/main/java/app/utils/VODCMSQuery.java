package app.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kong.unirest.Unirest;
import org.json.JSONArray;
import org.json.JSONObject;

public class VODCMSQuery {

	public static final String DISABLE_WEB_CHECKOUT_HTTPS = Config.getInstance().getValue("WebCheckoutHttps.disable");

	public static final String API_STATUS = "api_status";
	public static final String STATUS_CODE = "status_code";
	public static final String DATA = "data";
	public static final String RESPONSE = "response";
	public static final String NUMFOUND = "numFound";
	public static final String DOCS = "docs";
	public static final String WEB_IMAGE_ZH ="product_zh_tw_web_image_file1_t";
	public static final String WEB_IMAGE_EN ="product_en_us_web_image_file1_t";
	public static final String IS_RESTRICTED_TO_APP  ="product_no_app_checkout_t";
	public static final String CLASSIFICATION ="meta_SECCLASS_type_id_t";
	public static final String DURATION = "product_duration_td";
	public static final String CPID = "product_cp_id_t";
	public static final String PRODUCT_PAYMENT_TYPE = "product_payment_type_t";
	public static final String PRODUCT_ID = "product_id_t";
	public static final String LIBRARY_NAME_ZH = "library_zh_tw_library_name_t";
	public static final String LIBRARY_NAME_EN = "library_en_us_library_name_t";
	public static final String SERIES_ID = "product_series_id_t";
	public static final String PRODUCT_SUBSIDIARY1 = "product_subsidiary_id_1_t";
	public static final String PRODUCT_SUBSIDIARY2 = "product_subsidiary_id_2_t";
	public static final String PRODUCT_SUBSIDIARY3 = "product_subsidiary_id_3_t";
	public static final String PRODUCT_SUBSIDIARY4 = "product_subsidiary_id_4_t";
	public static final String PRODUCT_SUBSIDIARY5 = "product_subsidiary_id_5_t";
	public static final String PRODUCT_PRODUCT_TYPE = "product_product_type_t";
	public static final String ASSET_WEB_READY = "asset_web_ready_b";
	public static final String ASSET_HLS_READY = "asset_hls_ready_b";
	public static final String ASSET_FLV_READY = "asset_flv_ready_b";
	public static final String SCHEDULE_START_TIME = "schedule_start_tl";
	public static final String SCHEDULE_END_TIME = "schedule_end_tl";
	public static final String SCHEDULE_STATUS = "schedule_status_t";

	//mongo data
	public static final String LIB_ID = "product_library_id_t";
	public static final String PAYMENT_TYPE = "product_payment_type_t";
	
	
	public static final String PRODUCT_DISPLAY_ON_ONAIR = "product_display_on_onair_t";
	public static final String PPV = "PPV";
	public static final String SVOD = "SVOD";
	public static final String HLS_ADAPTIVE = "";
	public static final String HLS_LOW = "_Layer1_vod";
	public static final String HLS_HIGH = "_Layer2_vod";
	
	public static final String STATUS_PASSED = "PASSED";
	
	//NPVR
	public static final String NPVR_ID = "product_npvr_id_t";
	public static final String WEB_ASSET_ID = "asset_web_PlayReady_asset_id_t";
	public static final String HLS_ASSET_ID = "asset_hls_HLS_asset_id_t";
	
	public static final String JUMP_START = "product_vod_jump_start_td";
	
	//queryProductForCheckoutReplacement
	public static final String DATAGRID_API = Config.getInstance().getValue("dataGridAddress");
	public static final String QUERY_PRODUCT = "http://"+DATAGRID_API+"/DataCacheAPI/API/checkout/queryProductForCheckout?q=product_id_t:";
	public static final String QUERY_NPVR = "http://"+DATAGRID_API+"/DataCacheAPI/API/checkout/queryProductForCheckout?q=product_npvr_id_t:";
	public static final String QUERY_ASSET_URL = "/DataCacheAPI/API/asset/getAssetPathServlet?segmentId=&segmentType=";
	public static final String QUERY_YELLOW_VIU_SUBTITLE_URL = "/DataCacheAPI/API/checkout/getCheckOutInfoServlet?";

	public static final String SUBTITLE = "subtitles";
	public static final String PREROLL = "preRollURL";
	
	//On9Code
	public static final String OLD_SUBTITLE = "subtitleURL";
	//On9Code
	
	public static final String SOLR_FILTER = "product_zh_tw_web_image_file1_t,product_en_us_web_image_file1_t,product_no_app_checkout_t,meta_SECCLASS_type_id_t,product_duration_td,product_cp_id_t,product_payment_type_t,product_id_t,library_zh_tw_library_name_t,library_en_us_library_name_t,product_series_id_t,product_subsidiary_id_1_t,product_subsidiary_id_2_t,product_subsidiary_id_3_t,product_subsidiary_id_4_t,product_subsidiary_id_5_t,product_product_type_t,asset_web_ready_b,asset_hls_ready_b,asset_flv_ready_b,schedule_start_tl,schedule_end_tl,schedule_status_t,product_library_id_t,product_payment_type_t,product_display_on_onair_t,product_npvr_id_t,asset_web_PlayReady_asset_id_t,asset_hls_HLS_asset_id_t";
	
	public static HashMap<String, String> getYellowViuSubtitlePath(String pid, String deviceType) {
		long startTime = System.currentTimeMillis();
		String subtitleUrl = null;
		String preRollUrl = null;
		String oldSubtitle = null;
		HashMap<String, String> subtitleObj = new HashMap<String, String>();
		boolean disableHttps=false;
		if("true".equals(DISABLE_WEB_CHECKOUT_HTTPS)) {
			Log.log("Disable Web HTTPS Checkout", LogType.INFO);
			disableHttps=true;
		}
		
		do {
			JsonObject returnJSON = null;
			if(pid==null||"".equals(pid)){
				break;
			}
			try {
				String url = QUERY_YELLOW_VIU_SUBTITLE_URL +"&id="+pid;
				returnJSON = DataGridUtils.getDataGridAPI(url);
			}catch(Exception e){
				Log.log("DataGrid API Error"+e, LogType.ERROR, e);
				break;
			}
			JsonObject urlData=null;
			try {
				urlData = returnJSON.get("data").getAsJsonObject();
			}catch(Exception e){
				Log.log("DataGrid API Error"+e, LogType.ERROR, e);
			}
			
			try {
				subtitleUrl = urlData.get(SUBTITLE).getAsJsonArray().toString();
				if("WEB".equals(deviceType)&&!disableHttps){
					subtitleUrl= subtitleUrl.replaceAll("http:", "https:");
				}
			}catch(Exception e){
				Log.log("DataGrid API Subtitle subtitles not found! "+e, LogType.ERROR, e);
			}try {
				preRollUrl = urlData.get(PREROLL).getAsString();
				preRollUrl=preRollUrl+"&correlator="+System.currentTimeMillis();
			}catch(Exception e){
				Log.log("DataGrid API preRollUrl not found! "+e, LogType.ERROR, e);
			}

			//On9Code
			try {
				oldSubtitle = urlData.get(OLD_SUBTITLE).getAsString();
				if("WEB".equals(deviceType)&&!disableHttps){
					oldSubtitle = oldSubtitle.replaceAll("http:", "https:");
				}
			}catch(Exception e){
				Log.log("DataGrid API Subtitle subtitleUrl not found! "+e, LogType.ERROR, e);
			}
			//On9Code
			subtitleObj.put(SUBTITLE, subtitleUrl);
			subtitleObj.put(PREROLL, preRollUrl);
			subtitleObj.put(OLD_SUBTITLE, oldSubtitle);
			
			
		}while(false);
		Log.log("elapseTime = "+ (System.currentTimeMillis() - startTime),LogType.INFO);
		return subtitleObj;
	}
	
	public static AssetDetail getAssetPath(String pid, String type) {
		long startTime = System.currentTimeMillis();
		AssetDetail assetUrl = new AssetDetail();
		do {
			JsonObject returnJSON = null;
			if(pid==null||"".equals(pid)||type==null||"".equals(type)){
				break;
			}
			assetUrl.setPid(pid);
			if(!"npvr".equalsIgnoreCase(type)) {
				type="product";
			}
			assetUrl.setType(type);
			try {
				String url = QUERY_ASSET_URL +"&type="+type+"&id="+pid;
				returnJSON = DataGridUtils.getDataGridAPI(url);
			}catch(Exception e){
				Log.log("DataGrid API Error"+e, LogType.ERROR, e);
				assetUrl = null;
				break;
			}

			try {
				JsonObject urlData = returnJSON.get("data").getAsJsonObject();
				Set<Entry<String, JsonElement>> assetList = urlData.entrySet();
				for (Map.Entry<String, JsonElement> assetObject: assetList) {
					String assetType = assetObject.getKey();
					JsonObject assetDetail = assetObject.getValue().getAsJsonObject();
					String status = assetDetail.get("status").getAsString();//For what?
					//So on9 use different name in same type object....
					//assetUrl.getAsset().put(assetType.toUpperCase(),assetDetail.get("dashAssetPath").getAsString());
					
					if("mbts".equalsIgnoreCase(assetType)) {
						//MBTS may not exist
						if(assetDetail.get("hlsAssetPath")!=null) {
							assetUrl.getAsset().put(AssetDetail.HLS_VOS,assetDetail.get("hlsAssetPath").getAsString());
						}
						if(assetDetail.get("dashAssetPath")!=null) {
							assetUrl.getAsset().put(AssetDetail.DASH,assetDetail.get("dashAssetPath").getAsString());
						}
					}else if("HLS".equalsIgnoreCase(assetType)) {
						//Fallback logic for no MBTS
						if(assetUrl.getAsset().get(AssetDetail.HLS_PMO)==null) {
							assetUrl.getAsset().put(AssetDetail.HLS_PMO,assetDetail.get("hlsAssetPath").getAsString());
						}
					}else if("DASH".equalsIgnoreCase(assetType)) {
						//Fallback logic for no MBTS
						if(assetUrl.getAsset().get(AssetDetail.DASH)==null) {
							assetUrl.getAsset().put(AssetDetail.DASH,assetDetail.get("dashAssetPath").getAsString());
						}
					}else if("SS".equalsIgnoreCase(assetType)) {
						//Fallback logic for no MBTS
						if(assetUrl.getAsset().get(AssetDetail.SS)==null) {
							assetUrl.getAsset().put(AssetDetail.SS,assetDetail.get("ssAssetPath").getAsString());
						}
					}else {
						//unknow
					}
				}

				/*
				//Hopefully old API spec?
				String status = urlData.get("status").getAsString();
				if(!"ONAIR".equals(status)) {
					Log.log("PID:"+pid+" Type:"+type+" not ONAIR "+status+" ", logType.ERROR);
					break;
				}
				assetUrl.getAsset().put(AssetDetail.DASH,urlData.get("dashAssetPath").getAsString());
				assetUrl.getAsset().put(AssetDetail.HLS,urlData.get("hlsAssetPath").getAsString());
				*/
			}catch(Exception e){
				Log.log("DataGrid API Error"+e, LogType.ERROR, e);
				assetUrl = null;
			}
		}while(false);
		Log.log("elapseTime = "+ (System.currentTimeMillis() - startTime),LogType.INFO);
		return assetUrl;
	}
/*
	//only get one Product data
	public static VodDetail getVodDetailByPid(String pid){
		long startTime = System.currentTimeMillis();
		Log.startLog(String.format("pid: %s, startTime: %s", pid, startTime));
		VodDetail json = null;
		//Get json of VOD object from Eric's API server
		String content = "";
		do{
			try {
		//		String vodDetailUrl = "http://api.nowtv.now.com/nowtv-api/vodapi/queryProductForCheckout?nowtvapi_v=1.00&nowtvapi_key=NOWPLAYER_1.00&start=0&rows=1&indent=on&wt=json&q=product_id_t:" + pid;
		//		vodDetailUrl = vodDetailUrl + "&fl=" + SOLR_FILTER;
				String vodDetailUrl = QUERY_PRODUCT + pid;
				Log.log(String.format("vodDetailUrl: %s", vodDetailUrl), LogType.INFO);
				URL url;
				url = new URL(vodDetailUrl);
				HttpURLConnection client = (HttpURLConnection)url.openConnection();
				client.setRequestMethod("GET");
				client.setDoOutput(true);
				client.setConnectTimeout(1000);
				client.setReadTimeout(5000);
				client.connect();
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				String line = "";

				while((line = in.readLine()) != null) {
					content += line;
				}
				String responseJsonLog = "Response From VODCMS : " + content;
				Log.log(responseJsonLog, LogType.INFO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.log("Exception is found while calling to Vod Solr API", LogType.ERROR, e);
				break;
			}
			JSONObject returnJSON = null;
			try{
				//string content from API to JSON
				returnJSON = JSONObject.fromObject(content);
				
				//get API status
				JSONObject statusJson = returnJSON.getJSONObject(API_STATUS);
				String isSuccess = statusJson.getString(STATUS_CODE);
				if(!"0".equals(isSuccess)){
					Log.log("Vod Solr API return error code : " + statusJson.toString(), LogType.ERROR);
					break; 
				}
				
				//get data field
				JSONObject dataJson = returnJSON.getJSONObject(DATA);
				if(dataJson == null || "".equals(dataJson.toString()) || "null".equals(dataJson.toString())){
					Log.log("Vod Solr API DATA field error : NULL", LogType.ERROR);
					break; 
				}
				
				//get response
				JSONObject responseJson = dataJson.getJSONObject(RESPONSE);
				if(responseJson == null || "".equals(responseJson.toString()) || "null".equals(responseJson.toString())){
					Log.log("Vod CMS API RESPONSE field error : NULL", LogType.ERROR);
					break; 
				}
				
				//check VOD object exist
				int numFound = responseJson.getInt(NUMFOUND);
				if(numFound == 0){
					Log.log("Vod CMS API NUMFOUND field error : numFound = " + numFound, LogType.ERROR);
					break; 
				}
				
				//get the field to object
				JSONArray docsArray = responseJson.getJSONArray(DOCS);
				if(docsArray == null || "".equals(docsArray.toString()) || "null".equals(docsArray.toString())){
					Log.log("Vod CMS API DOCS field error : NULL", LogType.ERROR);
					break; 
				}
				if(docsArray.size() == 0){
					Log.log("Vod CMS API DOCS field error : size = " + docsArray.size(), LogType.ERROR);
					break; 
				}
				
				
				//convert JSON to VodDetail 
				JSONObject vodJson = docsArray.getJSONObject(0);
				json = parseVodJson(vodJson);

			} catch(Exception e){
				json = null;
				Log.log("Exception is found while parsing the response from Vod Solr API", LogType.ERROR, e);
			}
		}while(false);
		Log.endLog("elapseTime = "+ (System.currentTimeMillis() - startTime));
		return json;
	}
	*/
	public static VodDetail parseVodJson(JSONObject vodJson){
		Log.startLog(String.format("vodJson: %s", vodJson));
		VodDetail vodDetailObj = null;
		
		//require field
		boolean isWebImageZH = vodJson.getBoolean(WEB_IMAGE_ZH);
		boolean isWebImageEN = vodJson.getBoolean(WEB_IMAGE_EN);
		boolean isIsRestrictedToApp = vodJson.getBoolean(IS_RESTRICTED_TO_APP);
		boolean isClassification = vodJson.getBoolean(CLASSIFICATION);
		boolean isDuration = vodJson.getBoolean(DURATION);
		boolean isCpid = vodJson.getBoolean(CPID);
		boolean isProductPaymentType = vodJson.getBoolean(PRODUCT_PAYMENT_TYPE);
		boolean isProductId = vodJson.getBoolean(PRODUCT_ID);
		
		boolean isProductDisplayOnOnAir = vodJson.getBoolean(PRODUCT_DISPLAY_ON_ONAIR);
		
		//optional field
		boolean isProductSubsidiary1 = vodJson.getBoolean(PRODUCT_SUBSIDIARY1);
		boolean isProductSubsidiary2 = vodJson.getBoolean(PRODUCT_SUBSIDIARY2);
		boolean isProductSubsidiary3 = vodJson.getBoolean(PRODUCT_SUBSIDIARY3);
		boolean isProductSubsidiary4 = vodJson.getBoolean(PRODUCT_SUBSIDIARY4);
		boolean isProductSubsidiary5 = vodJson.getBoolean(PRODUCT_SUBSIDIARY5);
		boolean isProductProductType = vodJson.getBoolean(PRODUCT_PRODUCT_TYPE);
		boolean isAssetWebReady = vodJson.getBoolean(ASSET_WEB_READY);
		boolean isAssetHlsReady = vodJson.getBoolean(ASSET_HLS_READY);
		boolean isAssetFlvReady = vodJson.getBoolean(ASSET_FLV_READY);
		boolean isSeriesId = vodJson.getBoolean(SERIES_ID);
		boolean isLibraryNameEn = vodJson.getBoolean(LIBRARY_NAME_EN);
		boolean isLibraryNameZh = vodJson.getBoolean(LIBRARY_NAME_ZH);
		//NPVR
		boolean isNpvrId = vodJson.getBoolean(NPVR_ID);
		boolean isWebAssetId = vodJson.getBoolean(WEB_ASSET_ID);
		boolean isHlsAssetId = vodJson.getBoolean(HLS_ASSET_ID);
		
		boolean isScheduleStartTime=vodJson.getBoolean(SCHEDULE_START_TIME);
		boolean isScheduleEndTime=vodJson.getBoolean(SCHEDULE_END_TIME);
		boolean isScheduleStatus=vodJson.getBoolean(SCHEDULE_STATUS);

		//mongo data
		boolean isPaymentType = vodJson.getBoolean(PAYMENT_TYPE);
		
		boolean isLibId = vodJson.getBoolean(LIB_ID);

		boolean isJumpstart= vodJson.getBoolean(JUMP_START);
		

		boolean isPreroll= vodJson.getBoolean(PREROLL);

		//get require field
		Log.log(String.format("isPaymentType: %s, isIsRestrictedToApp: %s, isClassification: %s, isCpid: %s, isProductPaymentType: %s, isProductId: %s, isProductDisplayOnOnAir: %s",
				isPaymentType, isIsRestrictedToApp, isClassification,
				isCpid, isProductPaymentType, isProductId, isProductDisplayOnOnAir
				), LogType.INFO);
		
		if(isPaymentType && isIsRestrictedToApp && isClassification && isCpid && isProductPaymentType && isProductId && isProductDisplayOnOnAir){
			vodDetailObj = new VodDetail();
			vodDetailObj.setPaymentType(vodJson.getString(PAYMENT_TYPE));
			vodDetailObj.setIsAppRestricted(vodJson.getString(IS_RESTRICTED_TO_APP).equalsIgnoreCase("Y"));
			vodDetailObj.setClassification(vodJson.getString(CLASSIFICATION));	
			vodDetailObj.setCpid(vodJson.getString(CPID));
			vodDetailObj.setProductPaymentType(vodJson.getString(PRODUCT_PAYMENT_TYPE));
			vodDetailObj.setProductId(vodJson.getString(PRODUCT_ID));
			vodDetailObj.setProductDisplayOnOnAir(vodJson.getString(PRODUCT_DISPLAY_ON_ONAIR));
			
			//get optional field
			if (isWebImageZH) {
				vodDetailObj.setWebImgZHPath(vodJson.getString(WEB_IMAGE_ZH));
			}
			if (isWebImageEN) {
				vodDetailObj.setWebImgENPath(vodJson.getString(WEB_IMAGE_EN));
			}
			if (isDuration) {
				vodDetailObj.setDuration(vodJson.getLong(DURATION)*60*1000);
			}
			if(isProductSubsidiary1){
				vodDetailObj.setProductSubsidiary1(vodJson.getString(PRODUCT_SUBSIDIARY1));
			}
			if(isProductSubsidiary2){
				vodDetailObj.setProductSubsidiary2(vodJson.getString(PRODUCT_SUBSIDIARY2));
			}
			if(isProductSubsidiary3){
				vodDetailObj.setProductSubsidiary3(vodJson.getString(PRODUCT_SUBSIDIARY3));
			}
			if(isProductSubsidiary4){
				vodDetailObj.setProductSubsidiary4(vodJson.getString(PRODUCT_SUBSIDIARY4));
			}
			if(isProductSubsidiary5){
				vodDetailObj.setProductSubsidiary5(vodJson.getString(PRODUCT_SUBSIDIARY5));
			}
			if(isProductProductType){
				vodDetailObj.setProductType(vodJson.getString(PRODUCT_PRODUCT_TYPE));
			}
			if(isAssetWebReady){
				vodDetailObj.setIsWEB(vodJson.getBoolean(ASSET_WEB_READY));
			}
			if(isAssetHlsReady){
				vodDetailObj.setIsHLS(vodJson.getBoolean(ASSET_HLS_READY));
			}
			if(isAssetFlvReady){
				vodDetailObj.setIsFLV(vodJson.getBoolean(ASSET_FLV_READY));
			}
			if(isLibId){
				vodDetailObj.setLibId(vodJson.getString(LIB_ID));
			}
			if(isLibraryNameEn){
				vodDetailObj.setLibNameEn(vodJson.getString(LIBRARY_NAME_EN));
			}
			if(isLibraryNameZh){
				vodDetailObj.setLibNameZh(vodJson.getString(LIBRARY_NAME_ZH));
			}
			//NPVR
			if(isNpvrId){
				vodDetailObj.setNpvrId(vodJson.getString(NPVR_ID));
			}
			if(isWebAssetId){
				vodDetailObj.setWebAssetId(vodJson.getString(WEB_ASSET_ID));
			}
			if(isHlsAssetId){
				vodDetailObj.setHlsAssetId(vodJson.getString(HLS_ASSET_ID));
			}
			
			if (isScheduleStartTime){
				vodDetailObj.setScheduleStartTime(vodJson.getLong(SCHEDULE_START_TIME));
			}
			if (isScheduleEndTime){
				vodDetailObj.setScheduleEndTime(vodJson.getLong(SCHEDULE_END_TIME));
			}
			if(isScheduleStatus){
				vodDetailObj.setScheduleStatus(vodJson.getString(SCHEDULE_STATUS));
			}
			
			if (isSeriesId) {
				vodDetailObj.setSeriesId(vodJson.getString(SERIES_ID));
			}
			
			if (isJumpstart) {
				try {
					vodDetailObj.setJumpstart(vodJson.getLong(JUMP_START));
				}catch(Exception e) {
					Log.log("JUMP START field not number", LogType.ERROR);
				}
			}
			
			if(isPreroll) {
				vodDetailObj.setPreroll(vodJson.getString(PREROLL));
			}
			
		} else {
			//Error log
			String errorlog = "Solr API VOD Data field error : PRODUCT_PAYMENT_TYPE : ";
			if(isProductPaymentType){
				errorlog += vodJson.getString(PRODUCT_PAYMENT_TYPE) +", ";
			}else{
				errorlog += "NULL, ";
			}
			
			errorlog += "Solr API VOD Data field error : IS_RESTRICTED_TO_APP : ";
			if(isIsRestrictedToApp){
				errorlog += vodJson.getString(IS_RESTRICTED_TO_APP) +", ";
			}else{
				errorlog += "NULL, ";
			}
			
			errorlog += "Solr API VOD Data field error : CLASSIFICATION : ";
			if(isClassification){
				errorlog += vodJson.getString(CLASSIFICATION) +", ";
			}else{
				errorlog += "NULL, ";
			}
			errorlog += "Solr API VOD Data field error : DURATION : ";
			if(isDuration){
				errorlog += vodJson.getString(DURATION) +", ";
			}else{
				errorlog += "NULL, ";
			}
			
			errorlog += "Solr API VOD Data field error : CPID : ";
			if(isCpid){
				errorlog += vodJson.getString(CPID) +", ";
			}else{
				errorlog += "NULL, ";
			}
			
			errorlog += "Solr API VOD Data field error : PRODUCT_ID : ";
			if(isProductId){
				errorlog += vodJson.getString(PRODUCT_ID) +", ";
			}else{
				errorlog += "NULL, ";
			}
			
			errorlog += "Solr API VOD Data field error : PRODUCT_DISPLAY_ON_ONAIR : ";
			if(isProductId){
				errorlog += vodJson.getString(PRODUCT_DISPLAY_ON_ONAIR) +", ";
			}else{
				errorlog += "NULL, ";
			}
			
			errorlog += "Solr API VOD Data field error : PAYMENT_TYPE : ";
			if(isPaymentType){
				errorlog += vodJson.getString(PAYMENT_TYPE);
			}else{
				errorlog += "NULL";
			}
			Log.log(errorlog, LogType.ERROR);
		}
		Log.endLog(String.format("vodDetailObj: %s", vodDetailObj));
		return vodDetailObj;
	}
	
	
	public static HashMap<String, VodDetail> getVodDetailByPidAry(List<String> pids){
		long startTime = System.currentTimeMillis();
		String startLog = "Function getVodDetailByPid START ";
		for(int i = 0; i<pids.size(); ++i){
			startLog  += ", pid[" + i + "] = " + pids.get(i) ;
		}
		startLog  += ", startTime = " + startTime;
		
		HashMap<String, VodDetail> vodDetailMap = new HashMap<String, VodDetail>();
		
		Log.info(startLog);
		VodDetail json = null;
		//Get json of VOD object from Eric's API server
		String content = "";
		do {
			List<VodDetail> productList = new ArrayList<VodDetail>();
			for(int i = 0; i<pids.size(); ++i){
				productList.addAll(getVodDetailListByProductId(pids.get(i)));
			}
			for(int i = 0; i<productList.size();++i){
				try{
					json = productList.get(i);
					if(json == null){
						Log.error("Vod CMS API DOCS field Problem : NULL " + i);
						continue;
					}
					if(vodDetailMap.containsKey(json.getProductId())){
						continue;
					}
					vodDetailMap.put(json.getProductId(), json);
				}catch(Exception e){
					continue;
				}
			}
			
				
		}while(false);
		startLog += ", elapseTime = " + (System.currentTimeMillis() - startTime);
		Log.info("Function getVodDetailByPid END");
		return vodDetailMap;
	}
	
	
	public static List<VodDetail> getVodDetailList(String vodDetailUrl) {
		Log.startLog(String.format("vodDetailUrl: %s", vodDetailUrl));
		//vodDetailUrl = vodDetailUrl + "&fl=" + SOLR_FILTER;
		Log.log(String.format("vodDetailUrl with Filter : %s", vodDetailUrl), LogType.INFO);
		long startTime = System.currentTimeMillis();
		VodDetail json = null;
		List<VodDetail> vodDetailList = new ArrayList<VodDetail>();
		
		do {
			//Get List of VOD JSON object from Solr API server
			String content = "";
			try {
				content = Unirest.get(vodDetailUrl).asObject(String.class).getBody();
			/*
				URL url = new URL(vodDetailUrl);
				HttpURLConnection client = (HttpURLConnection)url.openConnection();
				client.setRequestMethod("GET");
				client.setDoOutput(true);
				client.setConnectTimeout(1000);
				client.connect();
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				String line = "";
				while((line = in.readLine()) != null) {
					content += line;
				}
				
				Log.log(String.format("Response From Solr API : %s", content), LogType.INFO);
				
			*/
			} catch (Exception e) {
				Log.log(String.format("Connect Solr API error : "+e), LogType.ERROR, e);
				break;
			}
			JSONObject returnJSON = null;
		
			try{
				//string content from API to JSON
				returnJSON = GsonUtils.getGsonInstance().fromJson(content, JSONObject.class);
				
				//get API status
				JSONObject statusJson = returnJSON.getJSONObject(API_STATUS);
				String isSuccess = statusJson.getString(STATUS_CODE);
				if(!"0".equals(isSuccess)){
					Log.log(String.format("Solr API return error code : %s", statusJson.toString()), LogType.ERROR);
					break; 
				}
				
				//get data field
				JSONObject dataJson = returnJSON.getJSONObject(DATA);
				if(dataJson == null || "".equals(dataJson.toString()) || "null".equals(dataJson.toString())){
					Log.log(String.format("Solr API DATA field error : NULL"), LogType.ERROR);
					break; 
				}
				
				//get response
				JSONObject responseJson = dataJson.getJSONObject(RESPONSE);
				if(responseJson == null || "".equals(responseJson.toString()) || "null".equals(responseJson.toString())){
					Log.log(String.format("Solr API RESPONSE field error : NULL"), LogType.ERROR);
					break; 
				}
				
				//check VOD object exist
				int numFound = responseJson.getInt(NUMFOUND);
				if(numFound == 0){
					Log.log(String.format("Solr API NUMFOUND field error : numFound = %s", numFound), LogType.INFO);//For logging
					break; 
				}
				
				//get the field to object
				JSONArray docsArray = responseJson.getJSONArray(DOCS);
				if(docsArray == null || "".equals(docsArray.toString()) || "null".equals(docsArray.toString())){
					Log.log(String.format("Solr API DOCS field error : NULL"), LogType.ERROR);
					break; 
				}
				
				if(docsArray.length() == 0){
					Log.log(String.format("Solr API DOCS field error : size = %s", docsArray.length()), LogType.INFO);//For logging
					break; 
				}
				
				//convert JSON to List<VodDetail>
				for (int i = 0; i < docsArray.length(); i++) {
					JSONObject vodJson = docsArray.getJSONObject(i);
					json = parseVodJson(vodJson);
					vodDetailList.add(json);
				}
			} catch(Exception e) {
				json = null;
				Log.log(String.format("Solr API JSON Parse error : "), LogType.ERROR, e);
			}
		} while(false);
		long elapsedTime = (System.currentTimeMillis() - startTime);
		Log.endLog(String.format("vodDetailList: %s, startTime: %s, elapsedTime: %s, End", vodDetailList, startTime, elapsedTime));
		return vodDetailList;
	}
	
	public static List<VodDetail> getVodDetailListByNpvrId(String npvrId) {
		Log.startLog(String.format("npvrId: %s", npvrId));
		//String vodDetailUrl = "http://nowtv.now.com/nowtv-api/vodapi/queryProductForCheckout?nowtvapi_v=1.00&nowtvapi_key=NOWPLAYER_1.00&start=0&wt=json&q=product_npvr_id_t:"+npvrId+"&fl=" + SOLR_FILTER;
		List<VodDetail> vodDetailList = null;
		if(npvrId!=null&&!"".equals(npvrId)) {
			String vodDetailUrl = QUERY_NPVR + npvrId;
			Log.log(String.format("vodDetailUrl: %s", vodDetailUrl), LogType.INFO);
			vodDetailList = getVodDetailList(vodDetailUrl);
		}else {
			vodDetailList = new ArrayList<VodDetail>();
		}
		int vodDetailListSize = vodDetailList.size();
		Log.endLog(String.format("vodDetailListSize: %s", vodDetailListSize));
		return vodDetailList;
	}
	
	public static List<VodDetail> getVodDetailListByProductId(String productId) {
		Log.startLog(String.format("productId: %s", productId));
		//String vodDetailUrl = "http://nowtv.now.com/nowtv-api/vodapi/queryProductForCheckout?nowtvapi_v=1.00&nowtvapi_key=NOWPLAYER_1.00&rows=1&start=0&wt=json&q=product_id_t:"+productId;
		List<VodDetail> vodDetailList = null;
		if(productId!=null&&!"".equals(productId)) {
			String vodDetailUrl = QUERY_PRODUCT + productId;
			Log.log(String.format("vodDetailUrl: %s", vodDetailUrl), LogType.INFO);
			vodDetailList = getVodDetailList(vodDetailUrl);
		}else {
			vodDetailList = new ArrayList<VodDetail>();
		}
		int vodDetailListSize = vodDetailList.size();
		Log.endLog(String.format("vodDetailListSize: %s", vodDetailListSize));
		return vodDetailList;
	}
}
