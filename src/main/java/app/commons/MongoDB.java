package app.commons;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import app.main.MainApp;
import app.utils.Config;
import app.utils.Log;
import app.utils.LogType;
import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.MongoClient;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.bson.Document;

public class MongoDB {
	private static MongoClient _instance;
	
	public static String MONGODB_URL = Config.getInstance().getValue("mongodbAddress");
	public final static String MONGODB_DB = "cdns";
	public final static String MONGODB_COLLECTION_CALLER = "caller";
	public final static String MONGODB_COLLECTION_PROFILENAME = "profilename";
	
	public static synchronized MongoClient getMongoInstance() {
		if (_instance == null) {
			if (MainApp.ENV.equals("local")) {
				MongoServer server = new MongoServer(new MemoryBackend());
				InetSocketAddress serverAddress = server.bind();
				Log.info(serverAddress.toString());
				_instance = MongoClients.create("mongodb:/" + serverAddress.toString()); //new MongoClient(new ServerAddress(serverAddress));
			} else {
				try {
					ArrayList<ServerAddress> addr = new ArrayList<ServerAddress>();
					for (String s : MONGODB_URL.split(",")) {
						addr.add(new ServerAddress(s));
					}

					//MongoDB Login
					MongoCredential credential = MongoCredential.createCredential("admin", "admin", "admin123".toCharArray());
					MongoClientSettings settings = MongoClientSettings.builder()
							.credential(credential)
							.readPreference(ReadPreference.secondaryPreferred())
							.applyToClusterSettings(builder ->
									builder.hosts(addr))
							.build();
					_instance = MongoClients.create(settings);

				} catch (Exception e) {
					Log.log("Error at Connect Server : " + e, LogType.ERROR);
					_instance.close();
				}
			}
		}
		return _instance;
	}
	
	public static MongoCollection<Document>  getMongoCollection(String dbName, String collectionName) throws Exception {
		return  getMongoInstance().getDatabase(dbName).getCollection(collectionName);

	}

	public static void close() {
		if (_instance != null) {
			_instance.close();
			_instance = null;
		}
	}
}
