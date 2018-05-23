package MongoApp;

import java.util.LinkedList;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Paho implements MqttCallback {

	public String topic = "iscte_sid_2018_S1";
	public String content = "Message from MqttPublishSample";
	public int qos = 0;
	public String broker = "tcp://iot.eclipse.org:1883";
	public String clientId = MqttClient.generateClientId();
	public MemoryPersistence persistence = new MemoryPersistence();
	private LinkedList<MqttMessage> messages = new LinkedList<MqttMessage>();

	public Paho() {
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
			sampleClient.subscribe(topic);
			sampleClient.setCallback(this);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
		System.out.println(arg1);
		synchronized (this) {
			messages.add(arg1);
			notifyAll();
		}
		System.out.println("passou");
	}

	public synchronized MqttMessage retrieveMsg() {
		MqttMessage msg = null;
		try {
			while (messages.size() < 1) {
				System.out.println("bloquear");
				wait();
			}
			msg = messages.poll();
			System.out.println("tirei msg");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
			return msg;
		}

}


