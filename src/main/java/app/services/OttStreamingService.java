package app.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import app.utils.*;
import io.javalin.http.Context;

public class OttStreamingService {

	private final static int SESSION_ID_LENGTH = 32;
	private final static String MAIN_CLIP_URL_FORMAT = "%s/session/%s%s?token=%s_%s";
	private final static String MAIN_CLIP_URL_FORMAT_FOR_AKAMAI = "%s%s?%s";
	private final static String SERVER_CONFIG_DEFAULT="default";
	private final static String SECRET= Config.getInstance().getValue("stream.secret");

	public static final String CONTENT_SOURCE_OTT="OTT";
	public static final String CONTENT_SOURCE_HBO="HBO";
	public static final String CONTENT_SOURCE_YELLOW_VIU="YELLOW_VIU";
	public static final String AKAMAI_TOKEN_PREFIX = "AKAMAI_TOKEN";
	
	public static final String sessionFormat = Config.getInstance().getValue("stream.session.format");
	public static final String[] sessionArgs = Config.getInstance().getValue("stream.session.args").split(",");
	public static final String sessionFormatLogined = Config.getInstance().getValue("stream.session.loginedFormat");
	public static final String[] sessionArgsLogined = Config.getInstance().getValue("stream.session.loginedArgs").split(",");
	private static final String DISABLE_WEB_CHECKOUT_HTTPS = (String) Config.getInstance().getValue("WebCheckoutHttps.disable");
	public static final String[] VOS_CHANNEL = ((String)Config.getInstance().getValue("stream.vos.channel")).split(",");
	private static final boolean ENABLE_OVERFLOW= "true".equals(((String) Config.getInstance().getValue("Overflow.enable")))?true:false;
	private static final boolean AUTO_OVERFLOW= "true".equals(((String) Config.getInstance().getValue("AutoOverflow.enable")))?true:false;
	public static final String[] AKAMAI_CHANNEL = ((String)Config.getInstance().getValue("akamai.channel")).split(",");
	
	public static final HashMap<String, String> deviceTypeMappingLookupTable;
    static {
        deviceTypeMappingLookupTable = new HashMap<String, String>();
        deviceTypeMappingLookupTable.put("ANDROID_TV_NOWE","b");
        deviceTypeMappingLookupTable.put("ANDROID_TV","b");
        deviceTypeMappingLookupTable.put("ANDROID_PHONE","3");
        deviceTypeMappingLookupTable.put("ANDROID_TABLET","4");
        deviceTypeMappingLookupTable.put("IOS_PHONE","1");
        deviceTypeMappingLookupTable.put("IOS_TABLET","2");
        deviceTypeMappingLookupTable.put("WEB_ANDROID","3");
        deviceTypeMappingLookupTable.put("WEB_IPHONE","1");
        deviceTypeMappingLookupTable.put("WEB","5");
    }

