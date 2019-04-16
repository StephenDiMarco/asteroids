package gameContent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtility {
	
	GsonBuilder builder;
	
	public GsonUtility() {
        builder = new GsonBuilder();
        builder.setPrettyPrinting().serializeNulls();
	}
	
	public <T> String serialize(T object) {
        Gson gson = builder.create();
        return gson.toJson(object);
	}
	
	public <T> T deserialize(String json, final Class<T> type) {
        Gson gson = builder.create();
        return gson.fromJson(json, type);
	}
	
	public <T> T deserializeFile(String filename, final Class<T> type) {
		try {
	        Gson gson = builder.create();
	        BufferedReader json;
			json = new BufferedReader(new FileReader("json/" + filename));
	        return gson.fromJson(json, type); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
