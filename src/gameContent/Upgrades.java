package gameContent;
import java.awt.Color;

public class Upgrades extends Polygon{
	
	private Color color;
	private int modType;
	private int duration; //Determines how long it stays on screen
	
	Upgrades(Point[] inShape, Point inPosition, int modType, int duration, int[] rbg){
		super(inShape, inPosition, 0);	
		this.modType = modType;
		this.duration = duration;
		color = new Color(rbg[0],rbg[1],rbg[2]);
	}

	public Color getColor() {
		return color;
	}

	public int getModType() {
		return modType;
	}

	public int getDuration() {
		return duration;
	}

	public void decreaseDuration() {
		this.duration--;
	}
	
	
	
}
