package code;

import java.awt.Graphics2D;

public class ColliderSpriteGameObject implements GameObject {

	private Transform transform;
	private Collider collider;
	private ColliderSprite sprite;
	protected boolean alive;
	
	public ColliderSpriteGameObject(Transform transform, Collider collider, ColliderSprite sprite) {
		this.transform = transform;
		this.collider = collider;
		this.sprite = sprite;
		this.alive = true;
	}
	
	public Transform getTransform() {
		return transform;
	}
	
	public ColliderSprite getSprite() {
		return sprite;
	}
	
	public boolean isAlive() {
		return alive;
	}

	public void update(){
		transform.move();
		collider.update();
	}

	public void paint(Graphics2D brush){
		sprite.paint(brush);
	}
	
	public boolean collides(ColliderSpriteGameObject target){
		return collider.intersect(target.collider);
	}
	
	public void onCollision(ColliderSpriteGameObject target) {
		collider.onCollision(target);
	}
	
	public void onDeath() {}
	
}
