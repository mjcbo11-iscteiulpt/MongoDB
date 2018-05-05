package MongoApp;

import org.bson.Document;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;


	
public class Mongo {
	
	private MongoClient mongoClient = null;
	private MongoDatabase database = null;
	private MongoCollection<Document> collection;
	
	public static void main(String[] args) {
		(new Thread(new MongoDownload())).start();
	}
	public Mongo () {
		mongoClient = new MongoClient();
		database = mongoClient.getDatabase("LabMDB");
		System.out.println("Connection Successful");		
		collection = database.getCollection("HumidadeTemperatura");
		for(String nome: database.listCollectionNames()) {
			System.out.println(nome);
		}
	}
	
	public MongoClient getMongoClient() {
		return mongoClient;
	}

	public MongoDatabase getDatabase() {
		return database;
	}

	public MongoCollection<Document> getCollection() {
		return collection;
	}
	
	
}
