package code;

import java.awt.Color;
import java.awt.Graphics2D;

public class ColliderSprite {
	
	private Collider collider;
	private Color color;

	public ColliderSprite(Collider collider, Color color) {
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
