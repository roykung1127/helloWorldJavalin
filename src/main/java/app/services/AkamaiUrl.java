package app.services;

import app.utils.*;

//import static app.utils.StreamingUtils.*;

public class AkamaiUrl {

//    public static String generateLiveAsset() {
//        if(Constants.ContentType.STARTOVER.equals(contentType)) {
//            Log.log("Use akamai URL", LogType.INFO);
//
//            //Get Asset
//            String akamaiUrl = Config.getInstance().getValue("startoverAkamai.ch"+channelNo);
//            if(akamaiUrl==null) {
//                returnObject.put(JSONKey.ERROR, ResponseCode.INVALID_SERVER_SIDE_APP_CONFIG.toString());
//                break;
//            }
//            akamaiUrl = akamaiUrl.replace("<startOverTime>", checkoutParams.get(Constants.CheckoutArgs.STARTOVER_TIME));
//            //Set into object
//            returnObject.put(JSONKey.ASSET, akamaiUrl);
//            break;
//        }else if(Constants.ContentType.CHANNEL.equals(contentType)) {
//            streamingServer = getAkamaiChannelDomain(channelNo);
//            if("WEB".equals(deviceType)) {
//                adToken = AkamaiToken.genToken(ipAddr, assetOrigin.replace(AKAMAI_TOKEN_PREFIX, ""), true);
//            }else {
//                adToken = AkamaiToken.genToken(ipAddr, assetOrigin.replace(AKAMAI_TOKEN_PREFIX, ""), false);
//            }
//            adAssetUrl = String.format(MAIN_CLIP_URL_FORMAT_FOR_AKAMAI, streamingServer, assetOrigin.replace(AKAMAI_TOKEN_PREFIX, ""), adToken, expires);
//        }else {
//            streamingServer = getStreamingServer(contentType, contentId);
//            adToken = AkamaiToken.genToken(ipAddr, assetOrigin.replace(AKAMAI_TOKEN_PREFIX, ""), false);
//            adAssetUrl = String.format(MAIN_CLIP_URL_FORMAT_FOR_AKAMAI, streamingServer, assetOrigin.replace(AKAMAI_TOKEN_PREFIX, ""), adToken, expires);
//        }
//    }
//
//    public static String generateVodAssest() {
//        String expires = getTokenExpiryTime(contentType);
//
//        return "";
//    }
//
//    public static String generateStale() {
//        return "";
//    }

}
