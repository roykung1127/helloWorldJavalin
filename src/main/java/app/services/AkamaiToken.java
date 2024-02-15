package app.services;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.*;
import java.util.*;
import java.util.regex.*;
import javax.xml.bind.DatatypeConverter;

import app.utils.Config;
import app.utils.Log;
import app.utils.LogType;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class AkamaiToken {
    public static String program_name = "akamai_token_v2";
    public static String default_token_name = "hdnts";
    public static String default_acl = "/*";
    public static String default_algo = "sha256";
    public static String default_field_delimiter = "~";
    public static String default_acl_delimiter = "!";

    public static void displayHelp() {
        System.out.println("Usage: java AkamaiToken [options]");
        System.out.println("ie.");
        System.out.println("java AkamaiToken");
        System.out.println("");
        System.out.println("Options:");
        System.out.println("  --version             show program's version number and exit");
        System.out.println("  -h, --help            show this help message and exit");
        System.out.println("  -t TOKEN_TYPE, --token_type TOKEN_TYPE");
        System.out.println("                        Select a preset: (Not Supported Yet) [2.0, 2.0.2 ,PV, Debug]");
        System.out.println("  -n TOKEN_NAME, --token_name TOKEN_NAME");
        System.out.println("                        Parameter name for the new token. [Default:hdnts]");
        System.out.println("  -i IP_ADDRESS, --ip IP_ADDRESS");
        System.out.println("                        IP Address to restrict this token to.");
        System.out.println("  -s START_TIME, --start_time START_TIME");
        System.out.println("                        What is the start time. (Use now for the current time)");
        System.out.println("  -e END_TIME, --end_time END_TIME");
        System.out.println("                        When does this token expire? --end_time overrides");
        System.out.println("                        --window [Used for:URL or COOKIE]");
        System.out.println("  -w WINDOW_SECONDS, --window WINDOW_SECONDS");
        System.out.println("                        How long is this token valid for?");
        System.out.println("  -u URL, --url URL     URL path. [Used for:URL]");
        System.out.println("  -a ACCESS_LIST, --acl ACCESS_LIST");
        System.out.println("                        Access control list delimited by ! [ie. /*]");
        System.out.println("  -k KEY, --key KEY     Secret required to generate the token.");
        System.out.println("  -p PAYLOAD, --payload PAYLOAD");
        System.out.println("                        Additional text added to the calculated digest.");
        System.out.println("  -A ALGORITHM, --algo ALGORITHM");
        System.out.println("                        Algorithm to use to generate the token. (sha1, sha256,");
        System.out.println("                        or md5) [Default:sha256]");
        System.out.println("  -S SALT, --salt SALT  Additional data validated by the token but NOT");
        System.out.println("                        included in the token body.");
        System.out.println("  -I SESSION_ID, --session_id SESSION_ID");
        System.out.println("                        The session identifier for single use tokens or other");
        System.out.println("                        advanced cases.");
        System.out.println("  -d FIELD_DELIMITER, --field_delimiter FIELD_DELIMITER");
        System.out.println("                        Character used to delimit token body fields.");
        System.out.println("                        [Default:~]");
        System.out.println("  -D ACL_DELIMITER, --acl_delimiter ACL_DELIMITER");
        System.out.println("                        Character used to delimit acl fields. [Default:!]");
        System.out.println("  -x, --escape_early    Causes strings to be url encoded before being used.");
        System.out.println("                        (legacy 2.0 behavior)");
        System.out.println("  -X, --escape_early_upper");
        System.out.println("                        Causes strings to be url encoded before being used.");
        System.out.println("                        (legacy 2.0 behavior)");
        System.out.println("  -v, --verbose");
    }

    public static void displayVersion() {
        System.out.println("2.0.7");
    }

    public static String getKeyValue(final Dictionary token_config, final String key,
        final String default_value) {
        Object value = token_config.get(key);
        if (value == null) {
            return default_value;
        }
        return value.toString();
    }

    public static String escapeEarly(final Dictionary token_config, final String text) {
        String escape_early = getKeyValue(token_config, "escape_early", "false");
        String escape_early_upper = getKeyValue(token_config, "escape_early_upper", "false");
        StringBuilder new_text = new StringBuilder(text);
        try {
            if (escape_early.equalsIgnoreCase("true") ||
                escape_early_upper.equalsIgnoreCase("true")) {
                new_text = new StringBuilder(URLEncoder.encode(text, "UTF-8"));
                Pattern pattern = Pattern.compile("%..");
                Matcher matcher = pattern.matcher(new_text);
                String temp_text;
                while (matcher.find()) {
                    if (escape_early_upper.equalsIgnoreCase("true")) {
                        temp_text = new_text.substring(matcher.start(), matcher.end()).toUpperCase();
                    } else {
                        temp_text = new_text.substring(matcher.start(), matcher.end()).toLowerCase();
                    }
                    new_text.replace(matcher.start(), matcher.end(), temp_text);
                }
            }
        } catch (UnsupportedEncodingException e) {
            // Ignore any encoding errors and return the original string.
        }

        return new_text.toString();
    }

    public static void displayParameters(Dictionary token_config) {
        String escape_early = getKeyValue(token_config, "escape_early", "false");
        String escape_early_upper = getKeyValue(token_config, "escape_early_upper", "false");
        if (escape_early.equalsIgnoreCase("true") || escape_early_upper.equalsIgnoreCase("true")) {
            escape_early = "true";
        }
        System.out.println("Akamai Token Generation Parameters");
        System.out.println("    Token Type      : " + getKeyValue(token_config, "token_type", ""));
        System.out.println("    Token Name      : " + getKeyValue(token_config, "token_name", default_token_name));
        System.out.println("    Start Time      : " + getKeyValue(token_config, "start_time", ""));
        System.out.println("    Window(seconds) : " + getKeyValue(token_config, "window_seconds", ""));
        System.out.println("    End Time        : " + getKeyValue(token_config, "end_time", ""));
        System.out.println("    IP              : " + getKeyValue(token_config, "ip_address", ""));
        System.out.println("    URL             : " + getKeyValue(token_config, "url", ""));
        System.out.println("    ACL             : " + getKeyValue(token_config, "acl", default_acl));
        System.out.println("    Key/Secret      : " + getKeyValue(token_config, "key", ""));
        System.out.println("    Payload         : " + getKeyValue(token_config, "payload", ""));
        System.out.println("    Algo            : " + getKeyValue(token_config, "algo", default_algo));
        System.out.println("    Salt            : " + getKeyValue(token_config, "salt", ""));
        System.out.println("    Session ID      : " + getKeyValue(token_config, "session_id", ""));
        System.out.println("    Field Delimiter : " + getKeyValue(token_config, "field_delimiter", default_field_delimiter));
        System.out.println("    ACL Delimiter   : " + getKeyValue(token_config, "acl_delimiter", default_acl_delimiter));
        System.out.println("    Escape Early    : " + escape_early);
        System.out.println("Generating token...");
    }

    public static String getTokenIP(Dictionary token_config) {
        String ip_address = escapeEarly(token_config, getKeyValue(token_config, "ip_address", "")); 
        if (ip_address.length() > 0) {
            return "ip=" + ip_address + getKeyValue(token_config, "field_delimiter", default_field_delimiter);
        }
        return "";
    }

    public static String getTokenStartTime(Dictionary token_config) {
        String start_time = getKeyValue(token_config, "start_time", "");
        if (start_time.length() > 0) {
            return "st=" + start_time + getKeyValue(token_config, "field_delimiter", default_field_delimiter);
        }
        return "";
    }

    public static String getTokenEndTime(Dictionary token_config) {
        return "exp=" + getKeyValue(token_config, "end_time", "") + getKeyValue(token_config, "field_delimiter", default_field_delimiter);
    }

    public static String getTokenAcl(Dictionary token_config) {
        String acl = escapeEarly(token_config, getKeyValue(token_config, "acl", ""));
        if (acl.length() > 0) {
            return "acl=" + acl + getKeyValue(token_config, "field_delimiter", default_field_delimiter);
        }
        return "";
    }

    public static String getTokenSessionID(Dictionary token_config) {
        String session_id = escapeEarly(token_config, getKeyValue(token_config, "session_id", ""));
        if (session_id.length() > 0) {
            return "id=" + session_id + getKeyValue(token_config, "field_delimiter", default_field_delimiter);
        }
        return "";
    }

    public static String getTokenPayload(Dictionary token_config) {
        String payload = escapeEarly(token_config, getKeyValue(token_config, "payload", ""));
        if (payload.length() > 0) {
            return "data=" + payload + getKeyValue(token_config, "field_delimiter", default_field_delimiter);
        }
        return "";
    }

    public static String getTokenUrl(Dictionary token_config) {
        String url = escapeEarly(token_config, getKeyValue(token_config, "url", ""));
        if (url.length() > 0) {
            return "url=" + url + getKeyValue(token_config, "field_delimiter", default_field_delimiter);
        }
        return "";
    }

    public static String getTokenSalt(Dictionary token_config) {
        String salt = escapeEarly(token_config, getKeyValue(token_config, "salt", ""));
        if (salt.length() > 0) {
            return "salt=" + salt + getKeyValue(token_config, "field_delimiter", default_field_delimiter);
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    public static String generateToken(Dictionary token_config) throws AkamaiTokenException {

    	Enumeration<String> token_key = token_config.keys();
        while(token_key.hasMoreElements()) {
        	try {
	    	    String key = token_key.nextElement();
	    	    String value = token_config.get(key).toString();
	            Log.log("Akamai token key:"+key+" value:"+value , LogType.INFO);
        	}catch(Exception e) {
        		
        	}
    	}
    	
        String algo = getKeyValue(token_config, "algo", default_algo);
        if (!algo.equalsIgnoreCase("md5") && !algo.equalsIgnoreCase("sha1") &&
            !algo.equalsIgnoreCase("sha256")) {
            throw new AkamaiTokenException("unknown algorithm");
        }

        String start_time_text = getKeyValue(token_config, "start_time", "");
        long start_time = 0;
        if (start_time_text.equalsIgnoreCase("now")) {
            start_time = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()/1000L;
            token_config.put("start_time", Long.toString(start_time));
        } else if (start_time_text != "" ) {
            try {
                start_time = Long.parseLong(start_time_text);
            } catch (Exception e) {
                throw new AkamaiTokenException("start_time must be numeric or now");
            }
        }

        long window = Long.parseLong(getKeyValue(token_config, "window_seconds", "0"));

        String end_time_text = getKeyValue(token_config, "end_time", "");
        long end_time = 0;
        if (end_time_text.equalsIgnoreCase("now")) {
            end_time = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()/1000L;
        } else if (end_time_text != "" ) {
            try {
                end_time = Long.parseLong(end_time_text);
            } catch (Exception e) {
                throw new AkamaiTokenException("end_time must be numeric");
            }
        } else {
            if (start_time_text != "" ) {
                end_time = start_time + window;
            } else {
                end_time = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()/1000L + window;
            }
        }
        token_config.put("end_time", Long.toString(end_time));

        String acl = getKeyValue(token_config, "acl", "");
        String url = getKeyValue(token_config, "url", "");
        if (acl.length() < 1 && url.length() < 1) {
            throw new AkamaiTokenException("you must provide an acl or url");
        } else if (acl.length() >= 1 && url.length() >= 1) {
            throw new AkamaiTokenException("you must provide an acl or url, not both");
        }

        String key = getKeyValue(token_config, "key", "");
        if (key.length() < 1)
            throw new AkamaiTokenException("you must provide a key");

        if (getKeyValue(token_config, "verbose", "").equalsIgnoreCase("true"))
            displayParameters(token_config);

        StringBuilder new_token = new StringBuilder();
        new_token.append(getTokenIP(token_config));
        new_token.append(getTokenStartTime(token_config));
        new_token.append(getTokenEndTime(token_config));
        new_token.append(getTokenAcl(token_config));
        new_token.append(getTokenSessionID(token_config));
        new_token.append(getTokenPayload(token_config));

        StringBuilder hash_source = new StringBuilder(new_token);
        hash_source.append(getTokenUrl(token_config));
        hash_source.append(getTokenSalt(token_config));

        algo = getKeyValue(token_config, "algo", default_algo);
        String crypto_algo = "HmacSHA256";
        if (algo.equalsIgnoreCase("sha256"))
            crypto_algo = "HmacSHA256";
        else if (algo.equalsIgnoreCase("sha1"))
            crypto_algo = "HmacSHA1";
        else if (algo.equalsIgnoreCase("md5"))
            crypto_algo = "HmacMD5";

        try {
            Mac hmac = Mac.getInstance(crypto_algo);
            byte[] key_bytes = DatatypeConverter.parseHexBinary(getKeyValue(token_config, "key", ""));
            SecretKeySpec secret_key = new SecretKeySpec(key_bytes, crypto_algo);
            hmac.init(secret_key);
            byte[] hmac_bytes = hmac.doFinal(hash_source.substring(0, hash_source.length()-1).toString().getBytes());
            return getKeyValue(token_config, "token_name", default_token_name) + "=" +
                new_token.toString() + "hmac=" + String.format("%0" + (2*hmac.getMacLength()) +  "x",new BigInteger(1, hmac_bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new AkamaiTokenException(e.toString());
        } catch (InvalidKeyException e) {
            throw new AkamaiTokenException(e.toString());
        }
    }
    
    public static String genToken(String ip_address, String url, boolean enableCookieless) {
    	//fake IP
    	//ip_address="58.153.138.79";
    	//End Fake IP
    	Log.startLog("Akamai Generate Token");
        Dictionary token_config = new Hashtable();
        String retVal = null;
          String ACL = Config.getInstance().getValue("akamai.akamaiACL");
          String key = Config.getInstance().getValue("akamai.akamaiKey");
          String window = Config.getInstance().getValue("akamai.akamaiWindow");
          int expiresTime = Integer.parseInt((String)Config.getInstance().getValue("akamai.akamaiExpireTime"));
          Log.log("DEBUG:akamai.akamaiExpireTime="+(String)Config.getInstance().getValue("akamai.akamaiExpireTime")+ " expiresTime"+expiresTime, LogType.INFO);
          //Tricks for time not sync....
          Date startDate = new Date();
          Calendar cal = Calendar.getInstance();
          cal.setTime(startDate);
          cal.add(Calendar.HOUR_OF_DAY, -1);
          startDate = cal.getTime();
          String start = String.valueOf((startDate.getTime() / 1000));
          
          Date endDate = new Date();
          cal.setTime(endDate);
          cal.add(Calendar.HOUR_OF_DAY, expiresTime);
          endDate = cal.getTime();
          String expires = String.valueOf((endDate.getTime() / 1000));
          
          int isAuthURL = 0;
          int isAuthIp = 0;
          try {
        	  isAuthURL = Integer.parseInt(Config.getInstance().getValue("akamai.akamaiIsAuthUrl"));
          } catch (Exception e){}
          
          try {
        	  isAuthIp = Integer.parseInt(Config.getInstance().getValue("akamai.akamaiIsAuthIp"));
          } catch (Exception e){}

          
          if(ip_address.contains(",")) {
	        String[] ipAry = ip_address.split(",");
	  		if(ipAry.length>1){
	  			String proxyStr = "The incoming IP address with PROXY: "+ ipAry[0];
	  			for(int i =1;i<ipAry.length;++i){
	  				proxyStr = proxyStr + " --> " + ipAry[i];
	  			}
	  			Log.log(proxyStr, LogType.INFO);
	  			ip_address = ipAry[0].trim();
	  			Log.log("The 'real' client IP address: "+ ip_address, LogType.INFO);
	  		}
          }
          Log.log("Akamai value from AMS:  ip_address:"+ip_address + " window:"+window+" key:"+key , LogType.INFO);

        if (isAuthURL == 1) {
            token_config.put("url", url);
        } else {
            token_config.put("acl", ACL);
        }
          
        if ((isAuthIp == 1) && (StringUtils.isNotEmpty(ip_address))) {
            token_config.put("ip_address", ip_address);
        } 
        
        if (StringUtils.isNotEmpty(expires)) {
        	token_config.put("end_time", expires);
            token_config.put("start_time", start);
        } else {
        	token_config.put("window", window);
        }
        
        if (StringUtils.isNotEmpty(key)) {
        //	String hexString = DatatypeConverter.printHexBinary(key.getBytes());
        //	Log.log("key: " + hexString, logType.INFO);
            token_config.put("key", key);
        } 
        
        try {
        	retVal = generateToken(token_config);
        	if(enableCookieless) {
        		retVal+="&cookieless=true";
        	}
        	retVal+="&app=nowe";
        } catch (AkamaiTokenException e) {
            System.out.println(e);
            Log.log("Akamai Token Generation Error", LogType.ERROR, e);
        } 
        Log.endLog();
    	return retVal;
    	
    }
    
    
}
