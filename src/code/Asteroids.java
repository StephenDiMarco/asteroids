package code;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import code.GameObjectRegistry.Layers;

import java.awt.image.BufferedImage;


@SuppressWarnings("serial")
public class Asteroids extends Game {

    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;
    private int MIN_DISTANCE = 60; //The minimum distance an asteroid can spawn away from a player
    private int NUM_STARS = 150;
    private boolean updateThread;
    
    //Stores game objects
    private Ship ship;
    private ArrayList < Bullet > playerBullets; //Will be used for AIs fire as well
    private ArrayList < Asteroid > asteroids;
    private ArrayList < Ship > ships;
    private ArrayList < Bullet > aiBullets; //Will be used for AIs fire as well
    private ArrayList < Upgrades > upgrades;    
    private GameObjectRegistry gameObjectRegistry;
    private Star[] stars;
    private Hud hud;
    
    //Audio
    private AudioManager audioManager;
    
    //Factories
    private GsonUtility gsonUtility;
    private AsteroidFactory asteroidFactory;
    private ShipFactory shipFactory;
    private UpgradeFactory upgradeFactory;
    private DebrisFactory debrisFactory;

    //Stats
    private int level;
    //Server Connection
    @SuppressWarnings("unused")
    private ServerConnection serverConnection;
    //Custom Colors
    private Color brown = new Color(139, 75, 60);
    
	public static void main(String[] args)
	{
		Asteroids frame = new Asteroids ();
		// Setting the application to be fullscreen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize((int)screenSize.getWidth(),(int)screenSize.getHeight());
		frame.setUndecorated(true);
		frame.setVisible(true);
		// Creating the buffer to draw on
		frame.buffer = (BufferedImage) frame.createImage((int)screenSize.getWidth(),(int)screenSize.getHeight());
	}
	
    //Constructor	
    public Asteroids() {
      	this.addWindowListener(this);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        SCREEN_WIDTH = (int)screenSize.getWidth();
        SCREEN_HEIGHT = (int)screenSize.getHeight();
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

        //Sound
        this.audioManager = new AudioManager();

        //Creating factories
        this.gsonUtility = new GsonUtility();
        this.asteroidFactory = new AsteroidFactory(audioManager);
        this.shipFactory = new ShipFactory(gsonUtility, audioManager);
        this.upgradeFactory = new UpgradeFactory(gsonUtility);
        this.debrisFactory = new DebrisFactory(audioManager);
        
        newGame();
        //Setting updateThread to false to begin;
        updateThread = false;
    }

    public void newGame() {
        setNewGame(false);
        this.level = 0;
 
        //Creating scene object stores
        gameObjectRegistry = new GameObjectRegistry();
        asteroids = new ArrayList < Asteroid > ();
        playerBullets = new ArrayList < Bullet > ();
        this.ship = shipFactory.createPlayerShip(new Point(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2), playerBullets);
        this.ship.lives = 3;
        this.addKeyListener(((PlayerController)ship.getController()));
        aiBullets = new ArrayList < Bullet > ();
        ships = new ArrayList < Ship > ();
        upgrades = new ArrayList < Upgrades > ();
        stars = new Star[NUM_STARS];
        hud = new Hud(SCREEN_WIDTH, SCREEN_HEIGHT, ship);
        hud.setPause(false);
        newLevel();
    }

