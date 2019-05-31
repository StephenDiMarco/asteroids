package code;

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
		resetKeys();
		
		distance = getDistance(target.position);
		range = getRange();
		
		//Scanning surrounding pace for threats
		int asteroidIndex = asteroidsInRange();
		
		if(asteroidIndex > -1){
			avoid(asteroids.get(asteroidIndex).getTransform().getPosition());
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
	private double TARGET_TURN_MODIFIER = 10;
	//Tolerance (angle) modifiers
	private double EARLY_TARGET_TURN_ACC_MODIFIER = 20;
	private double EARLY_AST_TURN_ACC_MODIFIER = 0.5;
	
	private void track(){
		double angleToTarget = getAngleToTarget(target.position);        
		double absDiff  = getDiffInAngleToTarget(angleToTarget);

	    //Targeting Player
		if(absDiff <= TARGETTING_TOLERANCE){
			boolean accelerate = distance > range/4;
			setRotationTowardsTarget(angleToTarget);
			if(distance > range){
				setAcceleration(accelerate, TARGET_STRAIGHT_MODIFIER);
			}
			checkFireRange();
		}else{
			setRotationTowardsTarget(angleToTarget);
			//If ship is angled somewhat towards its target will accelerate away while still rotating
			if(absDiff <= EARLY_TARGET_TURN_ACC_MODIFIER*TARGETTING_TOLERANCE){
				setAcceleration(true, TARGET_TURN_MODIFIER);
			}else{
				setAcceleration(false, 0);
			}
		}
	}

	private void setRotationTowardsTarget(double angleToTarget) {
		if(angleToTarget < ship.rotation || angleToTarget > ship.rotation + 180){
			keyRotateCCW = true;
			keyRotateCW = false;
		}else{
			keyRotateCCW = false;
			keyRotateCW = true;
		}
	}
	
	private double getRange(){
		return ship.getBulletRange();
	}
	
	//Modifies acceleration booleans and value
	private void setAcceleration(boolean accelerate, double factor){
		if(accelerate){
			keyAccelerate = true;
			keyStabilize = false;
			ship.setAcceleration(ship.getMaxAcceleration()/factor);
		}
	}
	
	//Checks whether ai is in range to fire
	private void checkFireRange(){
		if(distance <= range){
			keyFire = true;
		}
	}
	
	private double getDistance(Point point){
		double deltaX = ship.position.x - point.x;
		double deltaY = ship.position.y - point.y;
		return Math.sqrt((deltaX)*(deltaX)+(deltaY)*(deltaY));
	}
	
	private double  getAngleToTarget(Point point){
		double angle = -Math.atan2(point.x - ship.position.x, point.y - ship.position.y);
		angle += Math.PI;
		angle = Math.toDegrees(angle);
		return angle;
	}
	
	private double getDiffInAngleToTarget(double angle){
		return Math.abs(angle - ship.rotation);
	}
	
	/******************************************* Collision Avoidance Methods *****************************************/
	private int AVOIDANCE_TOLERANCE = 220; //The angle tolerance for facing away 

	//Method scans all asteroid checking for any in range
	private int asteroidsInRange(){
		//Assuming no avoidance necessary, turning flag off
		double distance = SCANNER_RANGE;
		double minDistance = SCANNER_RANGE;
		int index = -1;
		
		for(int i = 0; i < asteroids.size(); i++){
			distance = getDistance(asteroids.get(i).getTransform().getPosition());
			if(distance < minDistance){
				minDistance = distance;
				index = i;
			}
		}
		return index;
	}

	private void avoid(Point object) {
		double angleToTarget = getAngleToTarget(object);            	
		double absDiff = getDiffInAngleToTarget(angleToTarget); 

		//If ship is sufficiently facing away from the asteroid it will accelerate away
		if(absDiff >= AVOIDANCE_TOLERANCE){
			keyRotateCCW = false;
			keyRotateCW = false;
			//Fleeing at increased rate
			setAcceleration(true, AST_AVOID_STRAIGHT_MODIFIER);
		}else{
			setRotationAwayFromTarget(angleToTarget);

			//If ship is angled somewhat will accelerate away while still rotating
			if(absDiff >= EARLY_AST_TURN_ACC_MODIFIER*AVOIDANCE_TOLERANCE){
				setAcceleration(true, AST_AVOID_TURN_MODIFIER);
			}else{
				setAcceleration(false, 0);
			}
		}
		
	}
	
	private void setRotationAwayFromTarget(double angleToTarget) {
		if(angleToTarget < ship.rotation || angleToTarget > ship.rotation + 180){
			keyRotateCCW = false;
			keyRotateCW = true;
		}else{
			keyRotateCCW = true;
			keyRotateCW = false;
		}
	}

}
