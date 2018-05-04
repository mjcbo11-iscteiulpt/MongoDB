package MongoApp;

import java.util.LinkedList;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class PahoServer {

	private String broker = "tcp://iot.eclipse.org:1883";
	public String topic = "iscte_sid_2018_S1";
	public String content = "Message from MqttPublishSample";
	public int qos = 0;
	public String clientId = MqttClient.generateClientId();
	public MemoryPersistence persistence = new MemoryPersistence();
	private LinkedList<MqttMessage> messages = new LinkedList<MqttMessage>();

	public static void main(String[] args) {
		Paho p = new Paho();	
	}

	public PahoServer() {
		connect();
	}
	
	public void connect() {
		try {
			MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			System.out.println("Connecting to broker: " + broker);
			sampleClient.connect(connOpts);
			System.out.println("Connected");
			System.out.println("Publishing message: "+content);
			MqttMessage message = new MqttMessage(content.getBytes());
			message.setQos(qos);
			sampleClient.publish(topic, message);
					
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