    @Override
    public void paint(Graphics g) {
    	lastTimeStamp = System.currentTimeMillis();
    	
        if (getNewGame()) {
            newGame();
        }
        
        Graphics2D brush = (Graphics2D) g;
        if (!hud.getPause()) {
            updateBackground(brush);
            updateStars(brush);
            updateShip(brush);
            updateAIShip(brush);
            updateUpgrade(brush);
            gameObjectRegistry.update();
            gameObjectRegistry.checkCollisions();
            gameObjectRegistry.clean();
            gameObjectRegistry.paint(brush);
            hud.update(brush);

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
            hud.update(brush);
        }
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
                    hud.updateOverlayMessage(upgrades.get(index).getName(), hud.UPGRADE_OVERLAY_TIME);
                    hud.updateScore(upgrades.get(index).getScore(), upgrades.get(index).position);
                    upgrades.remove(index);
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

    //Makes checks and paints images
    private void updateUpgrade(Graphics2D brush) {
        //Drawing the upgrade
        if (!upgrades.isEmpty()) {
            for (int index = 0; index < upgrades.size(); index++) {
		        if (upgrades.get(index) != null) {
		            brush.setColor(upgrades.get(index).getColor());
		            upgrades.get(index).update();
		            brush.fill(upgrades.get(index).getBoundingBoxPath());
		            //Decrementing upgrade time
		            upgrades.get(index).decreaseDuration(elapsedTime);
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
            if (Math.random() > 0.995) {
                stars[i].flipTwinkle();
            }
            brush.fillOval((int) stars[i].position.x, (int) stars[i].position.y, stars[i].getRadius(), stars[i].getRadius());
        }
    }

    private void updateShip(Graphics2D brush) {
        //Updating then drawing ship
        ship.update();
        brush.setColor(Color.red);
        brush.fill(ship.getBoundingBoxPath());
        //Adding overshield
        if (ship.hasOvershields()) {
            brush.setColor(Color.white);
            brush.draw(ship.getBoundingBoxPath());
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
            brush.fill(ships.get(i).getBoundingBoxPath());
        }
        //Painting aiBullets
        for (int j = 0; j < aiBullets.size(); j++) {
            aiBullets.get(j).move();
            brush.fillOval((int) aiBullets.get(j).position.x, (int) aiBullets.get(j).position.y, aiBullets.get(j).getRadius(), aiBullets.get(j).getRadius());
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

    static protected int SHIP_DROP_CHANCE = 2;

    private void destroyShip(int index) {
    	hud.updateScore(ships.get(index).score(), ships.get(index).position);

        //Chance item drop
        if (SHIP_DROP_CHANCE * Math.random() > SHIP_DROP_CHANCE - 1) {
        	addUpgradeToScene(ships.get(index).position);
        }
        audioManager.playOnce(audioManager.SHIP_DESTROYED);
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
            //Submitting Score
            serverConnection = new ServerConnection(hud.getScore());
            hud.setPause(true);
            hud.setGameOver(true);
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
    private int POINTS_PER_LEVEL = 50;

    private void newLevel() {
    	//Clearing level
    	gameObjectRegistry.resetNonPlayerGameObjects();

        //Increasing level and lives
        level++;
        ship.lives++;
        //Creating new stars
        createStars();
        //Level completion bonus
        hud.updateScore(level * POINTS_PER_LEVEL);
        //Increasing all stats
        ship.upgrade(ShipAttributes.ModifiableAttributeTypes.STRENGTH, 0.25f);
        ship.upgrade(ShipAttributes.ModifiableAttributeTypes.MAX_CHARGE, 2f);
        ship.upgrade(ShipAttributes.ModifiableAttributeTypes.BULLET_RANGE, 5f);
        ship.upgrade(ShipAttributes.ModifiableAttributeTypes.STABILIZER, -0.002f);
        ship.upgrade(ShipAttributes.ModifiableAttributeTypes.CHARGE_RATE, 0.005f);
        //Creating new enemies
        if (level % 5 == 0) {
            hud.updateOverlayMessage("Enemy Space - Level " + level);
            createAiShips(BASE_AISHIP_COUNT + level, "falcon-III.json");
            //Easing difficulty moving forward
            BASE_ASTERIOD_COUNT -= 7;
        } else {
        	hud.updateOverlayMessage("Asteroid Belt - Level " + level);
            createAsteroids(BASE_ASTERIOD_COUNT + level, level);
            createAiShips(level, "falcon-I.json");
            createAiShips(level, "falcon-II.json");
        }
        // Creating objects in new system
        createPassiveDebris(55);
    }
    
	public void createPassiveDebris(int numDebris) {
        for (int i = 0; i < numDebris; i++) {
            gameObjectRegistry.register(debrisFactory.createDebris(findLocation()), Layers.PASSIVE_FRIENDLY);
        }
	}
    
	public void createAiShips(int numShips, String type) {
        for (int i = 0; i < numShips; i++) {
            ships.add((Ship) shipFactory.createAiShip(findLocation(), type, ship, asteroids, aiBullets));
        }
	}
	
	public void createAsteroids(int numAsteroids, int level) {
        for (int i = 0; i < numAsteroids; i++) {
        	gameObjectRegistry.register(asteroidFactory.createAsteroid(findLocation(), level), Layers.PASSIVE_HOSTILE);
        }
	}
	
    private void killPlayer() {
        ship.death();
        audioManager.playOnce(audioManager.PLAYER_DESTROYED);
        resetPlayer();
    }
    
    public void keyPressed(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_P){
			if (!hud.getGameOver()) {
				hud.togglePause();				
			}
		}else if(e.getKeyCode() == KeyEvent.VK_R){
			setNewGame(true);
			hud.setGameOver(false);
		}else if(e.getKeyCode() == KeyEvent.VK_M)	 {
			audioManager.toggleMute();
		}
    }

}