    public static String formatSessionId(HashMap<String, String> sessionParams){
		
		String sessionId = null;
		ArrayList<String> argsValue = new ArrayList();
		String currentSessionIdFormat = null;
		String[] currentSessionIdArgs = null;
		if(sessionParams != null){
			if(sessionParams.get(Constants.CheckoutArgs.UID)!=null) {
				//Logined
				currentSessionIdFormat = sessionFormatLogined;
				currentSessionIdArgs = sessionArgsLogined;
			}else {
				//geust
				currentSessionIdFormat = sessionFormat;
				currentSessionIdArgs = sessionArgs;
			}
			Log.log("sessionFormat: " + currentSessionIdFormat + ", sessionIdArgs: " + currentSessionIdArgs, LogType.INFO);
			
			for (String sessionIdArg : currentSessionIdArgs) {
				Log.log("sessionIdArg Value: "  + sessionIdArg , LogType.INFO);
				if(sessionIdArg.equalsIgnoreCase(Constants.CheckoutArgs.APP_ID)) {
					argsValue.add(String.format("%2s", sessionParams.get(Constants.CheckoutArgs.APP_ID)).replace(' ','x'));
				}else if (sessionIdArg.equalsIgnoreCase(Constants.CheckoutArgs.UID)) {
					argsValue.add(String.format("%1$19s", sessionParams.get(Constants.CheckoutArgs.UID)).replace(' ','0'));
				}else if (sessionIdArg.equalsIgnoreCase(Constants.CheckoutArgs.MOBILE_ID)) {
					argsValue.add(String.format("%1$8s", sessionParams.get(Constants.CheckoutArgs.MOBILE_ID)).replace(' ','0'));
				}else if (sessionIdArg.equalsIgnoreCase(Constants.CheckoutArgs.CONN_MODE)) {
					argsValue.add(String.format("%1$1s", sessionParams.get(Constants.CheckoutArgs.CONN_MODE)).replace(' ','0'));
				}else if (sessionIdArg.equalsIgnoreCase(Constants.CheckoutArgs.DN)) {
					argsValue.add(String.format("%1$8s", sessionParams.get(Constants.CheckoutArgs.DN)).replace(' ','0'));
				}else if (sessionIdArg.equalsIgnoreCase(Constants.CheckoutArgs.DEVICE_ID)) {
					argsValue.add(String.format("%1$17s", sessionParams.get(Constants.CheckoutArgs.DEVICE_ID)).replace(' ','0'));
				}else if (sessionIdArg.equalsIgnoreCase(Constants.CheckoutArgs.USER_ID)) {
					argsValue.add(String.format("%1$10s", sessionParams.get(Constants.CheckoutArgs.USER_ID)).replace(' ','0'));
				}else if (sessionIdArg.equalsIgnoreCase(Constants.CheckoutArgs.GIFT_CODE)) {
					argsValue.add(sessionParams.get(Constants.CheckoutArgs.GIFT_CODE));
				}else if (sessionIdArg.equalsIgnoreCase(Constants.CheckoutArgs.DEVICE_TYPE)) {
					String deviceId = deviceTypeMapping(sessionParams.get(Constants.CheckoutArgs.DEVICE_TYPE));
					argsValue.add(deviceId);
				}else if (sessionIdArg.equalsIgnoreCase(Constants.CheckoutArgs.RANDOM)) {
					argsValue.add(Constants.CheckoutArgs.RANDOM.toUpperCase());
				}else {
					argsValue.add("");
				}
			}
			Log.log("Session arg Value: " + argsValue ,LogType.INFO );

			sessionId = String.format(currentSessionIdFormat, argsValue.toArray());
		}

		if (!sessionId.contains(Constants.CheckoutArgs.RANDOM.toUpperCase())){
			if (sessionId.length() > SESSION_ID_LENGTH) {
				sessionId = sessionId.substring(0, SESSION_ID_LENGTH - 1);
			}else if (sessionId.length() < SESSION_ID_LENGTH) {
				sessionId = sessionId + "-" + Constants.CheckoutArgs.RANDOM.toUpperCase();
			}
		}

		Log.endLog("sessionId: " + sessionId);
		return sessionId;
	}
	private static boolean isAkamai(String channelId, String contentType) {
		boolean result = false;
		do {
			if(!ENABLE_OVERFLOW) {
				break;
			}
			if(Constants.ContentType.CHANNEL.equals(contentType)) {
				for(int i=0;i<AKAMAI_CHANNEL.length;++i) {
					if(channelId.equals(AKAMAI_CHANNEL[i])) {
						result=true;
						break;
					}
				}
			}
		}while(false);
		return result;
	}
	private static boolean isAutoOverflow(String channelId, String contentType) {
		boolean result = false;
		do {
			if(!AUTO_OVERFLOW) {
				break;
			}
			if(Constants.ContentType.CHANNEL.equals(contentType)) {
				for(int i=0;i<AKAMAI_CHANNEL.length;++i) {
					if(channelId.equals(AKAMAI_CHANNEL[i])) {
						result=true;
						break;
					}
				}
			}
		}while(false);
		return result;
	}
	private static String getAkamaiChannelDomain(String channelNo) {
		String domain=null;
		domain = Config.getInstance().getValue("channelAkamai.domain.ch"+channelNo);
		if(domain==null) {
			domain = Config.getInstance().getValue("channelAkamai.domain.default");
		}
		return domain;
	}
	
