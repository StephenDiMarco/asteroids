package code;

import java.awt.Color;
import java.awt.Graphics2D;

public class ColliderSprite {
	
	private PolygonCollider collider;
	private Color color;

	public ColliderSprite(PolygonCollider collider, Color color) {
		this.collider = collider; 
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void paint(Graphics2D brush) {
        brush.setColor(color);
        brush.fill(collider.getBoundingBoxPath());
	}

}
