package code;

import java.awt.Graphics2D;

public interface GameObject {
	public boolean isAlive();
	public void update();
	public void paint(Graphics2D brush);	
	public void onDeath();
}
