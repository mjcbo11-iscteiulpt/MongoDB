package MongoApp;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

public class MongoDownload extends Mongo implements Runnable{
	
	private int periodicidade = 10000; //milisegundos
	private List<Integer> temperatura = new ArrayList<Integer>();
	private List<Integer> humidade = new ArrayList<Integer>();
	private List<String> data = new ArrayList<String>();
	private List<String> hora = new ArrayList<String>();
	FindIterable<Document> coll;
	
	
	public MongoDownload() {
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
		coll = getDatabase().getCollection("HumidadeTemperatura").find(eq("Estado", 0));
		for(Document doc:coll) {
			System.out.println("A temperatura é "+doc.getDouble("Temperatura")+" e a Humidade é "+doc.getDouble("Humidade"));
			
			//Aqui o valor vai ser enviado para o Sybase
			
			Bson filter = Filters.eq("_id", doc.getObjectId("_id"));
		    Bson updates = Updates.set("Estado", 1);

		    getCollection().findOneAndUpdate(filter, updates);
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
	
	
}