	public static HashMap<String, String> generateAssetUrlObject(Context ctx, String ipAddr, String sessionId, HashMap<String, String> checkoutParams, String[] checkoutFallback) throws Exception {
		HashMap<String, String> returnObject = new HashMap<String, String>();
		boolean disableHttps=false;
		if("true".equals(DISABLE_WEB_CHECKOUT_HTTPS)) {
			Log.log("Disable Web HTTPS Checkout", LogType.INFO);
			disableHttps=true;
		}
		do {
			String ottUid =checkoutParams.get(Constants.CheckoutArgs.UID);
			String contentId=checkoutParams.get(Constants.CheckoutArgs.CONTENT_ID);
			String contentType=checkoutParams.get(Constants.CheckoutArgs.CONTENT_TYPE);
			String deviceId=checkoutParams.get(Constants.CheckoutArgs.DEVICE_ID);
			String deviceType=checkoutParams.get(Constants.CheckoutArgs.DEVICE_TYPE);
			String redownload=checkoutParams.get(Constants.CheckoutArgs.RE_DOWNLOAD);
			String contentSource = checkoutParams.get(Constants.CheckoutArgs.CONTENT_SOURCE);
			String classification = checkoutParams.get(Constants.CheckoutArgs.CLASSIFICATION);
			String channelNo = checkoutParams.get(Constants.CheckoutArgs.CHANNEL_NO);

			Log.log(String.format("ottUid:%s checkoutType:%s, contentId:%s, channelId:%s, ipAddr:%s", ottUid, contentType, contentId, channelNo, ipAddr), LogType.INFO);
			
			// Get Expiry Time of Streaming URL
			String expires = getTokenExpiryTime(contentType);
	
			// Main Clip
			String mainClipSessionId = null;
			String adAssetUrl = "";
	
			String streamingServer=null;
			if("true".equals(redownload)) {
				streamingServer=checkoutParams.get(Constants.CheckoutArgs.STREAMING_SERVER);
				mainClipSessionId=checkoutParams.get(Constants.CheckoutArgs.SESSION_ID);
				String assetOrigin=checkoutParams.get(Constants.CheckoutArgs.ORIGIN);
				String adToken = null;
				if(isAkamai(channelNo, contentType)) {
					if("WEB".equals(deviceType)) {
						adToken = AkamaiToken.genToken(ipAddr, assetOrigin.replace(AKAMAI_TOKEN_PREFIX, ""), true);
					}else {
						adToken = AkamaiToken.genToken(ipAddr, assetOrigin.replace(AKAMAI_TOKEN_PREFIX, ""), false);
					}
					adAssetUrl = String.format(MAIN_CLIP_URL_FORMAT_FOR_AKAMAI, streamingServer, assetOrigin.replace(AKAMAI_TOKEN_PREFIX, ""), adToken);
				}else {
					adToken = generateTokenByApp(assetOrigin, ipAddr, expires);
					adAssetUrl = String.format(MAIN_CLIP_URL_FORMAT, streamingServer, mainClipSessionId, assetOrigin, adToken, expires);
				}
				returnObject.put(JSONKey.ASSET, adAssetUrl);
				return returnObject;
			}
			
			//TODO: Remove later
			/*
			if("OTT".equals(checkoutParams.get(Constants.CheckoutArgs.CONTENT_SOURCE))) {//For live
				streamingServer = getStreamingServer(contentType, contentId);
			}else {
				streamingServer = getStreamingServer(contentType, checkoutParams.get(Constants.CheckoutArgs.CONTENT_SOURCE));
			}
			*/
			//TODO: Remove later
			
			mainClipSessionId = generateSesstionId(sessionId);
			String assetOrigin = null;
	/*
			if(contentType.equals("channel")){
				assetOrigin = getStreamingOrgin(contentType, contentId, checkoutParams);
			}else {
	*/
			//Change get different type from checkout params and call different API get URL eg:HBO,VIU,PWH
		//		assetOrigin=checkoutParams.get(Constants.CheckoutArgs.ASSET_PATH);
	//		}
				
			//checkType
			if(CONTENT_SOURCE_OTT.equals(contentSource)){
				//streamingURL = OttStreamingService.generateAssetUrlObject(contentType, contentId, clientIp, sessionId, checkoutParams);
				if(Constants.ContentType.STARTOVER.equals(contentType)) {

		        	String adOrigin = getVosStreamingOrgin(contentType, channelNo, checkoutFallback);
					//
		        	/*
		        	String adOrigin = (String)  Config.getInstance().getValue("startover.ch"+channelNo);
		        	if(adOrigin==null||"".equals(adOrigin)) {
		        		adOrigin = (String)  Config.getInstance().getValue("startover.default");
			        	adOrigin = adOrigin.replace("<channelNo>", checkoutParams.get(Constants.CheckoutArgs.CHANNEL_NO));
		        	}
		        	*/
		        	adOrigin = adOrigin.replace("[startOverTime]", checkoutParams.get(Constants.CheckoutArgs.STARTOVER_TIME));
		        	assetOrigin = adOrigin;
		        }else if(Constants.ContentType.CHANNEL.equals(contentType)) {
		        	assetOrigin = getVosStreamingOrgin(contentType, channelNo, checkoutFallback);
		        }else {
					AssetDetail assetDetail = VODCMSQuery.getAssetPath(contentId, contentType);//<----Change get from generateAssetUrlObject...
					if(assetDetail==null) {
						Log.log("DataGrid Asset Path API Return NULL", LogType.INFO);
						returnObject.put(JSONKey.ERROR, ResponseCode.PRODUCT_INFORMATION_INCOMPLETE.toString());
						break;
					}
					//Get releated Asset
					String assetPath=null;
					for(int i=0;i<checkoutFallback.length;++i) {
						assetPath = assetDetail.getAsset().get(checkoutFallback[i]);
						//Url in Object maybe NULL!
						if(assetPath!=null) {
							assetOrigin=assetPath;
							break;
						}
					}
				}
				Log.log("Asset Generation - adaptiveOrigin: " + assetOrigin, LogType.INFO);
				if(assetOrigin==null||"".equals(assetOrigin)) {
					returnObject.put(JSONKey.ERROR, ResponseCode.ASSET_MISSING.toString());
					break;
				}
				String adToken = null;
				//generateToken(adOrigin, ipAddr, expires, appId, region);
				
				boolean useAkamai=isAkamai(channelNo, contentType);
				if(!useAkamai) {
					boolean convoyFail = false;
					try {
						streamingServer = ConvoyService.getConvoyIp(ctx, assetOrigin, deviceType);
						Log.log("Convoy StreamingServer: " + streamingServer, LogType.INFO);
					}catch(Exception e) {
						Log.log("Convoy API fail:"+e, LogType.ERROR, e);
						convoyFail = true;
					}
					if(streamingServer==null) {
						Log.log("Convoy API return NULL:", LogType.ERROR);
						convoyFail = true;
					}
					if(convoyFail) {
						//check enable auto overflow
						if(isAutoOverflow(channelNo, contentType)){
							useAkamai=true;
						}else {
							returnObject.put(JSONKey.ERROR, ResponseCode.CONVOY_ERROR.toString());
							break;
						}
					}
				}
					
				if(useAkamai) {
					if(Constants.ContentType.STARTOVER.equals(contentType)) {
							Log.log("Use akamai URL", LogType.INFO);

							//Get Asset
				        	String akamaiUrl = Config.getInstance().getValue("startoverAkamai.ch"+channelNo);
							if(akamaiUrl==null) {
								returnObject.put(JSONKey.ERROR, ResponseCode.INVALID_SERVER_SIDE_APP_CONFIG.toString());
								break;
							}
							akamaiUrl = akamaiUrl.replace("<startOverTime>", checkoutParams.get(Constants.CheckoutArgs.STARTOVER_TIME));
							//Set into object
							returnObject.put(JSONKey.ASSET, akamaiUrl);
							break;
					}else if(Constants.ContentType.CHANNEL.equals(contentType)) {
						streamingServer = getAkamaiChannelDomain(channelNo);
						if("WEB".equals(deviceType)) {
							adToken = AkamaiToken.genToken(ipAddr, assetOrigin.replace(AKAMAI_TOKEN_PREFIX, ""), true);
						}else {
							adToken = AkamaiToken.genToken(ipAddr, assetOrigin.replace(AKAMAI_TOKEN_PREFIX, ""), false);
						}
						adAssetUrl = String.format(MAIN_CLIP_URL_FORMAT_FOR_AKAMAI, streamingServer, assetOrigin.replace(AKAMAI_TOKEN_PREFIX, ""), adToken, expires);
					}else {
						streamingServer = getStreamingServer(contentType, contentId);
						adToken = AkamaiToken.genToken(ipAddr, assetOrigin.replace(AKAMAI_TOKEN_PREFIX, ""), false);
						adAssetUrl = String.format(MAIN_CLIP_URL_FORMAT_FOR_AKAMAI, streamingServer, assetOrigin.replace(AKAMAI_TOKEN_PREFIX, ""), adToken, expires);
					}
				}else {
					adToken = generateTokenByApp(assetOrigin, ipAddr, expires);
					/*
					boolean convoyFail = false;
					try {
						streamingServer = ConvoyService.getConvoyIp(assetOrigin, deviceType);
						Log.log("Convoy StreamingServer: " + streamingServer, LogType.INFO);
					}catch(Exception e) {
						Log.log("Convoy API fail:"+e, LogType.ERROR, e);
						convoyFail = true;
					}
					if(streamingServer==null) {
						Log.log("Convoy API return NULL:", LogType.ERROR);
						convoyFail = true;
					}
					if(convoyFail) {
						//check enable auto overflow
						isAutoOverflow(channelNo, contentType){
							
						}
						returnObject.put(JSONKey.ERROR, ResponseCode.CONVOY_ERROR.toString());
						break;
					}
					*/
					adAssetUrl = String.format(MAIN_CLIP_URL_FORMAT, streamingServer, mainClipSessionId, assetOrigin, adToken, expires);
				}
				//Classification
				if(classification!=null) {
					//Classification
					String slate = classificationAssetMapping(classification);
					if(slate!=null) {
						//String slateServer = streamingServer = getStreamingServer("slate", "");
						String slateServer = null;
						try {
							slateServer = ConvoyService.getConvoyIp(ctx, assetOrigin, deviceType);
							Log.log("Convoy StreamingServer: " + slateServer, LogType.INFO);
						}catch(Exception e) {
							
						}
						String slateToken = generateTokenByApp(slate, ipAddr, expires);
						String slateSessionId = generateSesstionId(sessionId);
						String slateAssetUrl = String.format(MAIN_CLIP_URL_FORMAT, slateServer, slateSessionId, slate, slateToken, expires);
						returnObject.put(JSONKey.SLATE, slateAssetUrl);
					}
				}
			}else if(CONTENT_SOURCE_HBO.equals(contentSource)) {
				boolean isWeb = "WEB".equals(deviceType);
				//String hboUrl = HboService.getAssetUrl(contentId, contentType, isWeb);
				//adAssetUrl = hboUrl+"?token="+HboService.generateJwtToken(checkoutParams.get(Constants.CheckoutArgs.UID), checkoutParams.get(Constants.CheckoutArgs.EMAIL));
				
			}else if(CONTENT_SOURCE_YELLOW_VIU.equals(contentSource)) {
				assetOrigin=YellowViuService.getAssetUrl(contentId);
				Log.log("Asset Generation - adaptiveOrigin: " + assetOrigin, LogType.INFO);
				String adToken = generateTokenByApp(assetOrigin, ipAddr, expires);
				
				try {
					streamingServer = ConvoyService.getConvoyIp(ctx, assetOrigin, deviceType);
					Log.log("Convoy StreamingServer: " + streamingServer, LogType.INFO);
				}catch(Exception e) {
					Log.log("Convoy API fail:"+e, LogType.ERROR, e);
					returnObject.put(JSONKey.ERROR, ResponseCode.CONVOY_ERROR.toString());
					break;
				}
				if(streamingServer==null) {
					Log.log("Convoy API return NULL:", LogType.ERROR);
					returnObject.put(JSONKey.ERROR, ResponseCode.CONVOY_ERROR.toString());
					break;
				}
				
				adAssetUrl = String.format(MAIN_CLIP_URL_FORMAT, streamingServer, mainClipSessionId, assetOrigin, adToken, expires);
				HashMap<String, String> subtitleObj = VODCMSQuery.getYellowViuSubtitlePath(contentId, deviceType);
				String subtitle = subtitleObj.get(VODCMSQuery.SUBTITLE);
				String vast = subtitleObj.get(VODCMSQuery.PREROLL);
				String oldSubtitle = subtitleObj.get(VODCMSQuery.OLD_SUBTITLE);
				if(subtitle!=null) {
					returnObject.put(JSONKey.SUBTITLE, subtitle);
				}
				/*
				if(vast!=null) {
					returnObject.put(JSONKey.VAST, vast);
				}
				*/
				if(oldSubtitle!=null) {
					returnObject.put(JSONKey.OLD_SUBTITLE, oldSubtitle);
				}
			}
			
			Log.log("IS_DOWNLOAD:"+checkoutParams.get(Constants.CheckoutArgs.IS_DOWNLOAD), LogType.INFO);
			if("1".equals(checkoutParams.get(Constants.CheckoutArgs.IS_DOWNLOAD))&&!CONTENT_SOURCE_HBO.equals(contentSource)) {//Only HBO not do donwload at our side
				String ticket = ticketEncrypt(ottUid, streamingServer, mainClipSessionId, assetOrigin, contentId, contentType, deviceId, deviceType);
				returnObject.put(JSONKey.DOWNLOAD_TICKET, ticket);
			}
	
			returnObject.put(JSONKey.ASSET, adAssetUrl);
		}while(false);
		return returnObject;
	}

