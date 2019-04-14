package gameContent;

import java.util.ArrayList;

public class aiShip extends Ship{
	
	private playerShip target;               //Uses to target player
	private boolean keyLockedOn;             //Flags whether ai is locked onto player.
	private boolean keyAvoidance;            //Used to notify ship when to flee    
	private ArrayList<Asteroid> asteroids;   //Used by AI for avoidance
	private double SCANNER_RANGE = 100;      //Used as range for collision avoidance
	private double distance;                 //Distance from target
	private double range;                    //Range of the ships ammunition
	
	public aiShip(Point[] inShape, Point inPosition, int timeInterval, playerShip target,  ArrayList<Bullet> bullets, ArrayList<Asteroid> asteroids){
		super(inShape, inPosition, timeInterval,  bullets);
		this.target = target;
		this .asteroids = asteroids;
		//Establishing weapon charge properties
		MAX_CHARGE = CHARGE = 15;CHARGE_RATE = 0.025;bullets = new ArrayList<Bullet>();
		//Establishing weapon strength properties
		STRENGTH = 1;BULLET_RANGE = 125;FIRE_DELAY_TIME = 1; FIRE_DELAY  = 0.0;     //No delay left
		//Establishing Engine properties
		ACCELERATION = MAX_ACCELERATION = 0.0105;ANGULAR_VELOCITY = .6;STABILIZE_COEFF = 0.97;
		//Initialising keys are false
		keyRotateCW = false; keyRotateCCW = false; keyAccelerate = false; 
		keyStabilize = false; keyFire = false;
	}
	
	/********************************************************** AI Controls *********************************************/
	
	public void updateAI(){
		//Updating ai targeting information
		distance = getDistance(target.position);
		range = getRange();
		//Scanning surrounding pace for threats
		checkInRange();
		//If no immeninent threat is near, targeting enemy and pursuing 
		if(!keyAvoidance){
			//Setting targeting tolerance for ship, and sending true as to attempt to target and not flee
			findTarget(TARGETTING_TOLERANCE, !keyAvoidance, target.position);
			//Finds targets angle and distance, rotating ship accordingly
			checkFireRange();
		}

	}
	
	//Finds target to avoid or chase 
	private double TARGETTING_TOLERANCE = 2;
	//Acceleration modifiers
	private double AST_AVOID_TURN_MODIFIER = 2;
	private double AST_AVOID_STRAIGHT_MODIFIER = 0.8;
	private double TARGET_TURN_MODIFIER = 4;
	//Tolerance (angle) modifiers
	private double EARLY_TARGET_TURN_ACC_MODIFIER = 20;
	private double EARLY_AST_TURN_ACC_MODIFIER = 0.5;
	
	private void findTarget(double tolerance, boolean chasing, Point object){
		/************************************* Setting up angles ******************************************/
		double angle = getAngle(object);            //Angle of aiShip to object
		double diff = getDiffAngle(object);         //Difference in angle between the rotation of the ship and the angle calculated above
		double absDiff = Math.abs(angle-rotation);  //Yet another difference angle.
		/************************************* Checking Direction ******************************************/
	    //Targeting Player
		if(absDiff <= tolerance && chasing){
			keyRotateCCW = false;
			keyRotateCW = false;
			keyLockedOn = true;
			pathFinding();
			return;
		}else if (chasing){
			if(diff <= 0){
			keyRotateCCW = true;
			keyRotateCW = false;
			}else{
			keyRotateCCW = false;
			keyRotateCW = true;
			}
			//If ship is angled somewhat towards its target will accelerate away while still rotating
			if(absDiff <= EARLY_TARGET_TURN_ACC_MODIFIER*tolerance){
				setAcceleration(true, TARGET_TURN_MODIFIER);
			}else{
				keyAccelerate = false;
				keyStabilize = true;
			}
			return;
		}
		
		/******************************** Avoidance Algorithm *******************************/
		//If ship is sufficiently facing away from the asteroid it will accelerate away
		if(absDiff >= tolerance && !chasing){
			keyRotateCCW = false;
			keyRotateCW = false;
			//Fleeing at increased rate
			setAcceleration(true, AST_AVOID_STRAIGHT_MODIFIER);
			keyLockedOn = false;
			//Ship must continue to turn away
		}else if(!chasing){
			if(diff >= 0){
				keyRotateCCW = true;
				keyRotateCW = false;
			}else{
				keyRotateCCW = false;
				keyRotateCW = true;
			}
			keyLockedOn = false;
			//If ship is angled somewhat will accelerate away while still rotating
			if(absDiff >= EARLY_AST_TURN_ACC_MODIFIER*tolerance){
				setAcceleration(true, AST_AVOID_TURN_MODIFIER);
			}else{
				setAcceleration(false, 0);
			}
		}
	}	
	//Calculates the aiShips range
	private double getRange(){
		return (getVelocity()+2*STRENGTH)*(timeInterval*BULLET_RANGE);
	}
	//Modifies acceleration booleans and value
	private void setAcceleration(boolean accelerate, double factor){
		if(accelerate){
			keyAccelerate = true;
			keyStabilize = false;
			ACCELERATION = MAX_ACCELERATION/factor;
		}else{
			keyAccelerate = true;
			keyStabilize = false;
		}
	}
	//Will be improved later, for now heads to player when locked on.
	private void pathFinding(){
		//If within close enough proximity ship will stop
		if(keyLockedOn && distance > range/2){
			keyStabilize = false;
			keyAccelerate = true;
		}else{
			keyStabilize = true;
			keyAccelerate = false;
		}
	}
	//Checks whether ai is in range to fire
	private void checkFireRange(){
		//Assuming they shouldn't fire
		keyFire = false;
		if(keyLockedOn){
			//Calculating the distance between ai and player
			if(distance <= range){
				keyFire = true;
			}
		}
	}
	
	private double getDistance(Point point){
		double deltaX = position.x - point.x;
		double deltaY = position.y - point.y;
		return Math.sqrt((deltaX)*(deltaX)+(deltaY)*(deltaY));
	}
	
	private double getDiffAngle(Point point){
		double angle = getAngle(point);
		if(rotation < 0){
			rotation += 360;
		}	
		
		double diff = angle - rotation;
		diff = (diff + 180) % 360 - 180;
		return diff;
	}
	
	private double  getAngle(Point point){
		double angle = -Math.atan2(point.x - position.x, point.y - position.y);
		angle += Math.PI;
		angle = Math.toDegrees(angle);
		if(angle < 0){
			angle += 360;
		}
		return angle;
	}
	/******************************************* Collision Avoidance Methods *****************************************/
	//Method scans all asteroid checking for any in range
	private void checkInRange(){
		//Assuming no avoidance necessary, turning flag off
		keyAvoidance = false;
		for(int i = 0; i < asteroids.size(); i++){
			if(SCANNER_RANGE > getDistance(asteroids.get(i).position)){
				checkCollision(i);
			}
		}
	}
	
	private int AVOIDANCE_TOLERANCE = 180; //The number of frames to run the test for
	//Check is the asteroid and sip will collide
	private void checkCollision(int index){
		//Denoting that ship is not locking onto target but fleeing
		keyLockedOn = false;
		//Creating temporary variables to project the ships ad asteroids path.
		keyAvoidance = true;
		findTarget(AVOIDANCE_TOLERANCE, keyLockedOn, asteroids.get(index).position);
	}
}
