package MongoApp;

import org.bson.Document;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoUpload implements Runnable {

	private MongoClient mongoClient = null;
	private MongoDatabase database = null;
	private MongoCollection<Document> collection;
	private Paho p;
	private double pTemp;

	MongoClientURI uri = new MongoClientURI("mongodb://teste:teste@localhost/?authSource=LabMDB");

	public MongoUpload(Paho p) {
		this.p = p;
		mongoClient = new MongoClient(uri);
		database = mongoClient.getDatabase("LabMDB");
		System.out.println("Connection Successful");
		collection = database.getCollection("HumidadeTemperatura");

	}

	@Override
	public void run() {
		while (true) {
			System.out.println("retrieve antes");
			validation(p.retrieveMsg());
			System.out.println("retrieve depois");

		}
	}

	protected void messageUp(MqttMessage msg) {
		Document doc = Document.parse(msg.toString());
		System.out.println("Estado");
		doc.append("Estado", 0);
		collection.insertOne(doc);
		System.out.println("Inserir");
	}

	private void validation(MqttMessage msg) {
		System.out.println("Validation");

		String aux = msg.toString();
		String mqtt = aux.substring(1, (aux.length() - 1));
		String[] mqttAux = mqtt.split(",");
		String[] tempAux = mqttAux[0].split(":");
		String[] humdAux = mqttAux[1].split(":");
		Double temp = Double
				.parseDouble(tempAux[1].substring(tempAux[1].indexOf('"') + 1, tempAux[1].lastIndexOf('"')));
		Double humd = Double
				.parseDouble(humdAux[1].substring(humdAux[1].indexOf('"') + 1, humdAux[1].lastIndexOf('"')));
		if (pTemp == 0.0) {
			pTemp = temp;
			messageUp(msg);
		} else if (temp >= pTemp - 5 * (Math.max(temp, pTemp) / Math.min(temp, pTemp))
				&& temp <= pTemp + 5 * (Math.max(temp, pTemp) / Math.min(temp, pTemp)) && humd <= 99.9) {
			messageUp(msg);
			pTemp = temp;
		}
	}

}
