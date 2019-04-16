package gameContent;
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
		Upgrades upgrade = gsonUtility.deserializeFile("upgrades/shield-capacity.json", Upgrades.class);
		upgrade.position = position;
		System.out.println(gsonUtility.serialize(upgrade));
		return upgrade;
	}
}
