package gameContent;

public class Asteroid extends Polygon{
	
	private double xVelocity;
	private double yVelocity;
	private double timeInterval;
	private float health; //Represents the number of shots 
	private int level; //Used for point scoring and division of asteroids
	
	public Asteroid(Point[] inShape, Point inPosition, int asterLevel){
		super(inShape, inPosition, 0);
		//Setting movement variables 
		timeInterval = Game.GetTimeInterval();
		//Randomly setting velocity, as a function of the square of the asteroids level
		double sqrdLevel = Math.sqrt(asterLevel);
		setVelocity(sqrdLevel*0.1-(sqrdLevel*0.2*Math.random()),sqrdLevel*0.1-(sqrdLevel*0.2*Math.random()));
		//Setting health dependent on level
		level = asterLevel;
		health = 2*level;
	}

	public Asteroid createCopy(){
		return new Asteroid(getShape(), position, level);
	}

	public void move(){
		position.x += xVelocity*timeInterval;
		position.y += yVelocity*timeInterval;
	}

	public void setVelocity(double xVelocity, double yVelocity){
		this.xVelocity = xVelocity;
		this.yVelocity = yVelocity;
	}

	public double getXvel(){
		return xVelocity;
	}
	
	public double getYvel(){
		return yVelocity;
	}
	
	public boolean hit(float attack){
		health -= attack;
		if(health <= 0){
			return true;
		}else{
			return false;
		}
	}
	
	public int getLevel(){
		return level;
	}
}
