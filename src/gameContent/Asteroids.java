package gameContent;
/*
CLASS: Asteroids
DESCRIPTION: Extending Game, Asteroids is all in the paint method.
NOTE: This class is the metaphorical "main method" of your program,
      it is your control center.
*/

import java.awt.*;
import java.util.ArrayList;
import java.awt.geom.Path2D;

@SuppressWarnings("serial")
public class Asteroids extends Game{

	static protected int SCREEN_WIDTH = 900;
	static protected int SCREEN_HEIGHT = 650;
	static protected int MAX_POINTS = 15; //Max number of points on an asteroid
	static protected int MIN_DISTANCE = 60; //The minimum distance an asteroid can spawn away from a player
	static protected int NUM_STARS = 130;
	static protected boolean updateThread;
	//Stores game objects
	private playerShip ship;
	protected ArrayList<Bullet> playerBullets;         //Will be used for AIs fire as well
	private ArrayList <Asteroid> asteroids;
	private ArrayList <aiShip> ships;
	protected ArrayList<Bullet> aiBullets;         //Will be used for AIs fire as well
	private Star[] stars;
	private Upgrades upgrade; //Only one can appear on screen at a time
	//Stats
	private int level;
	private int score;
	//Server Connection
	@SuppressWarnings("unused")
	private ServerConnection serverConnection;
	//Custom Colors
	protected Color brown = new Color(139,75,60);
	
 //Constructor	
  public Asteroids() {
	newGame();              
    //Setting updateThread to false to begin;
    updateThread = false;

  }
	 public void newGame(){
		 //Submitting score over 0
		 if(score>0){
			 serverConnection = new ServerConnection(score);
		 }
		 this.level = 0;
		 //Resetting enemy base counts
		 BASE_ASTERIOD_COUNT = 2;
		 BASE_AISHIP_COUNT = 3;
		 //Resetting upgrade
		 upgrade = null;
		 //Creating the ship and its bullets
		 playerBullets = new ArrayList<Bullet>();
		 this.ship = (playerShip)createShip(SHIP_SHAPE,new Point(SCREEN_WIDTH/2,SCREEN_HEIGHT/2), true);
		   
		 //Creating the control interface
		  this.addKeyListener(ship); 

		 //Initialising ArrayList
		 asteroids = new ArrayList<Asteroid>(); 
		    
		 //Creating ship ArrayList
		 aiBullets = new ArrayList<Bullet>();
		 ships = new ArrayList<aiShip>();
		 this.ship.lives = 3;     
		  //Creating the stars
		 stars = new Star[NUM_STARS];

		 //Starting new level, creates enemies.
		 newLevel();
		 //Setting score to zero
		 this.score = 0;
	 }
  
