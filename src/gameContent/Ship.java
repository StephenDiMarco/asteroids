package gameContent;
import java.util.ArrayList;

public class Ship extends Polygon {
	
	//Button Pressed Variables
	protected boolean keyRotateCW;
	protected boolean keyRotateCCW;
	protected boolean keyAccelerate;
	protected boolean keyStabilize;
	protected boolean keyFire;
	//Weapon attributes, all accessible fields
	protected ArrayList<Bullet> bullets;         //Will be used for AIs fire as well
	protected float STRENGTH;           //Used for strength of bullets
	protected int BULLET_RANGE;         //Determines how far a bullet may travel
	protected double MAX_CHARGE;			//The weapons maximum charge
	protected double CHARGE_RATE;         //Rate at which the weapons charges
	protected double CHARGE;              //Weapons Charge
	protected double FIRE_DELAY_TIME;        //Delay between shots
	protected double FIRE_DELAY;             //Delay time remaining
	public int lives;
	//Shields
	protected double MAX_SHIELDS;
	protected double SHIELDS;
	protected boolean OVERSHIELDS;
	protected double OVERSHIELDS_DURATION;
	//Movement attributes
	protected double MAX_ACCELERATION;  //Sets out maximum acceleration
	protected double ACCELERATION ;     //Used to determine change in velocity due to thrusters
	protected double ANGULAR_VELOCITY;  //Used to determine turning rate
	protected double STABILIZE_COEFF;    //Used to slow down craft
	//State information
	private double xVelocity;
	private double yVelocity;
	protected double timeInterval;
	
	/************************************** Constructors ****************************************/
	public Ship(Point[] inShape, Point inPosition, double timeInterval,  ArrayList<Bullet> bullets){
		super(inShape, inPosition, 0);
		//Setting time interval
		this.timeInterval = timeInterval/10;
		this.bullets = bullets;
	}
	//Creates a copy of the ship
	public Ship createCopy(){
		return new Ship(getShape(), position, timeInterval, bullets);
	}
	/************************************** Getter Methods ****************************************/
	//Waepon's charge getters
	public double getCharge(){return CHARGE;}
	public double getMaxCharge(){return MAX_CHARGE;}
	
    //Shield Charge getters
	public double getShield(){return SHIELDS;}
	public double getMaxShield(){return MAX_SHIELDS;}

	//Velocity Getters
	public double getVelocity(){	return Math.sqrt(xVelocity*xVelocity+yVelocity*yVelocity);}
	public double getXVel(){	return xVelocity;}
	public double getYVel(){	return yVelocity;}
	
	/************************************** Update Method ****************************************/

	//Updates ship
	 public void update(){
		 move();
		 recharge();
		 //Checking overshield
		 if(OVERSHIELDS){
			 OVERSHIELDS_DURATION--;
			 if(OVERSHIELDS_DURATION < 0){
				 OVERSHIELDS = false;
			 }	
		}
			 
		 //Checking Rotation
		 if(keyRotateCW && !keyRotateCCW){
			 rotate(ANGULAR_VELOCITY);
		 }else if(!keyRotateCW && keyRotateCCW){
			 rotate(-ANGULAR_VELOCITY);
		 }
		 //Checking Thrusters (Acceleration)
		 if(keyAccelerate){
			accelerate(ACCELERATION);
		 }
		//Checking Stabilizers 
		 if(keyStabilize){
			 stabilize();
		 }
		//Checking Fire Delay and Fire trigger
		 if(keyFire && FIRE_DELAY <= 0){
			 fire();
		 }else if(FIRE_DELAY > 0){
			 FIRE_DELAY -= 0.1;
		 }
	 }
	 /************************ Shield System Methods **************************/
	 //Hitting ship and returning value
	 public double hit(double Strength){
		 return SHIELDS = SHIELDS - Strength;
	 }
	 //Restoring Shields
	 public void restoreShields(){
		 SHIELDS = MAX_SHIELDS;
	 }
	 //Restoring OverShields
	 public void restoreOverShields(){
			OVERSHIELDS = true;
			OVERSHIELDS_DURATION = 200;
	 }
	 /************************ Engine System Methods **************************/
	public void setVelocity(double xVelocity, double yVelocity){
		this.xVelocity = xVelocity;
		this.yVelocity = yVelocity;
	}
	 //Moves ship
	 private void move(){
		 position.x += xVelocity*timeInterval;
		 position.y += yVelocity*timeInterval;
	 }
	 
	 private void accelerate (double acceleration) {
		 xVelocity += (acceleration * Math.sin(Math.toRadians(rotation)));
		 yVelocity += (acceleration * -Math.cos(Math.toRadians(rotation)));
	 }
	 
	 private void stabilize(){
		 xVelocity *= STABILIZE_COEFF;
		 yVelocity *= STABILIZE_COEFF;
		 //At a particular speed ship will be stopped, this minimum speed increases with stabilize coeff.
		 if(Math.sqrt(xVelocity*xVelocity+yVelocity*yVelocity) < (0.4/STABILIZE_COEFF)){
			 yVelocity =  xVelocity = 0;
		 }
	 }
	 private void rotate(double angularVelocity){
		 rotation += angularVelocity*timeInterval;
		 rotation = rotation %360;
	 }
	 /********************************* Weapon Methods *********************************/
	 private void fire(){
		if(CHARGE > 0){
			bullets.add( new Bullet(position.x, position.y, (int)Math.round(STRENGTH), STRENGTH, timeInterval, rotation, BULLET_RANGE, getXVel(), getYVel()));
			//Decrementing the charge	
			CHARGE--;
			//Adding delay to next shot
			FIRE_DELAY = FIRE_DELAY_TIME;
		}
	 }
	 
	 private void recharge(){
		 //Checking if the charge is capable of charging
		 if(CHARGE <= MAX_CHARGE){
			 CHARGE += CHARGE_RATE;
			 //Ensuring it did not overshoot cap
			 if(CHARGE > MAX_CHARGE){
				 CHARGE = MAX_CHARGE;
			 }
		 }
	 }
	 
	 public void modWeapons(int modType){
		 switch(modType){
		    //Strength increase
		 	case 0:  STRENGTH += .50;
         		break;
            //Stabalizers improved
		 	case 1:  STABILIZE_COEFF -= 0.001;
     			break;
            //Range improved
    		case 2:  BULLET_RANGE += 10; 
         		break;
         	//Capacity increased	
    		case 3:  MAX_CHARGE += 3; 
      			break;
      	    //Health Increase	
    		case 4:  MAX_SHIELDS += 2; restoreShields(); 
      			break;
      		//Restore Health	
    		case 5:  if(SHIELDS == MAX_SHIELDS) lives++; 
    				 else restoreShields(); 
      			break;
         	//Defualt is level up mod, weapon, strength, capacity and range increase
		 	default: STRENGTH += .25; MAX_CHARGE += 2; BULLET_RANGE += 5;STABILIZE_COEFF -= 0.002; CHARGE_RATE += 0.005;
		 		break;
		 }
	 }
	 
}


