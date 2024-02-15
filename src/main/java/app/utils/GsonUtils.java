package app.utils;

import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class GsonUtils {
	protected static Gson gson = getGsonInstance();
	
	public static Gson getGsonInstance() {
		if (gson == null) {
			gson = new GsonBuilder().registerTypeAdapter(OffsetDateTime.class, new JsonDeserializer<OffsetDateTime>() {
			    @Override
			    public OffsetDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			    	if (StringUtils.length(json.getAsJsonPrimitive().getAsString())<10) {
			    		return null;
			    	}
			    	try {
			    		String dateString;
				    	if (StringUtils.length(json.getAsJsonPrimitive().getAsString())<16) {
				    		dateString = (json.getAsJsonPrimitive().getAsString().substring(0,10)+"T00:00:00Z").replaceAll(" ", "T");
				    	} else {
				    		dateString = (json.getAsJsonPrimitive().getAsString().substring(0,16)+":00Z").replaceAll(" ", "T");
				    	}
				        Instant instant = Instant.parse(dateString);

				        if (!json.getAsJsonPrimitive().getAsString().endsWith("Z")) {
				        	Log.debug("minus 8 hours to time to match UTC : original json time string is " + json.getAsJsonPrimitive().getAsString());
				        	instant = instant.minusSeconds(28800L);
				        }
				        
				        return OffsetDateTime.ofInstant(instant, ZoneId.systemDefault());
			    	} catch(Exception e) {
			    		Log.error("error when Gson is parsing jsonElement, " + e.getMessage());
			    		return OffsetDateTime.now();
			    	}
			    }
			}).registerTypeAdapter(OffsetDateTime.class, new JsonSerializer<OffsetDateTime>() {
				@Override
				public JsonElement serialize(OffsetDateTime src, Type typeOfSrc, JsonSerializationContext context) {
					return new JsonPrimitive(src.toInstant().toString());
				}
			}).create();
		}
		return gson;
	}
}
