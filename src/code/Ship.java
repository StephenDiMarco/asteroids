package code;
import java.util.ArrayList;

public class Ship extends ColliderSpriteGameObject {
    private Controller controller;
	//Weapon attributes, all accessible fields
	protected ArrayList<Bullet> bullets;         //Will be used for AIs fire as well
    private ShipAttributes attributes;
	public int lives;

	//Sounds
	private AudioManager audioManager;
	private String weaponSound;
	
	/************************************** Constructors ****************************************/
	public Ship(Transform transform, PolygonCollider collider, ColliderSprite sprite,  ArrayList<Bullet> bullets, Controller controller, ShipAttributes attributes, AudioManager audioManager, String weaponSound){
		super(transform, collider, sprite);
		//Setting time interval
		this.bullets = bullets;
		this.controller = controller;
		this.attributes = attributes;
		this.audioManager = audioManager;
		this.weaponSound = weaponSound;
	}

	/************************************** Getter Methods ****************************************/
	//Weapon's getters
	public double getCharge(){return attributes.CHARGE;}
	public double getMaxCharge(){return attributes.MAX_CHARGE;}
	public double getStrength(){return attributes.STRENGTH;}
	public double getBulletRange(){return attributes.BULLET_RANGE;}
	
    //Shield Charge getters
	public double getShield(){return attributes.SHIELDS;}
	public double getMaxShield(){return attributes.MAX_SHIELDS;}

	//Velocity Getters
	public double getAcceleration() {return attributes.ACCELERATION;}
	public void setAcceleration(double value) { attributes.ACCELERATION = value;}
	public double getMaxAcceleration() {return attributes.MAX_ACCELERATION;}
	
	public Controller getController() { return controller; }
	
	public boolean hasOvershields() { return attributes.OVERSHIELDS; }
	
	/************************************** Update Method ****************************************/

	//Updates ship
	 public void update(){
		 controller.update();
		 
		 getTransform().update();
		 recharge();
		 //Checking overshield
		 if(attributes.OVERSHIELDS){
			 attributes.OVERSHIELDS_DURATION--;
			 if(attributes.OVERSHIELDS_DURATION < 0){
				 attributes.OVERSHIELDS = false;
			 }	
		}
			 
		 //Checking Rotation
		 if(controller.isKeyRotateCW() && !controller.isKeyRotateCCW()){
			 getTransform().rotate(attributes.ANGULAR_VELOCITY);
		 }else if(!controller.isKeyRotateCW() && controller.isKeyRotateCCW()){
			 getTransform().rotate(-attributes.ANGULAR_VELOCITY);
		 }
		 //Checking Thrusters (Acceleration)
		 if(controller.isKeyAccelerate()){
			getTransform().accelerate(attributes.ACCELERATION, attributes.MAX_VELOCITY);
		 }
		//Checking Stabilizers 
		 if(controller.isKeyStabilize()){
			 stabilize();
		 }
		//Checking Fire Delay and Fire trigger
		 if(controller.isKeyFire() && attributes.FIRE_DELAY <= 0){
			 fire();
		 }else if(attributes.FIRE_DELAY > 0){
			 attributes.FIRE_DELAY -= 0.1;
		 }
		 super.update();
	 }
	 
	 
	 /************************ Shield System Methods **************************/
	 //Hitting ship and returning value
	 public double hit(double Strength){
		 return attributes.SHIELDS = attributes.SHIELDS - Strength;
	 }
	 //Restoring Shields
	 public void restoreShields(){
		 attributes.SHIELDS = attributes.MAX_SHIELDS;
	 }
	 //Restoring OverShields
	 public void restoreOverShields(){
		 attributes.OVERSHIELDS = true;
		 attributes.OVERSHIELDS_DURATION = 200;
	 }

	 /************************ Engine System Methods **************************/

	 private void stabilize(){
		 xVelocity *= attributes.STABILIZE_COEFF;
		 yVelocity *= attributes.STABILIZE_COEFF;
		 //At a particular speed ship will be stopped, this minimum speed increases with stabilize coeff.
		 if(getVelocity() < (0.1/attributes.STABILIZE_COEFF)){
			 yVelocity =  xVelocity = 0;
		 }
	 }

	 /********************************* Weapon Methods *********************************/
	 private void fire(){
		if(attributes.CHARGE > 0){
			audioManager.playOnce(weaponSound);
			bullets.add( new Bullet(getTranmform(), (int)Math.round(attributes.STRENGTH), attributes.STRENGTH, attributes.BULLET_RANGE);
			//Decrementing the charge	
			attributes.CHARGE--;
			//Adding delay to next shot
			attributes.FIRE_DELAY = attributes.FIRE_DELAY_TIME;
		}
	 }
	 
	 private void recharge(){
		 //Checking if the charge is capable of charging
		 if(attributes.CHARGE <= attributes.MAX_CHARGE){
			 attributes.CHARGE += attributes.CHARGE_RATE;
			 //Ensuring it did not overshoot cap
			 if(attributes.CHARGE > attributes.MAX_CHARGE){
				 attributes.CHARGE = attributes.MAX_CHARGE;
			 }
		 }
	 }
	 
	 public void upgrade(ShipAttributes.ModifiableAttributeTypes type, float modifier){
		 audioManager.playOnce(audioManager.UPGRADE_PICKUP);
		 attributes.modifyAttribute(type, modifier);
		 
		 switch(type){
	 		case SHIELDS:  
	 			if(attributes.SHIELDS == attributes.MAX_SHIELDS) {
	 				lives++; 
	 			}else {
	 				restoreShields(); 
	 			}
	   			break;
	 		case MAX_SHIELDS:  
	 			restoreShields(); 
	   			break;
		 }
	 }
	 
	 public void death(){
		 lives--;
		 restoreShields();
		 restoreOverShields();
		 attributes.CHARGE = attributes.MAX_CHARGE;
	 }
	 
	 private int SHIP_BASE_SCORE = 40;
	 
	 public int score() {
		 return SHIP_BASE_SCORE*(int)attributes.STRENGTH;
	 }
	 
}


