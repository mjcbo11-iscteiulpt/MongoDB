package MongoApp;

import org.bson.Document;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoUpload implements Runnable {

	private MongoClient mongoClient = null;
	private MongoDatabase database = null;
	private MongoCollection<Document> collection;
	private Paho p;

	public MongoUpload(Paho p) {
		this.p = p;
		mongoClient = new MongoClient();
		database = mongoClient.getDatabase("LabMDB");
		System.out.println("Connection Successful");
		collection = database.getCollection("HumidadeTemperatura");

	}

	@Override
	public void run() {
		while (true) {
			messageUp(p.retrieveMsg());
		}
	}

	protected void messageUp(MqttMessage msg) {
		Document doc = Document.parse(msg.toString());
		doc.append("Estado", "0");
		// collection.insertOne(doc);
	}

}
