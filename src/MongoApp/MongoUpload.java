package MongoApp;

import java.util.LinkedList;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MongoUpload extends Mongo implements Runnable{

	private LinkedList<MqttMessage> messages = new LinkedList<MqttMessage>();
	private Paho p;
	
	public MongoUpload(Paho p) {
		this.p = p;
	}
	
	protected void messageUp(MqttMessage msg) {
		for(MqttMessage m: messages) {
			// Envia mongo!
		}
	}

	@Override
	public void run() {
		while(true) {
			messages.add(p.retrieveMsg());
		}
		
	}


}
