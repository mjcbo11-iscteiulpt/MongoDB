package MongoApp;

import java.util.LinkedList;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.mongodb.BasicDBObject;

public class MongoUpload extends Mongo implements Runnable {

	private LinkedList<MqttMessage> messages = new LinkedList<MqttMessage>();
	private Paho p;

	public MongoUpload(Paho p) {
		this.p = p;
	}

	@Override
	public void run() {
		while (true) {
			messages.add(p.retrieveMsg());
		}
	}

	protected void messageUp(MqttMessage msg) {
		for (MqttMessage m : messages) {
			System.out.println("mongoU: " + m);
			BasicDBObject document = new BasicDBObject();
//			document.put("Hora", hora);
//			document.put("Data", data);
//			document.put("Estado", 0);		
//			document.put("Valor_Temperatura", valorT);
//			document.put("Valor_Humidade", valorH);
			super.getCollection().insert(document);
		}
	}

}
