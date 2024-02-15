package app.utils;

import java.util.HashMap;
import java.util.Map;

public class ErrorCode {
	
	public static enum ErrorList {
		SUCCESS(0),
		NETWORK(1), 
		PARSE(2),
		API_ERROR(3),
		NEED_NOWID(4),
		NEED_FSA(5),
		API_NOT_FOUND(6),
		CONTENTQPA_ERROR(100),
		PVR_API_ERROR(101),
		RECOMMENDATION_ENGINE_ERROR(102),
		U3API_ERROR(103),
		UPS_ERROR(104),
		MUPAPI_ERROR(105),
		CONTENTWISE_API_ERROR(99);

	    public int errId;
	    public static Map<Integer, ErrorList> map = new HashMap<Integer, ErrorList>();
	    
	    
	    ErrorList(final int errId) {
	    	this.errId = errId; 
	    }
	    
	    static {
	        for (ErrorList errEnum : ErrorList.values()) {
	            map.put(errEnum.errId, errEnum);
	        }
	    }
	    
	    public int getValue() {
	        return errId;
	    }
	    
	    public static ErrorList valueOf(int errId) {
	        return map.get(errId);
	    }
	    
	    // if greater than 0 means have error, default no error
 	    public static Integer msgToErrorCode(String errMsg) {
 	    	Log.info("response to msg: " + errMsg);
	    	for (Integer name: map.keySet()){
	    		if (errMsg.equals(map.get(name).toString())) {
	    			return name;
	    		}
	    	} 
	    	return -1;
	    }
	}
	
}
