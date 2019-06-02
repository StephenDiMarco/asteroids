package code;

import java.awt.Color;
import java.util.ArrayList;

import code.GameObjectRegistry.Layers;

public class AsteroidFactory {
	
    private int BASE_RADIUS = 200;
    private int RADIUS_MOD = 5;
    private int RADIUS_MOD_VARIANCE = 8;
    
    private int MIN_POINTS = 4;
    private int RANDOM_POINTS = 6;

    private Pallet browns;
	private AudioManager audioManager;
	private GameObjectRegistry gameObjectRegistry;
	private Hud hud;
	private UpgradeFactory upgradeFactory;
	
	public AsteroidFactory(AudioManager audioManager, GameObjectRegistry gameObjectRegistry, Hud hud, UpgradeFactory upgradeFactory) {
		browns = new Pallet(new Color(139, 75, 60));
		this.audioManager = audioManager;
		this.gameObjectRegistry = gameObjectRegistry;
		this.hud = hud;
		this.upgradeFactory = upgradeFactory;
	}

    
    public void splitAsteroid(Asteroid asteroid) {
    	// Decrementing the asteroid level
        int level =  asteroid.getLevel() - 1;

        // Larger asteroids only split into two
        int numberOfAsteroids = level + 1;        
        if (numberOfAsteroids > 4) {
        	numberOfAsteroids = 2;
        }
        
        ArrayList <Asteroid> asteroids = new ArrayList <Asteroid>();
        
        Point position = asteroid.getTransform().getPosition();
        Color color = asteroid.getSprite().getColor();
        
        for (int i = 0; i < numberOfAsteroids; i++) {
        	Point inPosition = new Point(position.x + 10 * level * Math.random(), position.y + 10 * level * Math.random());
        	createAsteroid(inPosition, level, color);
        }
    }
    
    public void createAsteroid(Point inPosition, int level) {
    	createAsteroid(inPosition, level, browns.getRandomColor());
    }
    
    private void createAsteroid(Point inPosition, int level, Color color) {
        int points = (int)(MIN_POINTS + level + Math.floor(RANDOM_POINTS * Math.random()));
        Point[] shape = new Point[points];
        
        //Creating Asteroid shape, the sqrt function gives a leveling in the size increase with each level
        double radius = Math.sqrt(BASE_RADIUS * Math.sqrt(level));
        
        //Creating first point
        shape[0] = new Point(0, radius);
        
        //Creating points other points which vary in distance from the center
        for (int i = 1; i < points; i++) {
            int x = (int) Math.round(radius * (Math.sin(2 * Math.PI * i / points)) + RADIUS_MOD - (int) Math.round((RADIUS_MOD_VARIANCE + level) * Math.random()));
            int y = (int) Math.round(radius * (Math.cos(2 * Math.PI * i / points)) + RADIUS_MOD - (int) Math.round((RADIUS_MOD_VARIANCE + level) * Math.random()));
            shape[i] = new Point(x, y);
        }
        
		//Randomly setting velocity, as a function of the square of the asteroids level
		double sqrdLevel = Math.sqrt(level);		
		Transform transform = new Transform(inPosition, sqrdLevel*0.1-(sqrdLevel*0.2*Math.random()),sqrdLevel*0.1-(sqrdLevel*0.2*Math.random()));
		PolygonCollider collider = new PolygonCollider(transform, shape);
		ColliderSprite sprite = new ColliderSprite(collider, browns.getRandomColor());
		
		gameObjectRegistry.register(new Asteroid(transform, collider, sprite, audioManager, level, hud, this, upgradeFactory), Layers.PASSIVE_HOSTILE);
    }
}
