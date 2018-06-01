package MongoApp;

import static com.mongodb.client.model.Filters.eq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

public class MongoDownload implements Runnable {

	private MongoClient mongoClient = null;
	private MongoDatabase database = null;
	private MongoCollection<Document> collection;
	private int periodicidade; // milisegundos
	private List<Integer> temperatura = new ArrayList<Integer>();
	private List<Integer> humidade = new ArrayList<Integer>();
	private List<String> data = new ArrayList<String>();
	private List<String> hora = new ArrayList<String>();
	FindIterable<Document> coll;

	public MongoDownload() {
		loadConfig();
		mongoClient = new MongoClient();
		database = mongoClient.getDatabase("LabMDB");
		System.out.println("Connection Successful");
		collection = database.getCollection("HumidadeTemperatura");		
		this.run();
		/*
		 * BasicDBObject query = new BasicDBObject(); BasicDBObject field = new
		 * BasicDBObject(); field.put("HumidadeTemperatura", 1); DBCursor cursor
		 * = getCollection().find(query,field); while (cursor.hasNext()) {
		 * BasicDBObject obj = (BasicDBObject) cursor.next();
		 * if(obj.getInt("Estado")!=1) {
		 * temperatura.add(obj.getInt("Temperatura"));
		 * humidade.add(obj.getInt("Humidade"));
		 * data.add(obj.getString("Data")); hora.add(obj.getString("Hora")); } }
		 */

	}

	public void getNewValues() {
		coll = database.getCollection("HumidadeTemperatura").find(eq("Estado", 0));
		for (Document doc : coll) {
			System.out.println("A temperatura � " + doc.getString("Temperatura") + " e a Humidade � " + doc.getString("Humidade"));

			// Aqui o valor vai ser enviado para o Sybase
			sendToSybase(doc);
			
			Bson filter = Filters.eq("_id", doc.getObjectId("_id"));
			Bson updates = Updates.set("Estado", 1);

			collection.findOneAndUpdate(filter, updates);
		}
	}

	private void sendToSybase(Document doc) {		
        try {
        	
			Connection con = DriverManager.getConnection("jdbc:sqlanywhere:uid=admin;pwd=admin" );
			Statement stmt = con.createStatement();
			
			String dia = doc.getString("Dia");
			String[] array = dia.split(":"); 
			String novoDia = array[2]+"-"+array[1]+"-"+array[0];
			System.out.println(novoDia);
					
			ResultSet rs = stmt.executeQuery("INSERT INTO HumidadeTemperatura (DataMedicao,HoraMedicao,ValorMedicaoTemperatura,ValorMedicaoHumidade)    VALUES ('"+novoDia+"','"+doc.getString("Hora")+"',"+doc.getString("Temperatura")+","+doc.getString("Humidade")+")");
			
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		
	}

	@Override
	public void run() {
		while (true) {
			try {
				System.out.println("\n***************** \nComecar espera");
				Thread.sleep(periodicidade);
				getNewValues();
				System.out.println("Valores inseridos no Sybase");
				System.out.println("Valores alterados\n*****************\n");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void loadConfig() {
		File configFile = new File("default.properties");
		try {
			FileReader reader = new FileReader(configFile);
			Properties props = new Properties();
			props.load(reader);

			String p = props.getProperty("Periodicidade");
			this.periodicidade = Integer.parseInt(p);
			reader.close();
		} catch (IOException e) {
			if (e instanceof FileNotFoundException) {
				try {
					Properties props = new Properties();
					props.setProperty("Topico", "iscte_sid_2018_S1");
					props.setProperty("Broker", "tcp://iot.eclipse.org:1883");
					props.setProperty("Periocidade", "30");
					FileWriter writer = new FileWriter(configFile);
					props.store(writer, "Default settings");
					writer.close();
				} catch (IOException e2) {

				}
			}
		}
	}

}
