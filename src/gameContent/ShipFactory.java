package gameContent;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ShipFactory {  	 
	
	private GsonUtility gsonUtility;
	private static String shipPath = "ships/";
	private static String shipFile = shipPath + "ships.json";
	

	//Ship array
	protected double[] SHIP_SHAPE = {10,-19, 9.25,-19, 7,-5.5, 0,-3, 0,0, 7,0, 7,3, 9,3, 9,0, 
			 							11,0, 11,3, 13,3, 13,0, 20,0, 20,-3, 13,-5.5, 10.75,-19};
	
	public ShipFactory(GsonUtility gsonUtility) {
		this.gsonUtility = gsonUtility;
	}

	public Ship createPlayerShip(Point inPosition, ArrayList<Bullet> playerBullets) {
        Polygon shipShape = Utilities.CreateObject(SHIP_SHAPE, inPosition, 0);
        PlayerController controller = new PlayerController();
        ShipAttributes attributes = gsonUtility.deserializeFile("ships/sparrow.json", ShipAttributes.class);
        return new Ship(shipShape.getShape(), shipShape.position, playerBullets, controller, attributes);
	}
}

