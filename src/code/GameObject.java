package code;

import java.awt.Graphics2D;

interface GameObject {
	public void update();
	public void paint(Graphics2D brush);
	public void onCollision(GameObject object);
	public boolean alive();
	public int getDamage();
}
