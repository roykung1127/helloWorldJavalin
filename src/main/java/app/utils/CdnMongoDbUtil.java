package app.utils;

import app.beans.CdnRawObj;
import app.commons.MongoDB;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;


import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

public class CdnMongoDbUtil extends MongoDB {
	private final static String EMPTY_STRING = "";
	private final static String ISO_8601_DATE_FORMAT = Config.getInstance().getValue("dateFormat.iso8601");

	public static String getTenantCollections(String key) {
		StringBuilder logMessage = new StringBuilder();
		String result = "";
		logMessage.append("[Read Mongo] " + key);
		try {
			MongoCollection<Document> collection = getMongoCollection(MONGODB_DB, MONGODB_COLLECTION_CALLER);
			Bson filter = Filters.eq("cdns_cols_key", key);
			Document doc = collection.find(filter).first();
			if (doc.containsKey("cdns_cols_data")) {
				result = doc.getString("cdns_cols_data");
			}
			logMessage.append("="+result);
			Log.log(logMessage.toString(), LogType.INFO);
		} catch (Exception e) {

		}
		return result;
	}

	public static boolean setTenantCollections(String key, String data) {
		StringBuilder logMessage = new StringBuilder();
		boolean isUpdated = false;
		logMessage.append("[Set Mongo] " + key);
		try {
			MongoCollection<Document> collection = getMongoCollection(MONGODB_DB, MONGODB_COLLECTION_CALLER);
			Bson filter = Filters.eq("cdns_cols_key", key);
			Bson update = Updates.set("cdns_cols_data", data);
			UpdateOptions options = new UpdateOptions().upsert(true);
			collection.updateOne(filter, update, options);
			logMessage.append("="+data);
			isUpdated = true;
			Log.log(logMessage.toString(), LogType.INFO);
		} catch (Exception e) {

		}
		return isUpdated;
	}




	public static String getProfileNameCollection(String key) {
		StringBuilder logMessage = new StringBuilder();
		String result = "";
		logMessage.append("[Read Mongo] " + key);
		try {
			MongoCollection<Document> collection = getMongoCollection(MONGODB_DB, MONGODB_COLLECTION_PROFILENAME);
			Bson filter = Filters.eq("Tenant", key);
			List<Document> doc = collection.find(filter).into(new ArrayList<>());
			List<String> resultJson = new ArrayList<>();
			for(Document row: doc) {
				resultJson.add(row.toJson());
			}
			result = resultJson.toString();
			logMessage.append("="+ result);
			Log.log(logMessage.toString(), LogType.INFO);
		} catch (Exception e) {

		}
		return result;
	}


	public static boolean setProfileNameCollectionSyncCockpit (String key, List<CdnRawObj> cdnRawObjList) {
		StringBuilder logMessage = new StringBuilder();
		boolean isUpdated = false;
		logMessage.append("[Set Mongo] " + key + " [Action] Sync cockpit");

		try {
			// remove
			MongoCollection<Document> collection = getMongoCollection(MONGODB_DB, MONGODB_COLLECTION_PROFILENAME);
			collection.drop();
	
			// insert results
			List<Document> insertList = new ArrayList<>();

			for(CdnRawObj cdnRawObj: cdnRawObjList) {

				Document insertRow = new Document("Key", key + "_" + cdnRawObj.getName())
									.append("_id", cdnRawObj.get_id())
									.append("Overflow", cdnRawObj.getOverflow())
									.append("Type", cdnRawObj.getType())
									.append("Id", cdnRawObj.getId())
									.append("CDN", cdnRawObj.getCdn())
									.append("_mby", cdnRawObj.get_mby())
									.append("_by", cdnRawObj.get_by())
									.append("_modified", cdnRawObj.get_modified())
									.append("_created", cdnRawObj.get_created())
									.append("Name", cdnRawObj.getName())
									.append("Scheduled", cdnRawObj.getScheduled())
									.append("Duration", cdnRawObj.getDuration())
									.append("Tenant", key)
									.append("Key", key + "_" + cdnRawObj.getName());
				
				insertList.add(insertRow);
			}

			collection.insertMany(insertList);
			logMessage.append("="+ cdnRawObjList.toString());
			isUpdated = true;
			Log.log(logMessage.toString(), LogType.INFO);
		} catch (Exception e) {
		
		}

		return isUpdated;

	}



