package gameContent;

public class Asteroid extends Polygon{
	
	private double xVelocity;
	private double yVelocity;
	private double timeInterval;
	private float health;
	private int level;
	
	public Asteroid(Point[] inShape, Point inPosition, int level){
		super(inShape, inPosition, 0);

		//Randomly setting velocity, as a function of the square of the asteroids level
		timeInterval = Game.GetTimeInterval();
		double sqrdLevel = Math.sqrt(level);
		setVelocity(sqrdLevel*0.1-(sqrdLevel*0.2*Math.random()),sqrdLevel*0.1-(sqrdLevel*0.2*Math.random()));
		//Setting health dependent on level
		this.level = level;
		health = 2*level;
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
