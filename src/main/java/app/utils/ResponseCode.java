package app.utils;

public enum ResponseCode {
	SUCCESS("SUCCESS"),
	CONVOY_ERROR("CONVOY_ERROR"),
	INVALID_SERVER_SIDE_APP_CONFIG("INVALID_SERVER_SIDE_APP_CONFIG"),

	//TnC
	TNC_NOT_ACCEPTED("TNC_NOT_ACCEPTED", Constants.FUNC_TNC),
	
	//General
	MISSING_INPUT("MISSING_INPUT", Constants.FUNC_ERROR, Constants.GENERAL_ERROR),
	ASSET_FORMAT_INCORRECT("ASSET_FORMAT_INCORRECT", Constants.FUNC_ERROR, Constants.GENERAL_ERROR),
	GENERATE_TOKEN_ERROR("GENERATE_TOKEN_ERROR", Constants.FUNC_ERROR, Constants.GENERAL_ERROR),
	INTERNAL_ERROR("INTERNAL_ERROR", Constants.FUNC_ERROR, Constants.GENERAL_ERROR),
	VERIMATRIX_ERROR("VERIMATRIX_ERROR"),
	CMT_ERROR("CMT_ERROR"),
	ASSET_MISSING("ASSET_MISSING"),
	UNSUPPORT_MOBILE("UNSUPPORT_MOBILE"),
	NOT_VEREGISTERED_DEVICE("NOT_VEREGISTERED_DEVICE"),
	
	//Login
	NEED_LOGIN("NEED_LOGIN", Constants.FUNC_LOGIN),
	
	//Maintenance Mode
	SYSTEM_MAINTENANCE("SYSTEM_MAINTENANCE", Constants.FUNC_ERROR, "SYSTEM_MAINTENANCE"),
		
	//Subscription
	NOT_LOGIN("NOT_LOGIN", Constants.FUNC_ERROR, "NOT_LOGIN"),
	NOT_ACTIVATED("NOT_ACTIVATED", Constants.FUNC_ERROR, "NOT_ACTIVATED"),
	GEO_CHECK_FAIL("GEO_CHECK_FAIL", Constants.FUNC_ERROR, "GEO_CHECK_FAIL"),
	BINDING_NOT_FOUND("BINDING_NOT_FOUND", Constants.FUNC_ERROR, "BINDING_NOT_FOUND"),
	NEED_SUB("NEED_SUB", Constants.FUNC_ERROR, "NEED_SUB"),
	PRODUCT_INFORMATION_INCOMPLETE("PRODUCT_INFORMATION_INCOMPLETE", Constants.FUNC_ERROR, "PRODUCT_INFORMATION_INCOMPLETE"),
	
	//Device Conrtol
	FAIL("FAIL"),
	ACCOUNT_NOT_REGISTER("ACCOUNT_NOT_REGISTER", Constants.FUNC_DEVICE_CONTROL),
	DEVICE_NOT_REGISTER("DEVICE_NOT_REGISTER", Constants.FUNC_DEVICE_CONTROL),
	INVALID_DEVICE("INVALID_DEVICE", Constants.FUNC_ERROR, "INVALID_DEVICE"),
	MISSING_DEVICE_ID("MISSING_DEVICE_ID", Constants.FUNC_DEVICE_CONTROL),
	//Device Control ResponseCode From API
	DUPLICATE_DEVICE_NAME("DUPLICATE_DEVICE_NAME"),
	RECORD_EXIST("RECORD_EXIST"),
	NO_DEVICE_LIMIT("NO_DEVICE_LIMIT"),
	RECORD_NOT_FOUND("RECORD_NOT_FOUND"),
	SERVICE_NOT_FOUND("SERVICE_NOT_FOUND"),
	//Device Control Custom Error Code
	WRONG_DEVICE_NAME_PATTERN("WRONG_DEVICE_NAME_PATTERN"),
	
	//Parental Lock
	FIRST_TIME_SETUP("FIRST_TIME_SETUP", Constants.FUNC_PL_FIRST_SETUP),
	INVALID_PIN("INVALID_PIN", Constants.FUNC_PL_INVALID_PIN),
	INVALID_FSA("INVALID_FSA", Constants.FUNC_ERROR),
	NO_PIN("NO_PIN", Constants.FUNC_PL_NO_PIN),
	
	//nowID v2
	INVALID_PROFILE_TYPE("INVALID_PROFILE_TYPE", Constants.FUNC_ERROR, Constants.GENERAL_ERROR),
	
	//Error code
	COMING_SOON("comingsoon"),

	//CDNS
	COMMIT_ALREADY("COMMIT_ALREADY"),
	NO_OVERFLOW("NO_OVERFLOW"),
	CONFIG_FILE_OVERFLOW("CONFIG_FILE_OVERFLOW"),
	CDNS_OVERFLOW("CDNS_OVERFLOW"),
	RECORD_NOT_FOUND_IN_CMS("RECORD_NOT_FOUND_IN_CMS"),

	//MUP
	PROFILE_NOT_FOUND("PROFILE_NOT_FOUND");
	
	public String responseCode;
	public String funcName;
	public String errorCode;

	ResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getErrorMessage() {
		return this.responseCode;
	}

	@Override
	public String toString() {
		return this.responseCode;
	}

	ResponseCode(String responseCode, String funcName) {
		this.responseCode = responseCode;
		this.funcName = funcName;
	}

	ResponseCode(String responseCode, String funcName, String errorCode) {
		this.responseCode = responseCode;
		this.funcName = funcName;
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode
	 *            the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @param funcName
	 *            the funcName to set
	 */
	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	/**
	 * @return the funcName
	 */
	public String getFuncName() {
		return funcName;
	}
}