	public static boolean setProfileNameCollection(String action, String key, CdnRawObj cdnRawObj) {
		StringBuilder logMessage = new StringBuilder();
		boolean isUpdated = false;
		logMessage.append("[Set Mongo] " + key + " [Action] " + action);

		if (action.equalsIgnoreCase("enable")) {
			try {

				MongoCollection<Document> collection = getMongoCollection(MONGODB_DB, MONGODB_COLLECTION_PROFILENAME);
				Bson filter = Filters.eq("Key", key + "_" + cdnRawObj.getName());
				Bson update1 = Updates.set("_id", cdnRawObj.get_id());
				Bson update2 = Updates.set("Overflow", cdnRawObj.getOverflow());
				Bson update3 = Updates.set("Type", cdnRawObj.getType());
				Bson update4 = Updates.set("Id", cdnRawObj.getId());
				Bson update5 = Updates.set("CDN", cdnRawObj.getCdn());
				Bson update6 = Updates.set("_mby", cdnRawObj.get_mby());
				Bson update7 = Updates.set("_by", cdnRawObj.get_by());
				Bson update8 = Updates.set("_modified", cdnRawObj.get_modified());
				Bson update9 = Updates.set("_created", cdnRawObj.get_created());
				Bson update10 = Updates.set("Name", cdnRawObj.getName());
				Bson update11 = Updates.set("Scheduled", cdnRawObj.getScheduled());
				Bson update12 = Updates.set("Duration", cdnRawObj.getDuration());
				Bson update13 = Updates.set("Tenant", key);
				Bson update14 = Updates.set("Key", key + "_" + cdnRawObj.getName());
				Bson updates = Updates.combine(update1,update2,update3,update4,update5,update6,update7,update8,update9,update10,update11,update12,update13,update14);
				
				UpdateOptions options = new UpdateOptions().upsert(true);
				collection.updateOne(filter, updates, options);
				logMessage.append("="+ cdnRawObj.toString());
				isUpdated = true;
				Log.log(logMessage.toString(), LogType.INFO);
			} catch (Exception e) {
	
			}

		} else if(action.equalsIgnoreCase("disable")) {
			try {
				MongoCollection<Document> collection = getMongoCollection(MONGODB_DB, MONGODB_COLLECTION_PROFILENAME);
				Bson filter = Filters.eq("Key", key + "_" + cdnRawObj.getName());
				collection.deleteOne(filter);
				isUpdated = true;

				Log.log(logMessage.toString(), LogType.INFO);
			} catch (Exception e) {
	
			}
		}
		return isUpdated;
	}




	public static String getApiManagedRef(String key) {
		StringBuilder logMessage = new StringBuilder();
		String result = "";
		logMessage.append("[Read Mongo] " + key);
		try {
			MongoCollection<Document> collection = getMongoCollection(MONGODB_DB, MONGODB_COLLECTION_CALLER);
			Bson filter = Filters.eq("cdns_ApiManagedRef", key);
			Document doc = collection.find(filter).first();
			if (doc.containsKey("cdns_ApiManagedRef_data")) {
				result = doc.getString("cdns_ApiManagedRef_data");
			}
			logMessage.append("="+result);

			Log.log(logMessage.toString(), LogType.INFO);
		} catch (Exception e) {

		} finally {
		}
		return result;
	}

	public static boolean setApiManagedRef(String key, String data) {
		StringBuilder logMessage = new StringBuilder();
		boolean isUpdated = false;
		logMessage.append("[Set Mongo] " + key);
		try {
			MongoCollection<Document> collection = getMongoCollection(MONGODB_DB, MONGODB_COLLECTION_CALLER);
			Bson filter = Filters.eq("cdns_ApiManagedRef", key);
			Bson update = Updates.set("cdns_ApiManagedRef_data", data);
			UpdateOptions options = new UpdateOptions().upsert(true);
			collection.updateOne(filter, update, options);
			logMessage.append("="+data);
			isUpdated = true;
			Log.log(logMessage.toString(), LogType.INFO);
		} catch (Exception e) {

		}
		return isUpdated;
	}

	public static String getPublishRef(String key) {
		StringBuilder logMessage = new StringBuilder();
		String result = "";
		logMessage.append("[Read Mongo] " + key);
		try {
			MongoCollection<Document> collection = getMongoCollection(MONGODB_DB, MONGODB_COLLECTION_CALLER);
			Bson filter = Filters.eq("cdns_PublishRef", key);
			Document doc = collection.find(filter).first();
			if (doc.containsKey("cdns_PublishRef_data")) {
				result = doc.getString("cdns_PublishRef_data");
			}
			logMessage.append("="+result);

			Log.log(logMessage.toString(), LogType.INFO);
		} catch (Exception e) {

		}
		return result;
	}

	public static boolean setPublishRef(String key, String data) {
		StringBuilder logMessage = new StringBuilder();
		boolean isUpdated = false;
		logMessage.append("[Set Mongo] " + key);
		try {
			MongoCollection<Document> collection = getMongoCollection(MONGODB_DB, MONGODB_COLLECTION_CALLER);
			Bson filter = Filters.eq("cdns_PublishRef", key);
			Bson update = Updates.set("cdns_PublishRef_data", data);
			UpdateOptions options = new UpdateOptions().upsert(true);
			collection.updateOne(filter, update, options);
			logMessage.append("="+data);
			isUpdated = true;
			Log.log(logMessage.toString(), LogType.INFO);
		} catch (Exception e) {

		}
		return isUpdated;
	}
}
