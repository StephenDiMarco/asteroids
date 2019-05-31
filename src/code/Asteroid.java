package code;

import java.awt.Color;
import java.util.function.Consumer;

public class Asteroid extends ColliderSpriteGameObject {

	private AudioManager audioManager;

	private float health;
	private int level;
	
	public Asteroid(Transform transform, Collider collider, ColliderSprite sprite, AudioManager audioManager, int level) {
		super(transform, collider, sprite);
	    Consumer<ColliderSpriteGameObject> onCollision = (ColliderSpriteGameObject t) -> this.onCollision(t);
		collider.setCallback(onCollision);
		this.audioManager = audioManager;

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

	//	hud.updateScore(ASTEROID_BASE_SCORE * asteroids.get(index).getLevel(), asteroids.get(index).position);
        
    //    //Breaking asteroid down into new asteroids
    //    if (asteroids.get(index).getLevel() >= 2) {
    //        asteroids.addAll(asteroidFactory.splitAsteroid(asteroids.get(index)));
    //    } else {
    //        if (Math.random() <= ASTEROID_DROP_CHANCE) {
    //        	addUpgradeToScene(asteroids.get(index).position);
    //        }
    //    }
	}
	
	public int getLevel(){
		return level;
	}
}
