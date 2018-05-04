package MongoApp;

import com.mongodb.BasicDBObject;

public class Document extends Mongo{

	public Document(String hora, String data, String valorT, String valorH) {
		BasicDBObject document = new BasicDBObject();
		document.put("Hora", hora);
		document.put("Data", data);
		document.put("Estado", 0);		
		document.put("Valor_Temperatura", valorT);
		document.put("Valor_Humidade", valorH);
	}
}