	private static boolean isVosChannel(String channelId) {
		boolean result = false;
		for(int i=0;i<VOS_CHANNEL.length;++i) {
			if(channelId.equals(VOS_CHANNEL[i])) {
				result=true;
				break;
			}
		}
		return result;
	}
	
	private static String getVosStreamingOrgin(String contentType, String contentId, String[] checkoutFallback){
		String assetPath=null;
		
		//check VOS....
		boolean isVos = isVosChannel(contentId);
		
		for(int i=0;i<checkoutFallback.length;++i) {
			//Check if not VOS then not check DASH/VOS_HLS 
			if(AssetDetail.DASH.equals(checkoutFallback[i])||AssetDetail.HLS_VOS.equals(checkoutFallback[i])) {
				if(!isVos) {
					continue;
				}
			}
			assetPath = getServerConfig(checkoutFallback[i], contentType, "origin", contentId);
			if(assetPath!=null) {
				break;
			}
		}
		if(assetPath!=null) {
			try{
				assetPath = assetPath.replace("[channelNo]", contentId);
			}catch(Exception e){
	
			}
		}

		return assetPath;
		
	}
	
	private static String getStreamingServer(String contentType, String contentId){
		return getStreamingServer(contentType, contentId, "");
	}
	
	private static String getStreamingServer(String contentType, String contentId, String deviceType){
		String streamType = "stream";
		if("WEB".equals(deviceType)) {
			streamType = "webStream";
		}
		String server = getServerConfig(streamType, contentType, "server", contentId);
		return server;
	}

