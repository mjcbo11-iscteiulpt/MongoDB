package MongoApp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.bson.Document;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoUpload implements Runnable{

//	private MongoClient mongoClient = null;
//	private MongoDatabase database = null;
//	private MongoCollection<Document> collection;
	private LinkedList<MqttMessage> messages = new LinkedList<MqttMessage>();
	private Paho p;
	
	public MongoUpload(Paho p) {
		this.p = p;
//		mongoClient = new MongoClient();
//		database = mongoClient.getDatabase("LabMDB");
		System.out.println("Connection Successful");
//		collection = database.getCollection("HumidadeTemperatura");
	}
	
	protected void messageUp(MqttMessage msg) {
		HashMap<String, String> fMap = documentBuilder(msg);
		
		BasicDBObject document = new BasicDBObject();
		for (String key: fMap.keySet()) {
			//document.put(key, fMap.get(key));
			System.out.println(key + " " + fMap.get(key));
		}
		document.put("Estado", "0");
//		collection.insert(document);

	}

	@Override
	public void run() {
		while(true) {
			messageUp(p.retrieveMsg());
		}
	}

	private HashMap documentBuilder(MqttMessage mqttMsg) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			String msg = mqttMsg.toString();
			BufferedReader bf = new BufferedReader(new FileReader(msg));
			String s = bf.readLine();
			while(s != null) {
				String[] v = s.split(",");
				v[0] = v[0].substring(1);
				v[3] = v[3].substring(0, v[3].length() - 1);
				for (String str: v) {
					String[] aux = str.split(":");
					map.put(aux[0].trim(), aux[1].trim());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

}