  @Override
  public void paint(Graphics g) {
	  //Checking for new game
	  if(getNewGame()){
		  //Creating new game
		  newGame();
		  //Resetting new game flag
		  setNewGame(false);
		  //Unpausing Screen updates
		  keyPause = false;
	  }
	  //Checking for pause
	  if(!getPause()){
		Graphics2D  brush = (Graphics2D)g;
		//Setting background of space
	    brush.setColor(Color.black);  
	    brush.fillRect(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
	    //Adding Stars
	    brush.setColor(Color.white);
	    updateStars(brush);
	    //Adding Ship
	    updateShip(brush);
	    //Adding the aiShips
	    updateAIShip(brush);
	    //Adding Upgrades
	    updateUpgrade(brush);
	    //Updating then drawing all asteroids in array
	    updateAsteriods(brush);
	    //Screen HUD
	    updateHUD(brush);
	    //Checking  for screen overlay
	    if(screenOverlay > 0){
	    	//Decrementing count down
	    	screenOverlay--;
	    	updateScreenOverlay(brush);
	    	
	    }
	     //Collision detction
	     collisionCheck();
	     //Checking Status - level completion, game over
	     gameStatus();
	     
	    if(updateThread == false){
	    	//Preventing paint from initiating the update loop
	    	   updateThread = true;
	    	   update(brush);
	    } 
	  }else{
		  Graphics2D  brush = (Graphics2D)g;
		  updateScreenOverlay(brush);
	  }
  }
  
  /************************************     Shape Creation         *************************************/
  private Path2D createShape(Polygon polygon){
	  
	  Point[] tempPoints = polygon.getPoints();
	  //Creating a shape
	  Path2D.Double path = new Path2D.Double();
	  //Creating the first point
	  path.moveTo(tempPoints[0].x, tempPoints[0].y);
	  //Creating the remaining points
	  for( int i = 1; i < tempPoints.length; i++) {
			path.lineTo(tempPoints[i].x, tempPoints[i].y);
	  }
	  return path;
  }
  /************************************     Object Creation         *************************************/
  //Creates a number of Asteroids depenedent on player level
  private void createAsteroids(int numAsteroids, int levelAster, Point origin){
	 //Start of a new level, no specific location chosen.
	  	  Point[] tempPoints = new Point[MAX_POINTS];
		  //Creating proper number of asteroids for level
		  for(int i = 0; i <numAsteroids; i++){
			  //Creating Asteroid Shape in a static method in Asteroid
			  tempPoints = createAsteroid(levelAster);
			  //Placing it in a free location
			  Point inPosition;
			  if(origin == null){
				  inPosition = findLocation();
			  }else{
				  inPosition = new Point(origin.x+10*levelAster*Math.random(), origin.y+10*levelAster*Math.random());
			  }
			  //Creating new Asteroid
			  asteroids.add(new Asteroid(tempPoints, inPosition, TIME_INTERVAL, levelAster));
			  //Increasing the enemies counter.
		     System.out.println(i);
		  }
  
  }

	//Creates an asteroid
     private double BASE_RADIUS = 200;
	 private  Point[] createAsteroid(int Asteroidlevel){
			//Drawing asteroid shape, begining with at least 5 points, increasing with size
		    int nPoints = (int)(4+Asteroidlevel+Math.floor(8*Math.random()));
		    Point[] asterShape = new Point[nPoints];
		    //Creating Asteroid shape, the sqrt function gives a leveling in the size increase with each level
		    double radius = Math.sqrt(BASE_RADIUS*Math.sqrt(Asteroidlevel));
		    //Creating first point
		    asterShape[0] = new Point(0,radius);
		    //Creating points other points with bumpy texture
		    for(int i = 1; i < nPoints; i++){
		    	//Creating altered x,y pos on circle
		    	int x = (int)Math.round(radius*(Math.sin(2*Math.PI*i/nPoints))+5-(int)Math.round((8+Asteroidlevel*1.5)*Math.random()));
		    	int y = (int)Math.round(radius*(Math.cos(2*Math.PI*i/nPoints))+5-(int)Math.round((8+Asteroidlevel*1.5)*Math.random()));
		    	asterShape[i] = new Point(x,y);
		    }
		    //Creating ship
		    return asterShape;
	 }
	 
	 /***********************************      Object Definitions        ***************************************/
	 //Array of points to be parsed and made into predefined objects
	 
	 //Ship array
	 protected double[] SHIP_SHAPE = {10,-19, 9.25,-19, 7,-5.5, 0,-3, 0,0, 7,0, 7,3, 9,3, 9,0, 
			 							11,0, 11,3, 13,3, 13,0, 20,0, 20,-3, 13,-5.5, 10.75,-19};
	//Ship array
    protected double[] ALIEN_SHIP_SHAPE = {0,0, 2,1, 2,2.5, 5,4, 5,-11, 7,-12, 9,-9, 11,-2, 12,5, 
				 							8,9, 3,6, -3,6, -8,9, -12,5, -11,-2, -9,-9, -7,-12, -5,-11, -5,4,
				 							-2,2.5, -2,1};
	 //Upgrade Arrays
	 //Strength Upgrade (Missile Shape)
	 protected double[] STRENGTH_UPGRADE = {0,0, 0,-2, 2,-3, 2,-9, 3,-10, 4,-9, 4,-3, 6,-2, 6,0};
	 protected int[] STRENGTH_UPGRADE_COLOR = {216,191,216};
	 
	 //Stabilzer Upgrade, allows player to slow down faster (Shield Shape)
	 protected double[] STABLIZE_COEFF_UPGRADE = {0,0, 3,0, 7,-4, 5,-6, -2,-6, -4,-4};
	 protected int[]  STABLIZE_COEFF_UPGRADE_COLOR = {255,160,122};
	 //Range Upgrade (Range Upgrade)
	 protected double[] RANGE_UPGRADE = {0,0, 4,-6, 0,-12, -4,-6};
	 protected int[] RANGE_UPGRADE_COLOR = {102,205,170};
	 
	 //Charge Capacity Upgrade (Box Shape)
	 protected double[] CHARGE_UPGRADE = {0,0, 5,0, 5,-5, 0,-5};
	 protected int[] CHARGE_UPGRADE_COLOR = {255,215,0};
	 
	 //Increase Health (Large Heart Shape)
	 protected double[] SHIELDS_INCREASE = {0,0, 5,-5, 4,-8, 2,-8, 0,-7, -2,-8, -4,-8, -5,-5};
	 protected int[] SHIELDS_INCREASE_COLOR = {164,25,25};
	 
	//Restore Health (Small Heart Shape)
	protected double[] SHIELDS_RESTORE = {0,0, 4,-4, 2,-6, 1,-6, 0,-5, -1,-6, -2,-6, -4,-4};
	protected int[] SHIELDS_RESTORE_COLOR = {164,25,25};
	 
	 //Filling double arrays to randomly select upgrades and their color and their name
	 protected double[][] UPGRADE_ARRAY = {STRENGTH_UPGRADE,STABLIZE_COEFF_UPGRADE,RANGE_UPGRADE,CHARGE_UPGRADE,SHIELDS_INCREASE,SHIELDS_RESTORE};
	 protected int[][] COLOR_ARRAY = {STRENGTH_UPGRADE_COLOR,STABLIZE_COEFF_UPGRADE_COLOR,RANGE_UPGRADE_COLOR,CHARGE_UPGRADE_COLOR,SHIELDS_INCREASE_COLOR,SHIELDS_RESTORE_COLOR};
	 protected String[] UPGRADE_NAME_ARRAY = {"Weapon Strength Upgrade","Stabilizer Upgrade","Weapon Range Upgrade","Weapon Capacity Upgrade","Shield Upgrade","Shield Recharge"};
	 
	 private Polygon createObject(double[] shape, Point inPosition){
		//Drawing object shape, begining
	    Point[] shapePoints = new Point[(shape.length)/2];
	    //Filling in points
	    for(int i= 0, j = 0; i<shapePoints.length; i++, j+=2){
	    	shapePoints[i] = new Point(shape[j],shape[j+1]);
	    }
	    //Creating object
	    return new Polygon(shapePoints, inPosition, TIME_INTERVAL);
	 }
	 
	 //CreatesShip
	 @SuppressWarnings("unchecked")
	private <T extends Ship>T createShip(double[] shape, Point inPosition, boolean player){
		 //Creating ship from generic polygon object
		 Polygon shipShape = createObject(shape, inPosition);
		 //Determining whether AI or player ship
		 if(player){
			 return ((T)new playerShip(shipShape.getShape(), shipShape.position, TIME_INTERVAL, playerBullets));
		 }else{
			 //player's ship will be made, sent as target
			 return (T)new aiShip(shipShape.getShape(), shipShape.position, TIME_INTERVAL, ship, aiBullets, asteroids);
		 }
	 }
	 //Creating ships
	 private void createShips(int numShips, double[] shape){
		 for(int i = 0; i < numShips; i++){
		 	ships.add((aiShip)createShip(shape, findLocation(), false));
		 }
	 }
	 
	 //Creates Ship
	 private Upgrades createUpgrade(double[] shape, Point inPosition, int modType, int duration, int[] rbg){
		 //Creating ship from generic polygon object
		 Polygon upgrade = createObject(shape, inPosition);
		 return new Upgrades(upgrade.getShape(), upgrade.position, modType, duration, rbg);
	 }
	 
	 
 
	 
	 private void createStars(){
		 for(int i = 0; i < NUM_STARS; i++){
			 //Randomly placing and sizing stars
			 stars[i] = new Star(Math.round(SCREEN_WIDTH*Math.random()),Math.round(SCREEN_HEIGHT*Math.random()),(i%3+1), false);
		 } 
	 }

	 
	 /****************************************     Collision Detection     *****************************************************/
	 //Checks objects for collisions or out of bounds
	 private void collisionCheck(){
		 isOutOfBounds(ship);
		 //Checking if the ship has hit any upgrades
		 if(upgrade != null){	
		 	if(ship.intersect(upgrade)){
				 ship.modWeapons(upgrade.getModType());
				 //Adding overlay of upgrade acquired
				 screenOverlayMessage = UPGRADE_NAME_ARRAY[upgrade.getModType()];
				 screenOverlay = 75;
				 upgrade = null;
				 //Bonus points to score
				 score += 20;
			 }
		 }
		 
		 //Checking if bullets are out of bounds or are depleted.
		 for(int i=playerBullets.size()-1; i>=0; i--){
			//Checking if shot is depleted or out of bounds
			if(playerBullets.get(i).depleted){
				playerBullets.remove(i);
			}else{
				isOutOfBounds(playerBullets.get(i));
			}
		 }

		 //Checking enemy bullets for depletion or collision
		 for(int i=aiBullets.size()-1; i>=0; i--){
			 //Checking if shot is depleted or out of bounds
			 if(aiBullets.get(i).depleted){
				aiBullets.remove(i);
			 //Checking player ship for collision
			 }else if(ship.contains(aiBullets.get(i).position)) {
				 //Checking for overshields, then hitting ship and checking for death
				 if(!ship.OVERSHIELDS){
					if(ship.hit(aiBullets.get(i).getStrength()) < 0){
						death();
					}
				 }
				aiBullets.remove(i);
			 }else{
				isOutOfBounds(aiBullets.get(i));
			 }
			 
		}
		  
		 //Checking if Asteroids have been hit by bullets, the player or are out of bounds
		 for(int i=0; i< asteroids.size(); i++){
			 //Checking all bullets to see if they overlap with the asteroid
				 for(int j = 0; j< playerBullets.size(); j++){
						 	if(asteroids.get(i).contains(playerBullets.get(j).position)){
						 		//Checking if health is depleted
								boolean destroyed = asteroids.get(i).hit(playerBullets.get(j).getStrength());
								//Destroying bullet
								playerBullets.get(j).depleted = true;
								//Asteroid has been destroyed
								if(destroyed){
									destroyAsteriod(i);
									break;
								}
							 }
					
			 }
				 //Destroyed Asteroids will not be checked.
				if(asteroids.size() != i){
					 //In case shot destroys asteroid as it just before collision.
					 //Checking if the ship has been hit.
					 if(asteroids.get(i).intersect(ship)){
						 //Destroying Asteroid
						 destroyAsteriod(i);
						 if(!ship.OVERSHIELDS){
							 death();
						 }
						 break;
					 }
					 //Checking if it is out of bounds
					 isOutOfBounds(asteroids.get(i));
					 
					  for(int j = 0; j< ships.size(); j++){
						//Checking if the ship has been hit.
							 if(asteroids.get(i).intersect(ships.get(j))){
								 //Destroying Asteroid
								 destroyAsteriod(i);
								 destroyShip(j);
								 break;
							 }
					  }
					}
				}
		 
		//AI Ship Collision Check
		 for(int i = 0; i < ships.size(); i++){
			 isOutOfBounds(ships.get(i));
			 //Checking the players bullets have hit the aiShips
			 for(int j = 0; j < playerBullets.size(); j++){
				 //Checking for collision
				 if(ships.get(i).contains(playerBullets.get(j).position))
				 {
					destroyShip(i);
					break;
				 }
			 }
		 }
	 }
	 
	 //Checks objects for collisions or out of bounds
	 private void isOutOfBounds(Shape shape){
		 if(shape.position.x > SCREEN_WIDTH){
			 shape.position.x = 0;
		 }else if(shape.position.x < 0){
			 shape.position.x = SCREEN_WIDTH;
		 }else if(shape.position.y > SCREEN_HEIGHT){
			 shape.position.y = 0;
		 }else if(shape.position.y < 0){
			 shape.position.y = SCREEN_HEIGHT;
		 }
	 }
	  /****************************************     Update Methods     *****************************************************/
	    private void updateHUD(Graphics2D brush){
	        //Hud Creation
	    	//Lives left
	    	brush.setColor(Color.white);
	        brush.drawString("Lives: "+ship.lives, 10, 20);
	        //Creating charge meter
	        brush.drawString("Charge: ", 10, 35);
	        brush.fillRect(55, 25, (int)Math.round(2*ship.getMaxCharge()), 10);
	        brush.setColor(Color.green);
	        brush.fillRect(56, 26, (int)Math.round(2*(ship.getCharge()))-2, 8);
	        //Creating health meter
	        brush.setColor(Color.white);
	        brush.drawString("Health: ", 10, 50);
	        brush.fillRect(55, 40, (int)Math.round(6*ship.getMaxShield()), 10);
	        brush.setColor(Color.red);
	        brush.fillRect(56, 41, (int)Math.round(6*(ship.getShield()))-2, 8);
	        //Level status
	        brush.setColor(Color.white);
	        brush.drawString("Level: "+level, 10, 65);
	        brush.drawString("Enemies Left: "+(asteroids.size()+ships.size()), 10, 80); 
	        brush.drawString("Score: "+score, 10, 95);
	       }   
	    private void updateAsteriods(Graphics2D brush){
	    	//Updating then drawing all asteroids in array
		    brush.setColor(brown);
		    for(int i = 0; i < asteroids.size() ; i++){
			    	asteroids.get(i).move();
			    	brush.fill(createShape(asteroids.get(i)));
		    }
	    }
	 //Makes checks and paints images
	    private void updateUpgrade(Graphics2D brush){
		    //Drawing the upgrade
		    if(upgrade != null){
		    	brush.setColor(upgrade.getColor());
		    	brush.fill(createShape(upgrade));
		    	//Decrementing upgrade time
		    	upgrade.decreaseDuration();
		    	//Checking if time is depleted
		    	if(upgrade.getDuration() <= 0){
		    		upgrade = null;
		    	}
		    }
	    }
	    private void updateStars(Graphics2D brush){
		    //Setting background stars
		    for(int i = 0; i <NUM_STARS;i++){
		    	//Randomly tinkling star
		    	if(Math.random()*1000>999){
		    		stars[i].flipTwinkle();
		    	}
		    	brush.fillOval((int)stars[i].position.x,(int)stars[i].position.y,stars[i].getRadius(),stars[i].getRadius());
		    }
	    }
	    
	    private void updateShip(Graphics2D brush){    
		    //Updating then drawing ship
		    ship.update();
		    brush.setColor(Color.red);
		    brush.fill(createShape(ship));
		    //Adding overshield
		    if(ship.OVERSHIELDS){
		    	brush.setColor(Color.white);
		    	brush.draw(createShape(ship));
		    }
		    //Updating bullets
		    brush.setColor(Color.green);
		    //Painting bullets
		    for(int i = 0; i <ship.bullets.size(); i++){
		    	   ship.bullets.get(i).move();
		    	   brush.fillOval((int)ship.bullets.get(i).position.x,(int)ship.bullets.get(i).position.y,ship.bullets.get(i).getRadius(),ship.bullets.get(i).getRadius());
		    }
	    }
	    
	    private void updateAIShip(Graphics2D brush){
	    	brush.setColor(Color.magenta);
	    	for(int i = 0; i < ships.size();i++){
	    		ships.get(i).updateAI();
	    		ships.get(i).update();
	 		    brush.fill(createShape(ships.get(i)));		      
	    	}
	        //Painting aiBullets
			for(int j = 0; j <aiBullets.size(); j++){
				aiBullets.get(j).move();
				brush.fillOval((int)aiBullets.get(j).position.x,(int)aiBullets.get(j).position.y,aiBullets.get(j).getRadius(),aiBullets.get(j).getRadius());
			}
	    }
		 private void updateScreenOverlay(Graphics2D brush){
			 brush.setColor(Color.white);
			 brush.setFont(new Font("Overlay",Font.BOLD, 30));
			 if(screenOverlayMessage.equals("Pause")){
			     brush.drawString(screenOverlayMessage, SCREEN_WIDTH/2-6*screenOverlayMessage.length(), SCREEN_HEIGHT/2);
			 }else{
				 brush.drawString(screenOverlayMessage, SCREEN_WIDTH/2-6*screenOverlayMessage.length(), 40);
			 }
		 }
	  /****************************************     Accessory Methods     *****************************************************/
		 //Creates a location for the asteroid to spawn, away from player at randomized locations
		 private Point findLocation(){
			 double x = 0,y = 0;
			 double distance,deltaX,deltaY;
			 //Checks if there is sufficient clearance between player and asteriod upon creation
			 boolean safe = false;
			 while(!safe){
				 x = 0+(int)Math.round(SCREEN_WIDTH*Math.random());
				 y = 0+(int)Math.round(SCREEN_HEIGHT*Math.random());
				 //Checking distance from player
				 deltaX = x - ship.position.x;
				 deltaY = y - ship.position.y;
				 distance = Math.sqrt(deltaX*deltaX+deltaY*deltaY);
				 if(distance > MIN_DISTANCE)
				 {
					 safe = true;
				 }
			 }
			 return new Point(x,y);
		 }
	 /***********  Asteroid Variables     **********/
	 static protected int ASTEROID_DROP_CHANCE = 3;
	 static protected int ASTEROID_BASE_SCORE = 20;
	 static protected int ASTEROID_DROP_DURATION = 750;
	 
	 private void destroyAsteriod(int index){
		 score += ASTEROID_BASE_SCORE*asteroids.get(index).getLevel();
		 //Breaking Asteroid Down into new asteroids
		 if(asteroids.get(index).getLevel() >= 2){
			 //Sends in the number of asteroids to be generated (equal to level, maxs at 4), their level and position.
			 int numAster = asteroids.get(index).getLevel();
			 //Larger asteroids only break in half
			 if(numAster > 4){  
				 numAster = 2;
			 }
			 createAsteroids(numAster,asteroids.get(index).getLevel()-1,asteroids.get(index).position);
		 }else{
			 //Chance item drop
			 if(ASTEROID_DROP_CHANCE*Math.random()>ASTEROID_DROP_CHANCE-1){
				 //Creating upgrade
				 int upgradeNumber = (int)Math.round(((UPGRADE_ARRAY.length-1)*Math.random()));
				 //Sending in double[] holding points, the origin, modType, duration
				 upgrade = createUpgrade(UPGRADE_ARRAY[upgradeNumber],asteroids.get(index).position, upgradeNumber, ASTEROID_DROP_DURATION, COLOR_ARRAY[upgradeNumber] );
			 }
		 }
		 //Destroying Asteroid
		 asteroids.remove(index);
	 }
	 
	 static protected int SHIP_DROP_CHANCE = 2;
	 static protected int SHIP_BASE_SCORE = 40;
	 static protected int SHIP_DROP_DURATION = 750;
	 
	 //Destroys aiShip
	 private void destroyShip(int index){
		 score += SHIP_BASE_SCORE*ships.get(index).STRENGTH;
		
		//Chance item drop
		if(ASTEROID_DROP_CHANCE*Math.random()>ASTEROID_DROP_CHANCE-1){
			 //Creating upgrade
			 int upgradeNumber = (int)Math.round(((UPGRADE_ARRAY.length-1)*Math.random()));
			 //Sending in double[] holding points, the origin, modType, duration
			 upgrade = createUpgrade(UPGRADE_ARRAY[upgradeNumber],ships.get(index).position, upgradeNumber, SHIP_DROP_DURATION, COLOR_ARRAY[upgradeNumber] );
		  }
		 ships.remove(index);
	 }
	 
	 //Checks various game state properties and makes appropriate calls
	 private void gameStatus(){
		 //Checking if level is complete
		 if(asteroids.size()+ships.size() == 0){
			 //Resetting player for new level
			 resetPlayer();
			 //Starting a new level
			 newLevel();
		 }
		 //Checking if game is over
		 if(ship.lives <= 0){
			 screenOverlayMessage = "Game Over";
			 //Submitting Score
			 serverConnection = new ServerConnection(score);
			 keyPause = true;
		 }
	 }
	 //resets Player's location to center of screen
	 private void resetPlayer(){
		//Resetting Ship
		 ship.position.x = SCREEN_WIDTH/2;
		 ship.position.y = SCREEN_HEIGHT/2;
		 ship.rotation = 0;
		 ship.setVelocity(0,0);
	 }

	 //New level Start Up
	 private int BASE_ASTERIOD_COUNT = 2;
	 private int BASE_AISHIP_COUNT = 3;

	 private void newLevel(){
		 //Increasing level and lives
		 level++;
		 ship.lives++;
		 //Creating new stars
		 createStars();
		 //Level completion bonus
		 score += level*50;
		 //Increasing all stats
		 ship.modWeapons(-1);
		 //Creating new enemies
		 if(level%5 == 0){
			 //Setting screen overlay
			 screenOverlay = 150;
			 screenOverlayMessage = "Enemy Space - Level "+level;
			 createShips(BASE_AISHIP_COUNT+level, ALIEN_SHIP_SHAPE);
			 //Easing difficulty moving forward
			 BASE_ASTERIOD_COUNT -= 7;
		 }else{
			 //Setting screen overlay
			 screenOverlay = 150;
			 screenOverlayMessage = "Asteroid Belt - Level "+level;
			 createAsteroids(BASE_ASTERIOD_COUNT+level,level, null);
			 createShips(level, ALIEN_SHIP_SHAPE);
		 }
	 }
	 
	 private void death(){
		 //Reducing lives
		 ship.lives--;
		 //Refilling Shields
		 ship.restoreShields();
		 //Restoring OverShield
		 ship.restoreOverShields();
		 //Recharging ships weapons
		 ship.CHARGE = ship.MAX_CHARGE;
		 //Resetting player's location/velocity/rotation
		 resetPlayer();
	 }


  /****************************************     Applet Settings     *****************************************************/
	 public void init() {
		 setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
	     buffer = createImage(SCREEN_WIDTH, SCREEN_HEIGHT);

	    } 

	    public void start() {
	    }

	    public void stop() {
	    }

	    public void destroy() {
	    }
	    
}