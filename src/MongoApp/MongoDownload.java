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
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
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
	
	MongoClientURI uri = new MongoClientURI("mongodb://teste:teste@localhost/?authSource=LabMDB");
	
	public MongoDownload() {
		loadConfig();
		mongoClient =new MongoClient(uri);		
		database = mongoClient.getDatabase("LabMDB");
		System.out.println("Connection Successful");
		collection = database.getCollection("HumidadeTemperatura");		
		this.run();
		

	}

	public void getNewValues() {
		coll = database.getCollection("HumidadeTemperatura").find(eq("Estado", 0));
		Boolean sucess;
		for (Document doc : coll) {
			System.out.println("A temperatura é " + doc.getString("temperature") + " e a Humidade é " + doc.getString("humidity"));

			// Aqui o valor vai ser enviado para o Sybase
			sucess=sendToSybase(doc);
			if(sucess) {
			Bson filter = Filters.eq("_id", doc.getObjectId("_id"));
			Bson updates = Updates.set("Estado", 1);

			collection.findOneAndUpdate(filter, updates);
				System.out.println("Valores alterados com sucesso");
			}else {
				System.out.println("Valores não alterados");
			}
		}
	}

	private Boolean sendToSybase(Document doc) {	
		Boolean sucess;
        try {
        	
			Connection con = DriverManager.getConnection("jdbc:sqlanywhere:uid=java;pwd=java" );
			Statement stmt = con.createStatement(); 
			
			String dia = doc.getString("date");
			String[] array = dia.split("/"); 
			String novoDia = array[2]+"-"+array[1]+"-"+array[0];
			System.out.println(novoDia);
					
			ResultSet rs = stmt.executeQuery("INSERT INTO admin.HumidadeTemperatura (DataMedicao,HoraMedicao,ValorMedicaoTemperatura,ValorMedicaoHumidade)   "
					+ " VALUES ('"+novoDia+"','"+doc.getString("time")+"',"+doc.getString("temperature")+","+doc.getString("humidity")+")");
			
			stmt.close();
			con.close();
			System.out.println("Valores inseridos no Sybase");			
			sucess= true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Valores não inseridos no Sybase");	
			sucess=false;
		}
		return sucess;		
	}

	@Override
	public void run() {
		while (true) {
			try {
				System.out.println("\n***************** \nComecar espera");
				Thread.sleep(periodicidade);
				getNewValues();
				
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
