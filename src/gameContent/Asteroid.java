package gameContent;

public class Asteroid extends Polygon{
	
	private double xVelocity;
	private double yVelocity;
	private double timeInterval;
	private float health; //Represents the number of shots 
	private int level; //Used for point scoring and division of asteroids
	
	public Asteroid(Point[] inShape, Point inPosition, double inTimeInterval, int asterLevel){
		super(inShape, inPosition, 0);
		//Setting movement variables 
		timeInterval = inTimeInterval/100;
		//Randomly setting velocity, as a function of the square of the asteroids level
		double sqrdLevel = Math.sqrt(asterLevel);
		setVelocity(sqrdLevel*4-(sqrdLevel*8*Math.random()),sqrdLevel*4-(sqrdLevel*8*Math.random()));
		//Setting health dependant on level
		level = asterLevel;
		health = 2*level;
	}
	//Creates a copy of the asteroid
	public Asteroid createCopy(){
		return new Asteroid(getShape(), position, timeInterval, level);
	}
	//Moves the asteroid
	public void move(){
		position.x += xVelocity*timeInterval;
		position.y += yVelocity*timeInterval;
	}

	//Allows for manual setting of velocity
	public void setVelocity(double xVelocity, double yVelocity){
		this.xVelocity = xVelocity;
		this.yVelocity = yVelocity;
	}
	//Velocity getters
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
