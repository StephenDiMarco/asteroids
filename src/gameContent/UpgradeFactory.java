package gameContent;
import static gameContent.ShipAttributes.ModifiableAttributeTypes;

import gameContent.ShipAttributes.ModifiableAttributeTypes;

public class UpgradeFactory {  	 
	
	private int duration = 750;
	private GsonUtility gsonUtility;
	
	public UpgradeFactory(GsonUtility gsonUtility) {
		this.gsonUtility = gsonUtility;
	}
	
	public Upgrades createRandomUpgrade(Point position) {
		ModifiableAttributeTypes type = ModifiableAttributeTypes.Random();
		return createUpgrade(position, type);
	}
	
	public Upgrades createUpgrade(Point position, ModifiableAttributeTypes type) {
		Polygon shape = Utilities.CreateObject(shape, position, 0);
		return new Upgrades(shape, position, type, duration, COLOR_ARRAY[upgradeNumber]);
	}
}
