package gameContent;

import java.util.ArrayList;

public class AiController extends Controller {
	
	private Ship target;               //Uses to target player
	private ArrayList<Asteroid> asteroids;   //Used by AI for avoidance
	private double SCANNER_RANGE = 100;      //Used as range for collision avoidance
	private double distance;                 //Distance from target
	private double range;                    //Range of the ships ammunition
	private Ship ship;
	
	public AiController(Ship target, ArrayList<Asteroid> asteroids){
		super();
		this.target = target;
		this.asteroids = asteroids;
	}
	
	public void setShip(Ship ship) {
		this.ship = ship;
	}
	
	/********************************************************** AI Controls *********************************************/
	
	public void update(){
		//Updating ai targeting information
		distance = getDistance(target.position);
		range = getRange();
		//Scanning surrounding pace for threats
		int asteroidIndex = asteroidsInRange();
		
		if(asteroidIndex > -1){
			avoid(asteroids.get(asteroidIndex).position);
		}else{
			track();
		}
	}
	
	//Finds target to avoid or chase 
	private double TARGETTING_TOLERANCE = 5;
	//Acceleration modifiers
	private double AST_AVOID_TURN_MODIFIER = 2;
	private double AST_AVOID_STRAIGHT_MODIFIER = 0.8;
	private double TARGET_STRAIGHT_MODIFIER = 0.6;
	private double TARGET_TURN_MODIFIER = 4;
	//Tolerance (angle) modifiers
	private double EARLY_TARGET_TURN_ACC_MODIFIER = 20;
	private double EARLY_AST_TURN_ACC_MODIFIER = 0.5;
	
	private void track(){
		double angle = getAngle(target.position);        //Angle of aiShip to object
		double diff  = getDiffAngle(angle);
		double absDiff = Math.abs(diff);  //Yet another difference angle.

		/************************************* Checking Direction ******************************************/
		System.out.println("Tracking");
		System.out.println("angle "  + angle);
		System.out.println("absDiff " + absDiff);
		System.out.println("diff" + diff);
		System.out.println("ship " + ship.rotation);

	    //Targeting Player
		if(absDiff <= TARGETTING_TOLERANCE){
			System.out.println("Within tolerance");
			keyRotateCCW = false;
			keyRotateCW = false;
			boolean accelerate = distance > range/4;
			System.out.println(distance + "m away from target with range" + range/4 + " accelerate " + accelerate);
			setAcceleration(accelerate, TARGET_STRAIGHT_MODIFIER);
			checkFireRange();
		}else{
			System.out.println("Not tolerance" + angle);
			setRotation(diff);
			//If ship is angled somewhat towards its target will accelerate away while still rotating
			if(absDiff <= EARLY_TARGET_TURN_ACC_MODIFIER*TARGETTING_TOLERANCE){
				setAcceleration(true, TARGET_TURN_MODIFIER);
			}else{
				setAcceleration(false, 0);
			}
		}
	}

	private void setRotation(double diff) {
		if(diff <= 180){
			keyRotateCCW = false;
			keyRotateCW = true;
		}else{
			keyRotateCCW = true;
			keyRotateCW = false;
		}
	}

	//Calculates the aiShips range
	private double getRange(){
		return ship.getBulletRange();
	}
	
	//Modifies acceleration booleans and value
	private void setAcceleration(boolean accelerate, double factor){
		if(accelerate){
			keyAccelerate = true;
			keyStabilize = false;
			ship.setAcceleration(ship.getMaxAcceleration()/factor);
		}else{
			keyAccelerate = false;
			keyStabilize = true;
		}
	}
	
	//Checks whether ai is in range to fire
	private void checkFireRange(){
		//Assuming they shouldn't fire
		keyFire = false;
		if(distance <= range){
			keyFire = true;
		}
	}
	
	private double getDistance(Point point){
		double deltaX = ship.position.x - point.x;
		double deltaY = ship.position.y - point.y;
		return Math.sqrt((deltaX)*(deltaX)+(deltaY)*(deltaY));
	}
	
	private double  getAngle(Point point){
		double angle = -Math.atan2(point.x - ship.position.x, point.y - ship.position.y);
		angle += Math.PI;
		angle = Math.toDegrees(angle);
		return angle;
	}
	
	private double getDiffAngle(double angle){
		if(ship.rotation < 0){
			ship.rotation += 360;
		}	
		
		double diff = angle - ship.rotation;
		diff = (diff + 180) % 360 - 180;
		return diff;
	}
	
	/******************************************* Collision Avoidance Methods *****************************************/
	private int AVOIDANCE_TOLERANCE = 180; //The angle tolerance for facing away 

	//Method scans all asteroid checking for any in range
	private int asteroidsInRange(){
		//Assuming no avoidance necessary, turning flag off
		double distance = SCANNER_RANGE;
		double minDistance = SCANNER_RANGE;
		int index = -1;
		
		for(int i = 0; i < asteroids.size(); i++){
			distance = getDistance(asteroids.get(i).position);
			if(distance < minDistance){
				minDistance = distance;
				index = i;
			}
		}
		return index;
	}

	private void avoid(Point object) {
		double angle = getAngle(object);            	 //Angle of aiShip to object
		double absDiff = Math.abs(angle-ship.rotation);  //Yet another difference angle.
		
		//If ship is sufficiently facing away from the asteroid it will accelerate away
		if(absDiff >= AVOIDANCE_TOLERANCE){
			keyRotateCCW = false;
			keyRotateCW = false;
			//Fleeing at increased rate
			setAcceleration(true, AST_AVOID_STRAIGHT_MODIFIER);
		}else{
			setRotation(absDiff);

			//If ship is angled somewhat will accelerate away while still rotating
			if(absDiff >= EARLY_AST_TURN_ACC_MODIFIER*AVOIDANCE_TOLERANCE){
				setAcceleration(true, AST_AVOID_TURN_MODIFIER);
			}else{
				setAcceleration(false, 0);
			}
		}
		
	}
}
