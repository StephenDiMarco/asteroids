package code;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import com.google.gson.*;

public class GsonUtility {
	
	private GsonBuilder builder;
	private JsonParser jsonParser;
	private String path = "src/json/";
	
	public GsonUtility() {
        builder = new GsonBuilder();
        builder.setPrettyPrinting().serializeNulls();
        builder.registerTypeAdapter(Color.class, colorDeserializer);
        builder.registerTypeAdapter(Color.class, colorSerializer);
        
        jsonParser = new JsonParser();
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
			json = new BufferedReader(new FileReader(path + filename));
	        return gson.fromJson(json, type); 
		} catch (FileNotFoundException | JsonSyntaxException | IllegalArgumentException e) {
			e.printStackTrace(); 
		    return null;
		}
	}
	
	public JsonObject getJsonObjectFromFile(String filename) {
		try {
	        BufferedReader json;
			json = new BufferedReader(new FileReader(path + filename));
			JsonElement jsonElement = jsonParser.parse(json);
			
			if(jsonElement.isJsonObject()) {
				return jsonElement.getAsJsonObject();
			}else{
				throw new FileNotFoundException("Improperly formatted JSON, please encode as a JSON object");
			}
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	
	public <T> boolean deserializationValidator(T object) throws IllegalAccessException {
	    for (Field f : object.getClass().getDeclaredFields()) {
			f.setAccessible(true);
	    	if (f.get(object) == null) {
	            return false;	
	    	}
	    }
	    return true;            
	}
	
	JsonDeserializer<Color> colorDeserializer = new JsonDeserializer<Color>() {  
	    @Override
	    public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
	    	JsonObject jsonObject = json.getAsJsonObject();

	        return new Color(
	                jsonObject.get("r").getAsInt(),
	                jsonObject.get("g").getAsInt(),
	                jsonObject.get("b").getAsInt()
	        );
	    }
	};

	JsonSerializer<Color> colorSerializer = new JsonSerializer<Color>() {  
	    public JsonElement serialize(Color color, Type typeOfSrc, JsonSerializationContext context) {
	    	JsonObject jsonColor = new JsonObject();

	    	jsonColor.addProperty("r", color.getRed());
	    	jsonColor.addProperty("g", color.getGreen());
	    	jsonColor.addProperty("b", color.getBlue());
	    	
	        return jsonColor;
	    }
	};
}
