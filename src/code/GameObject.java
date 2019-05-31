package code;

import java.awt.Graphics2D;

public class GameObject {

	private Transform transform;
	private Collider collider;
	private ColliderSprite sprite;
	
	public GameObject(Transform transform, Collider collider, ColliderSprite sprite) {
		this.transform = transform;
		this.collider = collider;
		this.sprite = sprite;
	}

	public void update(Graphics2D brush){
		transform.move();
		collider.update();
		sprite.paint(brush);
	}
	
}
