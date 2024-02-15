package app.utils;

import java.util.HashMap;

public class AssetDetail {

	public static String HLS_PMO = "HLS_PMO";
	public static String HLS_VOS = "HLS_VOS";
	public static String SS = "SS";
	public static String DASH = "DASH";
	
	public HashMap<String, String> Asset=new HashMap<String, String>();
	public String pid=null;
	public String type=null;
	
	
	
	@Override
	public String toString() {
		return "AssetDetail [Asset=" + Asset + ", pid=" + pid + ", type=" + type + "]";
	}
	
	public HashMap<String, String> getAsset() {
		return Asset;
	}
	public void setAsset(HashMap<String, String> asset) {
		Asset = asset;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
