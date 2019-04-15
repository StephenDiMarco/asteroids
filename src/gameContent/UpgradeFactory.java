package gameContent;
import static gameContent.ShipAttributes.ModifiableAttributeTypes;

import gameContent.ShipAttributes.ModifiableAttributeTypes;

public class UpgradeFactory {
	
	//Upgrade Arrays
	//Strength Upgrade (Missile Shape)
	private double[] STRENGTH_UPGRADE = {0,0, 0,-2, 2,-3, 2,-9, 3,-10, 4,-9, 4,-3, 6,-2, 6,0};
	private int[] STRENGTH_UPGRADE_COLOR = {216,191,216};
	 
	//Stabilizer Upgrade, allows player to slow down faster (Shield Shape)
	private double[] STABLIZE_COEFF_UPGRADE = {0,0, 3,0, 7,-4, 5,-6, -2,-6, -4,-4};
	private int[]  STABLIZE_COEFF_UPGRADE_COLOR = {255,160,122};
	//Range Upgrade (Range Upgrade)
	private double[] RANGE_UPGRADE = {0,0, 4,-6, 0,-12, -4,-6};
	private int[] RANGE_UPGRADE_COLOR = {102,205,170};
	 
	//Charge Capacity Upgrade (Box Shape)
	private double[] CHARGE_UPGRADE = {0,0, 5,0, 5,-5, 0,-5};
	private int[] CHARGE_UPGRADE_COLOR = {255,215,0};
	 
	//Increase Health (Large Heart Shape)
	private double[] SHIELDS_INCREASE = {0,0, 5,-5, 4,-8, 2,-8, 0,-7, -2,-8, -4,-8, -5,-5};
	private int[] SHIELDS_INCREASE_COLOR = {164,25,25};
	 
	//Restore Health (Small Heart Shape)
	private double[] SHIELDS_RESTORE = {0,0, 4,-4, 2,-6, 1,-6, 0,-5, -1,-6, -2,-6, -4,-4};
	private int[] SHIELDS_RESTORE_COLOR = {164,25,25};
	 
	//Filling double arrays to randomly select upgrades and their color and their name
	private double[][] UPGRADE_ARRAY = {STRENGTH_UPGRADE,STABLIZE_COEFF_UPGRADE,RANGE_UPGRADE,CHARGE_UPGRADE,SHIELDS_INCREASE,SHIELDS_RESTORE};
	private int[][] COLOR_ARRAY = {STRENGTH_UPGRADE_COLOR,STABLIZE_COEFF_UPGRADE_COLOR,RANGE_UPGRADE_COLOR,CHARGE_UPGRADE_COLOR,SHIELDS_INCREASE_COLOR,SHIELDS_RESTORE_COLOR};
	
	private int duration = 750;
		
	public UpgradeFactory() {
		
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
