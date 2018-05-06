package MongoApp;

public class Main {

	public static void main(String[] args) {
		Paho paho = new Paho();
		MongoUpload mUp = new MongoUpload(paho);
		Thread t = new Thread(mUp);
		t.start();

		
	}

}
