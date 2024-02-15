package app.utils;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import com.maxmind.geoip.Country;
import com.maxmind.geoip.LookupService;
import kong.unirest.Unirest;

public class GeoChecker {
	private static String ipHubKey = (String) Config.getInstance().getValue("vpn.iphub.key");
	private static final String DISABLE_GEOCHECK = (String) Config.getInstance().getValue("GeoCheck.disable");
	private static final Pattern IPs = Pattern.compile((String) Config.getInstance().getValue("geoip.bypass"));
	private static LookupService ls = null;
	
	public static String ipLocate(String ip) throws Exception{
		String ipAry[];
		String proxyStr;
		if (ls == null) {
			try {
				//May change to external path
				ClassLoader classLoader = GeoChecker.class.getClassLoader();
				URL resource = classLoader.getResource("GeoIP.dat");
				ls =new LookupService(new File(resource.toURI()),LookupService.GEOIP_MEMORY_CACHE | LookupService.GEOIP_CHECK_CACHE);
			}catch (Exception e) {
				Log.log("Error in looking up the GeoIP dataFile", LogType.ERROR, e);
			}
		}
		Log.log("Start check the incoming IP: \""+ ip+"\"", LogType.INFO);
		ipAry = ip.split(",");
		if(ipAry.length>1){
			proxyStr = "The incoming IP with PROXY: \""+ ipAry[0] +"\"";
			for(int i =1;i<ipAry.length;++i){
				proxyStr = proxyStr + " --> \"" + ipAry[i]+"\"";
			}
			Log.log(proxyStr, LogType.INFO);
		}
		if(ipAry.length>1){
			ip=ipAry[0].trim();
		}
		Country c = null;
		c = ls.getCountry(ip);
		if(c == null){
			//any problem, return Oversea..
			Log.log("The incoming IP: \""+ ip+"\"is Unknown.", LogType.INFO);
			return ("OO");
		}else{
			Log.log("The incoming IP: \""+ ip+"\"is (" + c.getCode() + ").", LogType.INFO);
			return c.getCode();
		}
		
	}
	
	public static boolean isHKIP(String ip) throws Exception{
		String ipLocate = ipLocate(ip);
		boolean result = false;
		if ("HK".equals(ipLocate)){
			result = true;
		}else {
			Log.log("The incoming IP: \""+ ip+"\" is NOT HK IP (" + ipLocate + ").", LogType.INFO);
		}
		return result;  
	}
	/*
	public static String ipLocateByConfig(String appId, String inAddr){
		Log.startLog(String.format("appId: %s, inAddr: %s", appId, inAddr));
		if(Play.configuration.get("ForceOverseaLocation") != null && !((String)Play.configuration.get("ForceOverseaLocation")).equals("") && !((String)Play.configuration.get("ForceOverseaLocation")).equals("null")){
			Log.log("Force Oversea! : " + (String)Play.configuration.get("ForceOverseaLocation"));
			return ((String)Play.configuration.get("ForceOverseaLocation"));
			
		}
		String isGeoCheck = "HK";
		//Pattern IPs = Pattern.compile((String)Play.configuration.get("geoip.bypass"));
		Pattern IPs = Pattern.compile((String)Play.configuration.get("geoip.bypass"));
		//String geoipMode = Play.configuration.getProperty("geoip.mode");
		String geoipMode = (String)Play.configuration.get("geoip.mode");
		Log.log(String.format("geoipMode: %s", geoipMode), LogType.INFO);
		if ("FORCE_OVERSEAS".equals(geoipMode)) {
			isGeoCheck = "OO";
		} else if ("NORMAL".equals(geoipMode)) {
			if (!IPs.matcher(inAddr).find()) {
				try {
					String userIpLocate = ipLocate(inAddr);
					String locateList[] = CacheUtils.appInfoCache(appId, "REGION_LIST").split(",");
					isGeoCheck = "OO";
					for(int i = 0; i < locateList.length; ++i){
						if(userIpLocate.equals(locateList[i])){
							Log.log("Incoming User locate is in the list : " + userIpLocate+inAddr, LogType.INFO);
							isGeoCheck = userIpLocate;
							break;
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.log("Error found in getLocate, IP: "+inAddr, LogType.ERROR, e);
					isGeoCheck = "OO";
				}
			}else{
				Log.log("IP in ignore List : " + inAddr, LogType.INFO);
			}
		}
		Log.startLog(String.format("isGeoCheck: %s", isGeoCheck));
		return isGeoCheck;
	}
	
	public static String ipLocateByConfig(String appId, String inAddr, String serverReferenceNo, String callerReferenceNo){
		if(Play.configuration.get("ForceOverseaLocation") != null && !((String)Play.configuration.get("ForceOverseaLocation")).equals("") && !((String)Play.configuration.get("ForceOverseaLocation")).equals("null")){
			Log.log("Force Oversea! : " + (String)Play.configuration.get("ForceOverseaLocation"));
			return ((String)Play.configuration.get("ForceOverseaLocation"));
			
		}
		String isGeoCheck = "HK";
		//Pattern IPs = Pattern.compile((String)Play.configuration.get("geoip.bypass"));
		Pattern IPs = Pattern.compile((String)Play.configuration.get("geoip.bypass"));
		Log.log("geoCheck inAddr, IP: "+inAddr);
		//String geoipMode = Play.configuration.getProperty("geoip.mode");
		String geoipMode = (String)Play.configuration.get("geoip.mode");
		if ("FORCE_OVERSEAS".equals(geoipMode)) {
			Log.log("Force to Oversea");
			isGeoCheck = "OO";
		} else if ("NORMAL".equals(geoipMode)) {
			if (!IPs.matcher(inAddr).find()) {
				try {
					String userIpLocate = ipLocate(inAddr);
					String locateList[] = CacheUtils.appInfoCache(appId, "REGION_LIST", serverReferenceNo, callerReferenceNo).split(",");
					isGeoCheck = "OO";
					for(int i = 0; i < locateList.length; ++i){
						if(userIpLocate.equals(locateList[i])){
							Log.log("Incoming User locate is in the list : " + userIpLocate);
							isGeoCheck = userIpLocate;
							break;
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.log("Error found in getLocate, IP: "+inAddr, e);
					isGeoCheck = "OO";
				}
			}else{
				Log.log("IP in ignore List : " + inAddr);
			}
		}
		return isGeoCheck;
	}
	
	public static String getRegion(String ipAddr, String appId, String mode, boolean removeRegionPrefix) {
		Log.startLog(String.format("ipAddr: %s, appId: %s, mode: %s, removeRegionPrefix: %s", ipAddr, appId, mode, removeRegionPrefix));
		String region = "";
		if (Constants.MODE_REVIEW.equals(mode)) {
			region = "REGION1";
		} else {
			region = CacheUtils.regionMappingCache(appId, ipLocateByConfig(appId, ipAddr));
		}
		if (removeRegionPrefix) {
			region = region.replaceAll("REGION", "");
		}
		Log.endLog(String.format("ipAddr: %s, appId: %s, mode: %s, removeRegionPrefix: %s, region: %s", ipAddr, appId, mode, removeRegionPrefix, region));
		return region;
	}
	*/
	
