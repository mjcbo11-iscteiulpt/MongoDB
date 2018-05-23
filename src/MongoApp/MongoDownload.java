package MongoApp;

import static com.mongodb.client.model.Filters.eq;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

public class MongoDownload implements Runnable{
	
	private MongoClient mongoClient = null;
	private MongoDatabase database = null;
	private MongoCollection<Document> collection;
	private int periodicidade = 10000; //milisegundos
	private List<Integer> temperatura = new ArrayList<Integer>();
	private List<Integer> humidade = new ArrayList<Integer>();
	private List<String> data = new ArrayList<String>();
	private List<String> hora = new ArrayList<String>();
	FindIterable<Document> coll;
	
	
	public MongoDownload() {
		mongoClient = new MongoClient();
		database = mongoClient.getDatabase("LabMDB");
		System.out.println("Connection Successful");
		collection = database.getCollection("HumidadeTemperatura");
		/*BasicDBObject query = new BasicDBObject();
		BasicDBObject field = new BasicDBObject();
		field.put("HumidadeTemperatura", 1);
		DBCursor cursor = getCollection().find(query,field);
		while (cursor.hasNext()) {
		    BasicDBObject obj = (BasicDBObject) cursor.next();
		    if(obj.getInt("Estado")!=1) {
		    temperatura.add(obj.getInt("Temperatura"));
		    humidade.add(obj.getInt("Humidade"));
		    data.add(obj.getString("Data"));
		    hora.add(obj.getString("Hora"));
		    }
		}*/
		
	}
	
	public void getNewValues() {
		coll = database.getCollection("HumidadeTemperatura").find(eq("Estado", 0));
		for(Document doc:coll) {
			System.out.println("A temperatura é "+doc.getDouble("Temperatura")+" e a Humidade é "+doc.getDouble("Humidade"));
			
			//Aqui o valor vai ser enviado para o Sybase
			
			Bson filter = Filters.eq("_id", doc.getObjectId("_id"));
		    Bson updates = Updates.set("Estado", 1);

		    collection.findOneAndUpdate(filter, updates);
		}
	}

	@Override
	public void run() {
		while(true) {
			try {
				System.out.println("\n***************** \nComecar espera");
				Thread.sleep(periodicidade);
				getNewValues();
				System.out.println("Valores alterados\n*****************\n");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void writeConfig() {
		Properties prop = new Properties();
		Properties configFile = new java.util.Properties();
    	try {
    		configFile.load(this.getClass().getClassLoader().getResourceAsStream("variale.properties"));
    		String b = configFile.getProperty("Broker");
    		String t = configFile.getProperty("Topico");
    		prop.setProperty("Broker", b);
    		prop.setProperty("Topico", t);
    		prop.setProperty("Periocidade", Integer.toString(this.periodicidade));
 
    		prop.store(new FileOutputStream("variable.properties"), null);
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
	}
	
	public void loadConfig() {
		Properties configFile = new java.util.Properties();
		try {
			configFile.load(this.getClass().getClassLoader().getResourceAsStream("variale.properties"));
			int periocidade = Integer.parseInt(configFile.getProperty("Periocidade"));
			this.periodicidade = periocidade;
		} catch(Exception e){
		    e.printStackTrace();
		}
	}
	
}