	private static String getTokenExpiryTime(String contentType){
		Date expiryDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(expiryDate);
		String expireTime = getServerConfig("stream", contentType, "expire", null);
		cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(expireTime));
		expiryDate = cal.getTime();
		String expires = String.valueOf((expiryDate.getTime() / 1000));

		return expires;
	}

	private static String getServerConfig(String header, String contentType, String key, String contentId){
		String configValue = null;
		if(contentId==null){
			contentId=SERVER_CONFIG_DEFAULT;
		}
		String configKey=header+"."+contentType.toLowerCase()+"."+key+"."+contentId;
		Object value = Config.getInstance().getValue(configKey);
		if(value==null&&contentId!=null){
			//Get default value
			configKey=header+"."+contentType.toLowerCase()+"."+key+"."+SERVER_CONFIG_DEFAULT;
			value = Config.getInstance().getValue(configKey);
		}
		configValue=(String)value;
		Log.log("Config:"+configKey+" Value:"+configValue, LogType.INFO);
		return configValue;

	}


	///?????
	private static String generateSesstionId(String sessionId){
		Log.startLog();

		try {
			if(sessionId == null){
				sessionId = generateRandomHexString(SESSION_ID_LENGTH);
			}else if (sessionId.contains(Constants.CheckoutArgs.RANDOM.toUpperCase())){
				Log.log("Default Session Id Length: " + String.valueOf(SESSION_ID_LENGTH) ,LogType.INFO);
				Log.log("Input Session Id Length: " + String.valueOf(sessionId.length()) ,LogType.INFO);
				int randomLength = SESSION_ID_LENGTH - (sessionId.length() - Constants.CheckoutArgs.RANDOM.length());
				Log.log("Session randon Length: " + String.valueOf(randomLength) ,LogType.INFO);

				if(randomLength > 0){
					sessionId = sessionId.replace(Constants.CheckoutArgs.RANDOM.toUpperCase(), generateRandomHexString(randomLength));
				}else {
					sessionId = sessionId.replace(Constants.CheckoutArgs.RANDOM.toUpperCase(), "").substring(0, SESSION_ID_LENGTH - 1);
				}
			}
		} catch (Exception e) {
			Log.log("Error in Session ID Generation.", LogType.ERROR, e);
		}

		Log.endLog();
		return sessionId;
	}

	//This token correct?
	private static String generateTokenByApp(String assetOrigin, String IPaddr, String expires) throws NoSuchAlgorithmException {

		Log.startLog("assetOrigin: "+assetOrigin+", IPaddr: "+IPaddr+", expires: "+expires);
		//get last IP addr at IP list
		String ipAry[];
		String proxyStr;
		String token;
		Log.log("Start get the 'real' client IP address from the incoming IP address: "+ IPaddr, LogType.INFO);
		ipAry = IPaddr.split(",");
		if(ipAry.length>1){
			proxyStr = "The incoming IP address with PROXY: "+ ipAry[0];
			for(int i =1;i<ipAry.length;++i){
				proxyStr = proxyStr + " --> " + ipAry[i];
			}
			Log.log(proxyStr, LogType.INFO);
			IPaddr = ipAry[0].trim();
			Log.log("The 'real' client IP address: "+ IPaddr, LogType.INFO);
		}

		MessageDigest md = MessageDigest.getInstance("MD5");
		String secret = SECRET;
		String assetName = assetOrigin.substring(0, assetOrigin.lastIndexOf("/"));
		Log.log("assetName: " + assetName, LogType.INFO);
		String input = secret + assetName + IPaddr + expires;

		byte[] mdbytes = md.digest(input.getBytes());
		StringBuffer hexString = new StringBuffer();
		for (int i=0;i<mdbytes.length;i++) {
			String hex=Integer.toHexString(0xff & mdbytes[i]);
			if(hex.length()==1) hexString.append('0');
			hexString.append(hex);
		}
		token = hexString.toString();
		Log.endLog("token: "+token);
		return token;
	}

	public static String generateRandomHexString(int length) {
		Random random = new Random();
		String hexString = "";
		for (int i = 0; i < length; i++) {
			hexString += Integer.toHexString(random.nextInt(16));
		}
		return hexString;
	}
	
	
	private static String ticketEncrypt(String ottUid, String streamingServer, String mainClipSessionId, String assetOrigin, String contentId, String contentType, String deviceId, String deviceType) {
		String rawTicket = ottUid + "::" + streamingServer + "::" + mainClipSessionId + "::" + assetOrigin + "::" + contentId + "::" + contentType + "::" + deviceId + "::" + deviceType;
		Log.log("rawTicket:"+rawTicket, LogType.INFO);
    	String encryptedContent = "";
		Log.log("Content rawTicket : " + rawTicket, LogType.INFO);
    	SecretKey key = new SecretKeySpec(DatatypeConverter.parseHexBinary(Constants.DL_TICKET_AESKEY), "AES");
		Cipher cipher = null;
		byte[] result = null;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(DatatypeConverter.parseHexBinary(Constants.DL_TICKET_IV)));
			//result = cipher.doFinal(rawTicket.getBytes("UTF-8"));
			//encryptedContent = Base64.encodeBase64String(result);
			result = cipher.doFinal(rawTicket.getBytes("UTF-8"));
			encryptedContent = DatatypeConverter.printHexBinary(result);
			Log.log("Content encrypted : " + encryptedContent, LogType.INFO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.log("Error is found while generating the encryptedContent", LogType.ERROR, e);
		}
    	return encryptedContent;
    }
    
    
	public static HashMap<String, String> ticketDecrypt(String encryptTicket) {
		//				String rawTicket = ottUid + "::" + streamingServer + "::" + mainClipSessionId + "::" + assetOrigin + "::" + contentId + "::" + contentType;
		HashMap<String, String> ticketParams = null;
		do {
			String decryptedContent = "";
			SecretKey key = new SecretKeySpec(DatatypeConverter.parseHexBinary(Constants.DL_TICKET_AESKEY), "AES");
			Cipher cipher = null;
			byte[] result = null;
			try {
				cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(DatatypeConverter.parseHexBinary(Constants.DL_TICKET_IV)));
				result = cipher.doFinal(DatatypeConverter.parseHexBinary(encryptTicket));				
				decryptedContent = new String(result, "UTF-8");
				Log.log("Content decrypted : " + decryptedContent, LogType.INFO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.log("Error is found while generating the decryptedContent", LogType.ERROR, e);
				break;
			}

			String[] decryptedData = decryptedContent.split("::");
			Log.log("decryptedData String[] length = " + decryptedData.length , LogType.INFO);

			ticketParams = new HashMap<String, String>();

			if (decryptedData.length == 8){
				ticketParams.put(Constants.TicketArgs.OTT_UID, decryptedData[0]);
				ticketParams.put(Constants.TicketArgs.STREAMING_SERVER, decryptedData[1]);
				ticketParams.put(Constants.TicketArgs.SESSION_ID, decryptedData[2]);
				ticketParams.put(Constants.TicketArgs.ORIGIN, decryptedData[3]);
				ticketParams.put(Constants.TicketArgs.CONTENT_ID, decryptedData[4]);
				ticketParams.put(Constants.TicketArgs.CONTENT_TYPE, decryptedData[5]);
				ticketParams.put(Constants.TicketArgs.DEVICE_ID, decryptedData[6]);
				ticketParams.put(Constants.TicketArgs.DEVICE_TYPE, decryptedData[7]);
			}
		}while(false);

		Log.log("decryptedData ticketParams = " + ticketParams , LogType.INFO);
		return ticketParams;
    }
	
	public static String classificationAssetMapping(String classification) {
		//TODO Classification
		/*
			hls/slates/NPSVOD-I/NPSVOD-I.m3u8
			hls/slates/NPSVOD-IIA/NPSVOD-IIA.m3u8
			hls/slates/NPSVOD-IIB/NPSVOD-IIB.m3u8
			hls/slates/NPSVOD-III/NPSVOD-III.m3u8

		 */
		String asset = null;
		do {
			if(classification==null||"".equals(classification)) {
				break;
			}
			asset = "/hls/slates/NPSVOD-"+classification+"/NPSVOD-"+classification+".m3u8";
		}while(false);
		return asset;
	}
	

	public static String deviceTypeMapping(String deviceType) {
		String mappedType = deviceTypeMappingLookupTable.get(deviceType);
		if(mappedType==null) {
			mappedType = deviceTypeMappingLookupTable.get("WEB");
		}
		Log.log("deviceType:"+deviceType+" Session Mapped Device Type:"+mappedType, LogType.INFO);
		return mappedType;
	}
}
