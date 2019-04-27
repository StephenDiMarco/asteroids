package gameContent;

import java.util.ArrayList;

public class ShipFactory {  	 
	
	private GsonUtility gsonUtility;
	private AudioManager audioManager;
	private static String shipPath = "ships/";
	
    /***********************************      Object Definitions        ***************************************/
    //Array of points to be parsed and made into predefined objects

	//Ship array
    protected double[] ALIEN_SHIP_SHAPE = {0,0, 2,1, 2,2.5, 5,4, 5,-11, 7,-12, 9,-9, 11,-2, 12,5, 
				 							8,9, 3,6, -3,6, -8,9, -12,5, -11,-2, -9,-9, -7,-12, -5,-11, -5,4,
				 							-2,2.5, -2,1};

	//Ship array
	protected double[] SHIP_SHAPE = {10,-19, 9.25,-19, 7,-5.5, 0,-3, 0,0, 7,0, 7,3, 9,3, 9,0, 
			 							11,0, 11,3, 13,3, 13,0, 20,0, 20,-3, 13,-5.5, 10.75,-19};
	
	public ShipFactory(GsonUtility gsonUtility, AudioManager audioManager) {
		this.gsonUtility = gsonUtility;
		this.audioManager = audioManager;
	}

	public Ship createPlayerShip(Point inPosition, ArrayList<Bullet> playerBullets) {
        Polygon shipShape = Utilities.CreateObject(SHIP_SHAPE, inPosition, 0);
        PlayerController controller = new PlayerController();
        ShipAttributes attributes = gsonUtility.deserializeFile(shipPath + "sparrow.json", ShipAttributes.class);
        return new Ship(shipShape.getShape(), shipShape.position, playerBullets, controller, attributes, audioManager, audioManager.PLAYER_WEAPON);
	}
	
    public Ship createAiShip(Point inPosition, String shipType, Ship target, ArrayList<Asteroid> asteroids, ArrayList<Bullet> aiBullets) {
    	Polygon shipShape = Utilities.CreateObject(ALIEN_SHIP_SHAPE, inPosition, 0);
        AiController aiController = new AiController(target, asteroids);
        ShipAttributes attributes = gsonUtility.deserializeFile(shipPath + shipType, ShipAttributes.class);
        Ship ship = new Ship(shipShape.getShape(), shipShape.position, aiBullets, aiController, attributes, audioManager, audioManager.ENEMY_WEAPON);
        AiController shipController = (AiController)ship.getController();
        shipController.setShip(ship);
        return ship;	    
	}
}