	public static boolean isVPN(String ip) {
		return isVpnIpHub(ip);
	}
	
	private static boolean isVpnIpHub(String ip) {
		//If API error, Just return it is not VPN
		long startTime = System.currentTimeMillis();
		boolean isVpn=false;
		
		String[] ipAry = ip.split(",");
		if(ipAry.length>1){
			ip=ipAry[0].trim();
		}
		
		HashMap<String, String> httpHeader = new HashMap<String, String>();
		httpHeader.put("X-Key", ipHubKey);
		try {
			/*
			curl http://v2.api.iphub.info/ip/8.8.8.8 -H "X-Key: 123"
			{
				"ip": "8.8.8.8",
				"countryCode": "US",
				"countryName": "United States",
				"asn": 15169,
				"isp": "GOOGLE - Google Inc.",
				"block": 1
			}
			
			*/
			JsonObject ipHubJson = Unirest.get("http://v2.api.iphub.info/ip/"+ip).asObject(JsonObject.class).getBody();
			String ipResult =  ipHubJson.get("ip").getAsString();
			String countryCode =  ipHubJson.get("countryCode").getAsString();
			String countryName =  ipHubJson.get("countryName").getAsString();
			String asn =  ipHubJson.get("asn").getAsString();
			String isp =  ipHubJson.get("isp").getAsString();
			int block =  ipHubJson.get("block").getAsInt();
			Log.log("IP Request:"+ip+" Result:"+ipResult, LogType.INFO);
			Log.log("CountryCode:"+countryCode, LogType.INFO);
			Log.log("CountryName:"+countryName, LogType.INFO);
			Log.log("asn:"+asn, LogType.INFO);
			Log.log("isp:"+isp, LogType.INFO);
			Log.log("block:"+block, LogType.INFO);
			if(block==1) {
				isVpn=true;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.log("IPHub error:"+e, LogType.ERROR, e);
		}
		long endTime = System.currentTimeMillis();

		Log.log("IPHub elapsedTime:"+(endTime - startTime), LogType.INFO);
		return isVpn;
	}
	
	public static String checkoutIPChecking(String clientIp) {
		String responseCode = null;
		do {
			//Geo Checking
			if("true".equals(DISABLE_GEOCHECK)) {
				Log.log("Disable GeoCheck", LogType.INFO);
				break;
			}
			try {
				if (IPs.matcher(clientIp).find()) {
					Log.log("IP:"+clientIp+" in Whitelist", LogType.INFO);
					break;
				}
			}catch(Exception e) {
			}
				
			try {
				if(!isHKIP(clientIp)) {
					Log.log("IP:"+clientIp+" not HK IP", LogType.INFO);
					responseCode = ResponseCode.GEO_CHECK_FAIL.toString();
					break;
				}
			}catch(Exception e) {
				Log.log("GeoCheck API Exception:"+e, LogType.ERROR, e);
				responseCode = ResponseCode.INTERNAL_ERROR.toString();
				break;
			}
			/*
			if(isVPN(clientIp)){
				Log.log("Seems IP:"+clientIp+" is VPN:", LogType.INFO);
				responseCode = ResponseCode.GEO_CHECK_FAIL.toString();
				break;
			}
			*/
		}while(false);
		return responseCode;
	}
}