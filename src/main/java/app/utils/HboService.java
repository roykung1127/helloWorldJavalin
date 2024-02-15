package app.utils;

//import app.main.MainApp;
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.auth0.jwt.exceptions.JWTCreationException;
//import com.auth0.jwt.interfaces.DecodedJWT;
//import org.apache.commons.lang.StringUtils;
//import org.apache.logging.log4j.core.config.ConfigurationSource;
//import org.apache.logging.log4j.core.config.Configurator;
//
//import javax.xml.bind.DatatypeConverter;
//import java.io.InputStream;
//import java.security.MessageDigest;
//import java.security.interfaces.ECPrivateKey;
//import java.security.interfaces.ECPublicKey;
//import java.util.Calendar;
//import java.util.Date;


public class HboService {
//	public static final String HBO_CONTENT_PREFIX="HBO";
//	public static final String LIB_ID="L50001";
//	public static final String HBO_AUD="HBO_NOWTV_SDK";
//	public static final String HBO_ISSUER = "HBO_HK";
//
//	public static boolean isHboContent(String contentId){
//		boolean result = false;
//		do{
//			if(StringUtils.isEmpty(contentId)){
//				break;
//			}
//			if(contentId.startsWith(HBO_CONTENT_PREFIX)){
//				result=true;
//			}
//		}while(false);
//		Log.log("isHboContent: " + contentId + "=" + result, LogType.INFO);
//		return result;
//	}
//
//
//	public static Algorithm _ECDSA256_ALGORITHM = null;
//
//	public static synchronized Algorithm getHboAlgorithm() {
//		//ECDSA256
//		/*
//		 * https://github.com/auth0/java-jwt
//		Generate EC KEY for HBO as follow:
//		openssl ecparam -genkey -name prime256v1 -noout -out ec256-key-pair.pem
//		openssl ec -in ec256-key-pair.pem -pubout -out ec_public.pem
//		openssl pkcs8 -topk8 -nocrypt -in ec256-key-pair.pem -out p8file.pem
//		 */
//
//		ClassLoader classLoader = HboService.class.getClassLoader();
//		InputStream inPubKey = classLoader.getResourceAsStream("keystore/ec_public.pem");
//		InputStream inPriKey = classLoader.getResourceAsStream("keystore/ec256-key-pair.pem");
//
//		if (_ECDSA256_ALGORITHM == null) {
//			try{
//				_ECDSA256_ALGORITHM = Algorithm.ECDSA256((ECPublicKey) PemUtils.getPublicKey(inPubKey.readAllBytes(),"EC"), (ECPrivateKey) PemUtils.getPublicKey(inPriKey.readAllBytes(),"EC"));
//				//_ECDSA256_ALGORITHM = Algorithm.ECDSA256((ECPublicKey) PemUtils.readPublicKeyFromFile("conf/ec_public.pem", "EC"), (ECPrivateKey) PemUtils.readPublicKeyFromFile("conf/ec256-key-pair.pem", "EC"));
//			}catch(Exception e){
//				Log.log("Error when get Public/public Key for HBO : " + e, LogType.ERROR, e);
//			}
//		}
//		return _ECDSA256_ALGORITHM;
//	}
//
//	public static String generateJwtToken(String id, String email){
//		String token = null;
//		int expire=30*24;//(30 days)
//		/*
//		email= "nowtvguest@brightcove.com";
//		id="20171031173250063";
//		 */
//		do{
//			Log.log("Generate JWT Token Input:ID="+id+" Email="+email+" expire="+expire, LogType.INFO);
//
//			try {
//				Date currentTime = new Date();
//				Date expireTime = null;
//				Calendar cal = Calendar.getInstance();
//				cal.setTime(currentTime);
//				cal.add(Calendar.HOUR_OF_DAY,expire);
//				expireTime=cal.getTime();
//
//				if(StringUtils.isEmpty(id)||StringUtils.isEmpty(email)) {
//				   token = JWT.create()
//					        .withIssuer(HBO_ISSUER)
//					        .withAudience(HBO_AUD)
//					        .withExpiresAt(expireTime)
//					        .withIssuedAt(currentTime)
//					        .withNotBefore(currentTime)
//					        .withClaim("country", "HK")
//					        .withClaim("operatorName", "nowe")
//					        .withClaim("operatorId", "NOWE")
//					        .sign(getHboAlgorithm());
//				   Log.log("HBO JWT Token Issuer:"+HBO_ISSUER+" Audience="+HBO_AUD+" ExpiresAt="+expireTime+ " IssuedAt="+currentTime+" NotBefore="+currentTime, LogType.INFO);
//				}else{
//				    token = JWT.create()
//				        .withIssuer(HBO_ISSUER)
//				        .withAudience(HBO_AUD)
//				        .withExpiresAt(expireTime)
//				        .withIssuedAt(currentTime)
//				        .withNotBefore(currentTime)
//				        .withSubject(id)
//				        .withClaim("eml", email)
//				        .withClaim("country", "HK")
//				        .withClaim("operatorName", "nowe")
//				        .withClaim("operatorId", "NOWE")
//				        .withClaim("spAccountId", ottIdToMD5SumAndSuckIntoUUID(id))
//				        .sign(getHboAlgorithm());
//				    Log.log("HBO JWT Token Issuer:"+HBO_ISSUER+" Audience="+HBO_AUD+" ExpiresAt="+expireTime+ " IssuedAt="+currentTime+" NotBefore="+currentTime+" Subject:"+id+" Claim="+email, LogType.INFO);
//				}
//			} catch (JWTCreationException e){
//			    //Invalid Signing configuration / Couldn't convert Claims.
//				Log.log("Invalid Signing configuration / Couldn't convert Claims :"+e, LogType.ERROR, e);
//			} catch (Exception e) {
//				Log.log("Exception:"+e, LogType.ERROR, e);
//			}
//		}while(false);
//		Log.log("HBO Token:"+token, LogType.INFO);
//
//		DecodedJWT sosad = JWT.decode(token);
//		Log.log("HBO JWT Token Issuer:"+sosad.getIssuer()+" Audience="+sosad.getAudience()+" ExpiresAt="+sosad.getExpiresAt()+ " IssuedAt="+sosad.getIssuedAt()+" NotBefore="+sosad.getNotBefore()+" Subject:"+sosad.getSubject()+" Claim="+sosad.getClaims(), LogType.INFO);
//
//		return token;
//	}
//
//	public static String getAssetUrl(String conetentId, String contentType, boolean isWeb) {
//		String hboConetentId = conetentId.substring(3);
//
//		String urlContentType = contentType.toLowerCase();
//
//		if(isWeb && urlContentType.equals("live")) {
//			//Hopefully will get error if type not match(checkout live not use live type)
//			hboConetentId=hboConetentId.replace("ch-", "");
//		}
//
//		String hboUrl = "hbo://"+urlContentType+"/"+hboConetentId;
//		return hboUrl;
//	}
//
//	public static String ottIdToMD5SumAndSuckIntoUUID(String ottId) {
//		String uuid="";
//		try {
//			byte[] bytesOfMessage = ottId.getBytes();
//			MessageDigest md = MessageDigest.getInstance("MD5");
//			byte[] thedigest = md.digest(bytesOfMessage);
//			String md5 = DatatypeConverter.printHexBinary(thedigest).toUpperCase();
//			char[] md5char = md5.toCharArray();
//			int md5length = md5char.length;
//			int temp = 32-md5length;
//
//			Log.log("OTTID to MS5 to UUID Test:ottId:+"+ottId+" MD5:"+md5+ " MD5 length:"+md5length+" Remain:"+temp, LogType.INFO);
//			for(int i=0;i<32;++i) {
//				if(i==8||i==12||i==16||i==20) {
//					uuid+="-";
//				}if(i<temp) {
//					uuid+="0";
//				}else {
//					uuid+=md5char[temp+i];
//				}
//			}
//			Log.log("ottId:"+ottId+" UUID:"+uuid, LogType.INFO);
//
//		}catch(Exception e) {
//			Log.log("OTTID to MS5 to UUID Fail:"+e, LogType.ERROR, e);
//		}
//		return uuid;
//	}
}
