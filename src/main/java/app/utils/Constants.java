package app.utils;

import java.util.TimeZone;

public class Constants {
	
	public static final TimeZone TIME_ZONE_HK = TimeZone.getTimeZone("GMT+8");

	// Client side function name
	public static final String FUNC_ERROR = "showErrorPrompt(data)";
	public static final String FUNC_DEVICE_CONTROL = "deviceControl(data)";
	public static final String FUNC_PL_FIRST_SETUP = "parentalLockSetup(data)";
	public static final String FUNC_PL_NO_PIN = "parentalLockCheckPin(data)";
	public static final String FUNC_PL_INVALID_PIN = "parentalLockInvalidPin(data)";
	public static final String FUNC_LOGIN = "login(closeLoginCB)";
	public static final String FUNC_TNC = "tncPrompt()";

	// Error code
	public static final String GENERAL_ERROR = "playerror";

	// Request/Response
	public static final String SERVER_REFERENCE_NO = "serverReferenceNo";
	public static final String CALLER_REFERENCE_NO = "callerReferenceNo";

	// format
	public static final String SS = "SS";
	public static final String HLS = "HLS";

	// action
	public static final String ACTION_PURCHASED = "purchased";
	public static final String ACTION_PLAYED = "played";

	public final class CheckoutArgs{
		// Session ID Args
		public final static String APP_ID = "appId";
		public final static String NP_ID = "npId";
		public final static String FSA = "fsa";
		public final static String MOBILE_ID = "mobileId";
		public final static String CONN_MODE = "connMode"; //for eye
		public final static String DN = "dn"; //for eye
		public final static String DEVICE_ID = "deviceId"; // Appa2, a3 (SmartTV)
		public final static String USER_ID = "userId"; //P6, P7 (OTTDrama)
		public final static String RANDOM = "random";
		public final static String GIFT_CODE = "giftCode";

		public final static String CHANNEL_NO = "channelNo"; //Live
		public final static String CONTENT_ID = "contentId"; //Generic VOD
		//	public final static String PRODUCT_ID = "productId"; //Generic VOD
		public final static String CP_ID = "cpId"; //Generic VOD
		public final static String NPVR_ID = "npvrId"; //VOD -- NPVR
		public final static String BITRATE_LAYER = "bitrateLayer"; //for Fix
		//		public final static String ASSET_ID = "assetId"; //VOD -- NPVR
		public static final String CLASSIFICATION = "classification"; //Slate_VOD
		public static final String DATA_ID = "dataId"; // Target Video use
		public static final String SLATE_FILE_NAME = "slateFileName"; //slate file name
		public static final String ASSET_PATH = "assetPath";
		public static final String CONTENT_TYPE = "contentType";
		public static final String CONTENT_SOURCE = "contentSource";
		public static final String UID = "uid";
		public static final String EMAIL = "email";
		public static final String DEVICE_TYPE = "deviceType";
		public static final String IS_DOWNLOAD = "isDownload";
		public static final String RE_DOWNLOAD = "reDownload";
		public final static String STREAMING_SERVER = "streamingServer";
		public final static String ORIGIN = "origin";
		public final static String SESSION_ID = "sessionId";

		public final static String STARTOVER_TIME = "startOverTime";
		public CheckoutArgs() { }
	}

	public final class ContentType{
		public static final String CHANNEL = "Channel";
		public static final String FEATURE = "Feature";
		public static final String NPVR = "NPVR";
		public static final String SVOD = "SVOD";
		public static final String VOD = "VOD";
		public static final String PPV = "PPV";
		public static final String PPS = "PPS";
		public static final String STARTOVER = "STARTOVER";
		public ContentType() {}
	}

	public final class OttApiContentType{
		public static final String CHANNEL = "Channel";
		public static final String VOD_LIBRARY = "VODLibrary";
		public static final String SERIES = "Series";
		public static final String PRODUCT = "Product";
		public static final String PPV = "PPV";
		public static final String PPS = "PPS";
		public static final String SERVICE_NAME = "ServiceName";
		public static final String CONTENT_GROUP = "ContentGroup";
		public static final String NPVR = "Npvr";
		public OttApiContentType() {}
	}

	public final class TicketArgs{
		public final static String OTT_UID = "ottUid";
		public final static String SESSION_ID = "sessionId";
		public final static String CONTENT_ID = "contentId";
		public final static String ASSET_FORMAT = "assetFormat";
		public final static String CONTENT_TYPE = "contentType";
		public final static String Download_IP = "downloadIp";
		public final static String REMOVE_SLATE = "removeSlate";
		public final static String CLASSIFICATION = "classification";
		public final static String STREAMING_SERVER = "streamingServer";
		public final static String ORIGIN = "origin";
		public final static String DEVICE_ID = "deviceId";
		public static final String DEVICE_TYPE = "deviceType";

		public TicketArgs() { }
	}


	public final class SessionIdKey {
		public final static String SESSION_GUEST = "sessionGuest";
		public final static String SESSION_TV_BINDED = "sessionTvBinded";
		public final static String SESSION_NP_ID = "sessionNpId";
		public final static String SESSION_CSL = "sessionCsl";
		public final static String SESSION_CSL_BINDED = "sessionCslBinded";
		public final static String SESSION_EYE = "sessionEye";
		public final static String SESSION_GIFT_CODE = "sessionGiftCode";
		public final static String SESSION_USER_ID = "sessionUserId";

		public SessionIdKey() {}
	}

	// For VIM display the field "Done By" in "Device Control History"
	public static final String userName = "user";

	// For GetAllDevice the field "action"
	public static final int INACTIVE = 0;
	public static final int ACTIVE = 1;

	//NPX Concurrent Control

	public final static String CONCURRENT_REQEST_TYPE_STREAMING = "s";
	public final static String CONCURRENT_REQEST_TYPE_DOWNLOAD = "d";
	public final static String DL_TICKET_AESKEY = "322ab45832f15584e5fd2877cab4e488";//md5(npxContentDownload)
	public final static String DL_TICKET_IV = "7abfa07d126440a248583bade7c86153";//md5(pccwNowTvNPX)



}
