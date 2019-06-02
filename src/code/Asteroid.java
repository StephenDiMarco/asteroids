package code;

import java.util.function.Consumer;

public class Asteroid extends ColliderSpriteGameObject {

	private AudioManager audioManager;
	private Hud hud;
	private AsteroidFactory asteroidFactory;
	private UpgradeFactory upgradeFactory;

	private float health;
	private int level;
	
	public Asteroid(Transform transform, PolygonCollider collider, ColliderSprite sprite, AudioManager audioManager, int level, Hud hud, AsteroidFactory asteroidFactory, UpgradeFactory upgradeFactory) {
		super(transform, collider, sprite);
	    Consumer<ColliderSpriteGameObject> onCollision = (ColliderSpriteGameObject t) -> this.onCollision(t);
		collider.setCallback(onCollision);
		this.audioManager = audioManager;
		this.hud = hud;
		this.asteroidFactory = asteroidFactory;
		this.upgradeFactory = upgradeFactory;

		//Setting health dependent on level
		this.level = level;
		health = 2*level;
	}

	public void onCollision(ColliderSpriteGameObject target){
		health -= 5;
		if(health <= 0){
			alive = false;
		}
	}

    static protected int ASTEROID_BASE_SCORE = 20;
    static protected float ASTEROID_DROP_CHANCE = 0.10f;
	
	public void onDeath() {
		audioManager.playOnce(audioManager.ASTEROID_DESTROYED);    

		hud.updateScore(ASTEROID_BASE_SCORE * level, getTransform().getPosition());
        
        //Breaking asteroid down into new asteroids
        if (level >= 2) {
            asteroidFactory.splitAsteroid(this);
        } else {
           if (Math.random() <= ASTEROID_DROP_CHANCE) {
            	upgradeFactory.createRandomUpgrade(getTransform().getPosition());
            }
       }
	}
	
	public int getLevel(){
		return level;
	}
}
