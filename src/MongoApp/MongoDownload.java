package MongoApp;

public class MongoDownload extends Mongo implements Runnable{
	
	private int periodicidade = 60; //segundos
	
	public MongoDownload() {
		
	}

	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(1000);
				//collection.
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
}
