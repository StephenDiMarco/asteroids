package code;

import java.util.function.Consumer;

public class Debris extends ColliderSpriteGameObject {

	private AudioManager audioManager;
	
	public Debris(Transform transform, PolygonCollider collider, ColliderSprite sprite, AudioManager audioManager) {
		super(transform, collider, sprite);
	    Consumer<ColliderSpriteGameObject> onCollision = (ColliderSpriteGameObject t) -> onCollision(t);
		collider.setCallback(onCollision);
		this.audioManager = audioManager;
	}
	
	public void onCollision(ColliderSpriteGameObject target) {
		alive = false;
	}
	
	public void onDeath() {
		audioManager.playOnce(audioManager.ASTEROID_DESTROYED);
	}
}
