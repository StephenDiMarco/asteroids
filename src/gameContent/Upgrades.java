package gameContent;
import java.awt.Color;
import static gameContent.ShipAttributes.ModifiableAttributeTypes;

public class Upgrades extends Polygon{
	
	private Color color;
	private ModifiableAttributeTypes attributeType;
	private int duration;
	private float modifier;
	private String name;
	private int score;
	
	Upgrades(Point[] inShape, Point inPosition, ModifiableAttributeTypes attributeType, int duration, float modifier, String name, int[] rbg){
		super(inShape, inPosition, 0);	
		this.attributeType = attributeType;
		this.duration = duration;
		this.modifier = modifier;
		this.name = name;
		color = new Color(rbg[0],rbg[1],rbg[2]);
	}
	
	public void setAttributeType() {
		attributeType = ModifiableAttributeTypes.MAX_SHIELDS;
	}
	
	public Color getColor() {
		return color;
	}

	public ModifiableAttributeTypes getAttributeType() {
		return attributeType;
	}

	public int getDuration() {
		return duration;
	}

	public void decreaseDuration() {
		this.duration--;
	}
	
	public float getModifier() {
		return modifier;
	}
	
	public String getName() {
		return name;
	}
	
	public int getScore() {
		return score;
	}
	
}
