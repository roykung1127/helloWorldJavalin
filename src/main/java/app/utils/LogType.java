package app.utils;

public enum LogType {
	INFO("INFO"),
	ERROR("ERROR"),
	DEBUG("DEBUG")
	;
	
	public String logTypeCode;
	
	LogType(String logTypeCode) {
		this.logTypeCode=logTypeCode;
	}
	
	public String getLogType(){
		return this.logTypeCode;
	}
	
}