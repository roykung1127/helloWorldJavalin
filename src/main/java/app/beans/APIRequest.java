package app.beans;

import com.google.gson.Gson;

public class APIRequest {
	public String callerReferenceNo;
	public String clientIp;
	public final String platform = "Tenet-web";

	public APIRequest(String callerReferenceNo) {
		this.callerReferenceNo = callerReferenceNo;
	}
	
	public APIRequest(String callerReferenceNo, String clientIp) {
		this.callerReferenceNo = callerReferenceNo;
		this.clientIp = clientIp;
	}

	public String getCallerReferenceNo() {
		return callerReferenceNo;
	}
	public void setCallerReferenceNo(String callerReferenceNo) {
		this.callerReferenceNo = callerReferenceNo;
	}
	
	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	
	public String getPlatform() {
		return platform;
	}

	@Override
	public String toString() {
		return "APIRequest [callerReferenceNo=" + callerReferenceNo
				+ ", clientIp=" + clientIp + ", platform=" + platform + "]";
	}

	public String toJson(){
		return new Gson().toJson(this);
	}
	
}
