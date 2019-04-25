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
    private int MAX_POINTS = 15; //Max number of points on an asteroid
    private int MIN_DISTANCE = 60; //The minimum distance an asteroid can spawn away from a player
    private int NUM_STARS = 130;
    private boolean updateThread;
    //Stores game objects
    private Ship ship;
    private ArrayList < Bullet > playerBullets; //Will be used for AIs fire as well
    private ArrayList < Asteroid > asteroids;
    private ArrayList < Ship > ships;
    private ArrayList < Bullet > aiBullets; //Will be used for AIs fire as well
    private UpgradeFactory upgradeFactory;
    private ShipFactory shipFactory;
    private GsonUtility gsonUtility;
    private Star[] stars;
    private ArrayList < Upgrades > upgrades;
    //Stats
    private int level;
    private int score;
    //Server Connection
    @SuppressWarnings("unused")
    private ServerConnection serverConnection;
    //Custom Colors
    protected Color brown = new Color(139, 75, 60);

    //Constructor	
    public Asteroids() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        SCREEN_WIDTH = (int)screenSize.getWidth();
        SCREEN_HEIGHT = (int)screenSize.getHeight();
        System.out.println("Size:" + SCREEN_WIDTH + "  " + SCREEN_HEIGHT);
        
        newGame();
        //Setting updateThread to false to begin;
        updateThread = false;
    }
    
    public void newGame() {
        //Submitting score over 0
        if (score > 0) {
            serverConnection = new ServerConnection(score);
        }
        this.level = 0;
        //Resetting enemy base counts
        BASE_ASTERIOD_COUNT = 2;
        BASE_AISHIP_COUNT = 3;
        //Resetting upgrade
        upgrades = new ArrayList < Upgrades > ();

        this.gsonUtility = new GsonUtility();
        this.upgradeFactory = new UpgradeFactory(gsonUtility);
        this.shipFactory = new ShipFactory(gsonUtility);
        
        //Creating the ship and its bullets
        playerBullets = new ArrayList < Bullet > ();
        this.ship = shipFactory.createPlayerShip(new Point(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2), playerBullets);
        this.addKeyListener(((PlayerController)ship.getController()));
        
        //Initialising ArrayList
        asteroids = new ArrayList < Asteroid > ();

        //Creating ship ArrayList
        aiBullets = new ArrayList < Bullet > ();
        ships = new ArrayList < Ship > ();
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
        if (getNewGame()) {
            //Creating new game
            newGame();
            //Resetting new game flag
            setNewGame(false);
            //Unpausing Screen updates
            keyPause = false;
        }
        //Checking for pause
        if (!getPause()) {
            Graphics2D brush = (Graphics2D) g;
            //Setting background of space
            brush.setColor(Color.black);
            brush.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
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
    //Creates a number of Asteroids depenedent on player level
    private void createAsteroids(int numAsteroids, int levelAster, Point origin) {
        //Start of a new level, no specific location chosen.
        Point[] tempPoints = new Point[MAX_POINTS];
        //Creating proper number of asteroids for level
        for (int i = 0; i < numAsteroids; i++) {
            //Creating Asteroid Shape in a static method in Asteroid
            tempPoints = createAsteroid(levelAster);
            //Placing it in a free location
            Point inPosition;
            if (origin == null) {
                inPosition = findLocation();
            } else {
                inPosition = new Point(origin.x + 10 * levelAster * Math.random(), origin.y + 10 * levelAster * Math.random());
            }
            //Creating new Asteroid
            asteroids.add(new Asteroid(tempPoints, inPosition, levelAster));
        }

    }

    //Creates an asteroid
    private double BASE_RADIUS = 200;
    private Point[] createAsteroid(int Asteroidlevel) {
        //Drawing asteroid shape, begining with at least 5 points, increasing with size
        int nPoints = (int)(4 + Asteroidlevel + Math.floor(8 * Math.random()));
        Point[] asterShape = new Point[nPoints];
        //Creating Asteroid shape, the sqrt function gives a leveling in the size increase with each level
        double radius = Math.sqrt(BASE_RADIUS * Math.sqrt(Asteroidlevel));
        //Creating first point
        asterShape[0] = new Point(0, radius);
        //Creating points other points with bumpy texture
        for (int i = 1; i < nPoints; i++) {
            //Creating altered x,y pos on circle
            int x = (int) Math.round(radius * (Math.sin(2 * Math.PI * i / nPoints)) + 5 - (int) Math.round((8 + Asteroidlevel * 1.5) * Math.random()));
            int y = (int) Math.round(radius * (Math.cos(2 * Math.PI * i / nPoints)) + 5 - (int) Math.round((8 + Asteroidlevel * 1.5) * Math.random()));
            asterShape[i] = new Point(x, y);
        }
        //Creating ship
        return asterShape;
    }

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
    private void updateHUD(Graphics2D brush) {
        //Hud Creation
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
    static protected int ASTEROID_DROP_CHANCE = 3;
    static protected int ASTEROID_BASE_SCORE = 20;

    private void destroyAsteriod(int index) {
        score += ASTEROID_BASE_SCORE * asteroids.get(index).getLevel();
        //Breaking Asteroid Down into new asteroids
        if (asteroids.get(index).getLevel() >= 2) {
            //Sends in the number of asteroids to be generated (equal to level, maxs at 4), their level and position.
            int numAster = asteroids.get(index).getLevel();
            //Larger asteroids only break in half
            if (numAster > 4) {
                numAster = 2;
            }
            createAsteroids(numAster, asteroids.get(index).getLevel() - 1, asteroids.get(index).position);
        } else {
            //Chance item drop
            if (ASTEROID_DROP_CHANCE * Math.random() > ASTEROID_DROP_CHANCE - 2) {
            	addUpgradeToScene(asteroids.get(index).position);
            }
        }
        //Destroying Asteroid
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
            createAsteroids(BASE_ASTERIOD_COUNT + level, level, null);
            createAiShips(level, "ships/falcon-I.json");
            createAiShips(level-1, "ships/falcon-II.json");
        }
    }
    
	public void createAiShips(int numShips, String type) {
        for (int i = 0; i < numShips; i++) {
            ships.add((Ship) shipFactory.createAiShip(findLocation(), type, ship, asteroids, aiBullets));
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