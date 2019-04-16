package gameContent;

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
}
