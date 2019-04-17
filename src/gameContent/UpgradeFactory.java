package gameContent;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import gameContent.ShipAttributes.ModifiableAttributeTypes;

public class UpgradeFactory {  	 
	
	private GsonUtility gsonUtility;
	private static String upgradePath = "upgrades/";
	private static String upgradeFile = upgradePath + "upgrades.json";
	
	public UpgradeFactory(GsonUtility gsonUtility) {
		this.gsonUtility = gsonUtility;
	}
	
	public Upgrades createRandomUpgrade(Point position) {
		ModifiableAttributeTypes type = ModifiableAttributeTypes.Random();
		return createUpgrade(position, type);
	}
	
	public Upgrades createUpgrade(Point position, ModifiableAttributeTypes type) {
		JsonObject filesByAttribute = gsonUtility.getJsonObjectFromFile(upgradeFile);
		
		if(filesByAttribute == null) {
			System.out.println("JSON was not formatted correctly");
			return null;			
		}else{
			return createUpgradeFromJson(position, type, filesByAttribute);	
		}
	}
	
	private Upgrades createUpgradeFromJson(Point position, ModifiableAttributeTypes type, JsonObject filesByAttributeJson) {
		if(filesByAttributeJson.has(type.name())) {
			JsonArray files = filesByAttributeJson.get(type.name()).getAsJsonArray();
			int fileIndex = (int)(Math.random() * files.size());
			String file = files.get(fileIndex).getAsString();
			
			Upgrades upgrade = gsonUtility.deserializeFile(upgradePath +  file, Upgrades.class);
			upgrade.position = position;
			
			return upgrade;	
		}else {
			System.out.println("JSON did not contain an element of type " + type.name());
			return null;
		}
	}
}
