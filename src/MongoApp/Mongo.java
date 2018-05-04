package MongoApp;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

	
public class Mongo {
	
	private MongoClient mongoClient = null;
	private DB database = null;
	private DBCollection collection = null;
	

	public Mongo () {
		mongoClient = new MongoClient();
		database = mongoClient.getDB("LabMDB");
		System.out.println("Connection Successful");
		
		collection = database.getCollection("HumidadeTemperatura");
		System.out.println(database.getCollectionNames());
	}
	
	public MongoClient getMongoClient() {
		return mongoClient;
	}

	public DB getDatabase() {
		return database;
	}

	public DBCollection getCollection() {
		return collection;
	}
	
	
}
