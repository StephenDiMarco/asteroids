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
public class Asteroids extends Game {

    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;
    private int MIN_DISTANCE = 60; //The minimum distance an asteroid can spawn away from a player
    private int NUM_STARS = 130;
    private boolean updateThread;
    
    //Stores game objects
    private Ship ship;
    private ArrayList < Bullet > playerBullets; //Will be used for AIs fire as well
    private ArrayList < Asteroid > asteroids;
    private ArrayList < Ship > ships;
    private ArrayList < Bullet > aiBullets; //Will be used for AIs fire as well
    private ArrayList < Upgrades > upgrades;    
    private Star[] stars;
    
    //Factories
    private GsonUtility gsonUtility;
    private AsteroidFactory asteroidFactory;
    private ShipFactory shipFactory;
    private UpgradeFactory upgradeFactory;

    //Stats
    private int level;
    private int score;
    //Server Connection
    @SuppressWarnings("unused")
    private ServerConnection serverConnection;
    //Custom Colors
    private Color brown = new Color(139, 75, 60);

    //Constructor	
    public Asteroids() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        SCREEN_WIDTH = (int)screenSize.getWidth();
        SCREEN_HEIGHT = (int)screenSize.getHeight();
        
        newGame();
        //Setting updateThread to false to begin;
        updateThread = false;
    }
    
    public void newGame() {
        setNewGame(false);
        keyPause = false;
    	this.score = 0;
        this.level = 0;
        
        //Creating factories
        this.gsonUtility = new GsonUtility();
        this.asteroidFactory = new AsteroidFactory();
        this.shipFactory = new ShipFactory(gsonUtility);
        this.upgradeFactory = new UpgradeFactory(gsonUtility);
 
        //Creating scene object stores
        asteroids = new ArrayList < Asteroid > ();
        playerBullets = new ArrayList < Bullet > ();
        this.ship = shipFactory.createPlayerShip(new Point(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2), playerBullets);
        this.ship.lives = 3;
        this.addKeyListener(((PlayerController)ship.getController()));
        aiBullets = new ArrayList < Bullet > ();
        ships = new ArrayList < Ship > ();
        upgrades = new ArrayList < Upgrades > ();
        stars = new Star[NUM_STARS];

        newLevel();
    }

    @Override
    public void paint(Graphics g) {
    	lastTimeStamp = System.currentTimeMillis();
    	
        if (getNewGame()) {
            newGame();
        }

        if (!getPause()) {
            Graphics2D brush = (Graphics2D) g;
            updateBackground(brush);
            updateStars(brush);
            updateShip(brush);
            updateAIShip(brush);
            updateUpgrade(brush);
            updateAsteriods(brush);
            updateHUD(brush);

            if (screenOverlay > 0) {
                //Decrementing count down
                screenOverlay--;
                updateScreenOverlay(brush);

            }
            //Collision detection
            collisionCheck();
            //Checking Status - level completion, game over
            gameStatus();

            if (updateThread == false) {
                //Preventing paint from initiating the update loop
                updateThread = true;
                update(brush);
            }
        } else {
            Graphics2D brush = (Graphics2D) g;
            updateScreenOverlay(brush);
        }
    }

    /************************************     Shape Creation         *************************************/
    private Path2D createShape(Polygon polygon) {

        Point[] tempPoints = polygon.getPoints();
        //Creating a shape
        Path2D.Double path = new Path2D.Double();
        //Creating the first point
        path.moveTo(tempPoints[0].x, tempPoints[0].y);
        //Creating the remaining points
        for (int i = 1; i < tempPoints.length; i++) {
            path.lineTo(tempPoints[i].x, tempPoints[i].y);
        }
        return path;
    }
    /************************************     Object Creation         *************************************/
    private void createStars() {
        for (int i = 0; i < NUM_STARS; i++) {
            //Randomly placing and sizing stars
            stars[i] = new Star(Math.round(SCREEN_WIDTH * Math.random()), Math.round(SCREEN_HEIGHT * Math.random()), (i % 3 + 1), false);
        }
    }


    /****************************************     Collision Detection     *****************************************************/
    //Checks objects for collisions or out of bounds
    private void collisionCheck() {
        isOutOfBounds(ship);
        //Checking if the ship has hit any upgrades
        if (!upgrades.isEmpty()) {
            for (int index = 0; index < upgrades.size(); index++) {
                if (ship.intersect(upgrades.get(index))) {
                    ship.upgrade(upgrades.get(index).getAttributeType(), upgrades.get(index).getModifier());
                    //Adding overlay of upgrade acquired
                    screenOverlayMessage = upgrades.get(index).getName();
                    screenOverlay = 75;
                    upgrades.remove(index);
                    //Bonus points to score
                    score += 20;
                }            
            } 
        }

        //Checking if bullets are out of bounds or are depleted.
        for (int i = playerBullets.size() - 1; i >= 0; i--) {
            //Checking if shot is depleted or out of bounds
            if (playerBullets.get(i).depleted) {
                playerBullets.remove(i);
            } else {
                isOutOfBounds(playerBullets.get(i));
            }
        }

        //Checking enemy bullets for depletion or collision
        for (int i = aiBullets.size() - 1; i >= 0; i--) {
            //Checking if shot is depleted or out of bounds
            if (aiBullets.get(i).depleted) {
                aiBullets.remove(i);
                //Checking player ship for collision
            } else if (ship.contains(aiBullets.get(i).position)) {
                //Checking for overshields, then hitting ship and checking for death
                if (!ship.hasOvershields()) {
                    if (ship.hit(aiBullets.get(i).getStrength()) < 0) {
                        killPlayer();
                    }
                }
                aiBullets.remove(i);
            } else {
                isOutOfBounds(aiBullets.get(i));
            }

        }

        //Checking if Asteroids have been hit by bullets, the player or are out of bounds
        for (int i = 0; i < asteroids.size(); i++) {
            //Checking all bullets to see if they overlap with the asteroid
            for (int j = 0; j < playerBullets.size(); j++) {
                if (asteroids.get(i).contains(playerBullets.get(j).position)) {
                    //Checking if health is depleted
                    boolean destroyed = asteroids.get(i).hit(playerBullets.get(j).getStrength());
                    //Destroying bullet
                    playerBullets.get(j).depleted = true;
                    //Asteroid has been destroyed
                    if (destroyed) {
                        destroyAsteriod(i);
                        break;
                    }
                }

            }
            //Destroyed Asteroids will not be checked.
            if (asteroids.size() != i) {
                //In case shot destroys asteroid as it just before collision.
                //Checking if the ship has been hit.
                if (asteroids.get(i).intersect(ship)) {
                    //Destroying Asteroid
                    destroyAsteriod(i);
                    if (!ship.hasOvershields()) {
                        killPlayer();
                    }
                    break;
                }
                //Checking if it is out of bounds
                isOutOfBounds(asteroids.get(i));

                for (int j = 0; j < ships.size(); j++) {
                    //Checking if the ship has been hit.
                    if (asteroids.get(i).intersect(ships.get(j))) {
                        //Destroying Asteroid
                        destroyAsteriod(i);
                        destroyShip(j);
                        break;
                    }
                }
            }
        }

        //AI Ship Collision Check
        for (int i = 0; i < ships.size(); i++) {
            isOutOfBounds(ships.get(i));
            //Checking the players bullets have hit the aiShips
            for (int j = 0; j < playerBullets.size(); j++) {
                //Checking for collision
                if (ships.get(i).contains(playerBullets.get(j).position)) {
                    destroyShip(i);
                    break;
                }
            }
        }
    }

    //Checks objects for collisions or out of bounds
    private void isOutOfBounds(Shape shape) {
        if (shape.position.x > SCREEN_WIDTH) {
            shape.position.x = 0;
        } else if (shape.position.x < 0) {
            shape.position.x = SCREEN_WIDTH;
        } else if (shape.position.y > SCREEN_HEIGHT) {
            shape.position.y = 0;
        } else if (shape.position.y < 0) {
            shape.position.y = SCREEN_HEIGHT;
        }
    }
    /****************************************     Update Methods     *****************************************************/
    private void updateBackground(Graphics2D brush) {
        brush.setColor(Color.black);
        brush.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    }
    
    private void updateHUD(Graphics2D brush) {
        //Lives left
        brush.setColor(Color.white);
        brush.drawString("Lives: " + ship.lives, 10, 20);
        
        //Creating charge meter
        brush.drawString("Charge: ", 10, 35);
        brush.fillRect(55, 25, (int) Math.round(2 * ship.getMaxCharge()), 10);
        brush.setColor(Color.green);
        brush.fillRect(56, 26, (int) Math.round(2 * (ship.getCharge())) - 2, 8);
        
        //Creating health meter
        brush.setColor(Color.white);
        brush.drawString("Health: ", 10, 50);
        brush.fillRect(55, 40, (int) Math.round(6 * ship.getMaxShield()), 10);
        brush.setColor(Color.red);
        brush.fillRect(56, 41, (int) Math.round(6 * (ship.getShield())) - 2, 8);
        
        //Level status
        brush.setColor(Color.white);
        brush.drawString("Level: " + level, 10, 65);
        brush.drawString("Enemies Left: " + (asteroids.size() + ships.size()), 10, 80);
        brush.drawString("Score: " + score, 10, 95);
    }
    
    private void updateAsteriods(Graphics2D brush) {
        //Updating then drawing all asteroids in array
        brush.setColor(brown);
        for (int i = 0; i < asteroids.size(); i++) {
            asteroids.get(i).move();
            brush.fill(createShape(asteroids.get(i)));
        }
    }
    
    //Makes checks and paints images
    private void updateUpgrade(Graphics2D brush) {
        //Drawing the upgrade
        if (!upgrades.isEmpty()) {
            for (int index = 0; index < upgrades.size(); index++) {
		        if (upgrades.get(index) != null) {
		            brush.setColor(upgrades.get(index).getColor());
		            brush.fill(createShape(upgrades.get(index)));
		            //Decrementing upgrade time
		            upgrades.get(index).decreaseDuration();
		            //Checking if time is depleted
		            if (upgrades.get(index).getDuration() <= 0) {
		            	upgrades.remove(index);
		            }
		        }
            }
        }
    }
    
    private void updateStars(Graphics2D brush) {
        //Adding Stars
        brush.setColor(Color.white);
        //Setting background stars
        for (int i = 0; i < NUM_STARS; i++) {
            //Randomly tinkling star
            if (Math.random() * 1000 > 999) {
                stars[i].flipTwinkle();
            }
            brush.fillOval((int) stars[i].position.x, (int) stars[i].position.y, stars[i].getRadius(), stars[i].getRadius());
        }
    }

    private void updateShip(Graphics2D brush) {
        //Updating then drawing ship
        ship.update();
        brush.setColor(Color.red);
        brush.fill(createShape(ship));
        //Adding overshield
        if (ship.hasOvershields()) {
            brush.setColor(Color.white);
            brush.draw(createShape(ship));
        }
        //Updating bullets
        brush.setColor(Color.green);
        //Painting bullets
        for (int i = 0; i < ship.bullets.size(); i++) {
            ship.bullets.get(i).move();
            brush.fillOval((int) ship.bullets.get(i).position.x, (int) ship.bullets.get(i).position.y, ship.bullets.get(i).getRadius(), ship.bullets.get(i).getRadius());
        }
    }

    private void updateAIShip(Graphics2D brush) {
        brush.setColor(Color.magenta);
        for (int i = 0; i < ships.size(); i++) {
            ships.get(i).update();
            brush.fill(createShape(ships.get(i)));
        }
        //Painting aiBullets
        for (int j = 0; j < aiBullets.size(); j++) {
            aiBullets.get(j).move();
            brush.fillOval((int) aiBullets.get(j).position.x, (int) aiBullets.get(j).position.y, aiBullets.get(j).getRadius(), aiBullets.get(j).getRadius());
        }
    }
    private void updateScreenOverlay(Graphics2D brush) {
        brush.setColor(Color.white);
        brush.setFont(new Font("Overlay", Font.BOLD, 30));
        if (screenOverlayMessage.equals("Pause")) {
            brush.drawString(screenOverlayMessage, SCREEN_WIDTH / 2 - 6 * screenOverlayMessage.length(), SCREEN_HEIGHT / 2);
        } else {
            brush.drawString(screenOverlayMessage, SCREEN_WIDTH / 2 - 6 * screenOverlayMessage.length(), 40);
        }
    }
    /****************************************     Accessory Methods     *****************************************************/
    //Creates a location for the asteroid to spawn, away from player at randomized locations
    private Point findLocation() {
        double x = 0, y = 0;
        double distance, deltaX, deltaY;
        //Checks if there is sufficient clearance between player and asteroid upon creation
        boolean safe = false;
        while (!safe) {
            x = 0 + (int) Math.round(SCREEN_WIDTH * Math.random());
            y = 0 + (int) Math.round(SCREEN_HEIGHT * Math.random());
            //Checking distance from player
            deltaX = x - ship.position.x;
            deltaY = y - ship.position.y;
            distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            if (distance > MIN_DISTANCE) {
                safe = true;
            }
        }
        return new Point(x, y);
    }
    /***********  Asteroid Variables     **********/
    static protected float ASTEROID_DROP_CHANCE = 0.10f;
    static protected int ASTEROID_BASE_SCORE = 20;

    private void destroyAsteriod(int index) {
        score += ASTEROID_BASE_SCORE * asteroids.get(index).getLevel();
        
        //Breaking asteroid down into new asteroids
        if (asteroids.get(index).getLevel() >= 2) {
            asteroids.addAll(asteroidFactory.splitAsteroid(asteroids.get(index)));
        } else {
            if (Math.random() <= ASTEROID_DROP_CHANCE) {
            	addUpgradeToScene(asteroids.get(index).position);
            }
        }
        asteroids.remove(index);
    }

    static protected int SHIP_DROP_CHANCE = 2;

    //Destroys aiShip
    private void destroyShip(int index) {
        score += ships.get(index).score();

        //Chance item drop
        if (ASTEROID_DROP_CHANCE * Math.random() > ASTEROID_DROP_CHANCE - 1) {
        	addUpgradeToScene(ships.get(index).position);
        }
        ships.remove(index);
    }
    
    private void addUpgradeToScene(Point position) {
        Upgrades upgrade = upgradeFactory.createRandomUpgrade(position);
        if(upgrade != null) {
            upgrades.add(upgrade);        	
        }else {
        	System.out.println("Error generating upgrade.");
        }
    }
    
    //Checks various game state properties and makes appropriate calls
    private void gameStatus() {
        //Checking if level is complete
        if (asteroids.size() + ships.size() == 0) {
            //Resetting player for new level
            resetPlayer();
            //Starting a new level
            newLevel();
        }
        //Checking if game is over
        if (ship.lives <= 0) {
            screenOverlayMessage = "Game Over";
            //Submitting Score
            serverConnection = new ServerConnection(score);
            keyPause = true;
        }
    }
    //resets Player's location to center of screen
    private void resetPlayer() {
        //Resetting Ship
        ship.position.x = SCREEN_WIDTH / 2;
        ship.position.y = SCREEN_HEIGHT / 2;
        ship.rotation = 0;
        ship.setVelocity(0, 0);
    }

    //New level Start Up
    private int BASE_ASTERIOD_COUNT = 2;
    private int BASE_AISHIP_COUNT = 3;

    private void newLevel() {
        //Increasing level and lives
        level++;
        ship.lives++;
        //Creating new stars
        createStars();
        //Level completion bonus
        score += level * 50;
        //Increasing all stats
        ship.upgrade(ShipAttributes.ModifiableAttributeTypes.STRENGTH, 0.25f);
        ship.upgrade(ShipAttributes.ModifiableAttributeTypes.MAX_CHARGE, 2f);
        ship.upgrade(ShipAttributes.ModifiableAttributeTypes.BULLET_RANGE, 5f);
        ship.upgrade(ShipAttributes.ModifiableAttributeTypes.STABILIZER, -0.002f);
        ship.upgrade(ShipAttributes.ModifiableAttributeTypes.CHARGE_RATE, 0.005f);
        //Creating new enemies
        if (level % 5 == 0) {
            //Setting screen overlay
            screenOverlay = 150;
            screenOverlayMessage = "Enemy Space - Level " + level;
            createAiShips(BASE_AISHIP_COUNT + level, "ships/falcon-III.json");
            //Easing difficulty moving forward
            BASE_ASTERIOD_COUNT -= 7;
        } else {
            //Setting screen overlay
            screenOverlay = 150;
            screenOverlayMessage = "Asteroid Belt - Level " + level;
            createAsteroids(BASE_ASTERIOD_COUNT + level, level);
            createAiShips(level, "ships/falcon-I.json");
            createAiShips(level, "ships/falcon-II.json");
        }
    }
    
	public void createAiShips(int numShips, String type) {
        for (int i = 0; i < numShips; i++) {
            ships.add((Ship) shipFactory.createAiShip(findLocation(), type, ship, asteroids, aiBullets));
        }
	}
	
	public void createAsteroids(int numAsteroids, int level) {
        for (int i = 0; i < numAsteroids; i++) {
            asteroids.add(asteroidFactory.createAsteroid(findLocation(), level));
        }
	}
	
    private void killPlayer() {
        ship.death();
        resetPlayer();
    }

    /****************************************     Applet Settings     *****************************************************/
    public void init() {
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        buffer = createImage(SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    public void start() {}

    public void stop() {}

    public void destroy() {}

}