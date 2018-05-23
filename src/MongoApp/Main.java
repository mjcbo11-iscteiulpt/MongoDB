package MongoApp;

public class Main {

	public static void main(String[] args) {
		Paho p = new Paho();
		//p.writeConfig();
		MongoUpload mUp = new MongoUpload(p);
		Thread t = new Thread(mUp);
		t.start();
	}

}
