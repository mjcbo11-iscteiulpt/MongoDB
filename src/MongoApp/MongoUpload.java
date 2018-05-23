package MongoApp;

import java.io.BufferedReader;
import java.io.File;
import java.util.HashMap;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.mongodb.BasicDBObject;

public class MongoUpload extends Mongo implements Runnable {

	private Paho p;

	public MongoUpload(Paho p) {
		this.p = p;
	}

	@Override
	public void run() {
		while (true) {
			messageUp(p.retrieveMsg());
			
		}
	}

	protected void messageUp(MqttMessage msg) {
		HashMap<String, String> map = new HashMap<String, String>();
		String[] m = msg.toString().split(",");
		//BufferedReader bfr = new BufferedReader(new File(m));
		System.out.println("a inserir mensagem: " + msg + "no Mongo");
		
		
		BasicDBObject document = new BasicDBObject();
//		document.put("Hora", hora);
//		document.put("Data", data);
//		document.put("Estado", 0);		
//		document.put("Valor_Temperatura", valorT);
//		document.put("Valor_Humidade", valorH);
		super.getCollection().insert(document);
		
	}

}
