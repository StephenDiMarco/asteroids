package code;

import java.awt.Color;
import java.awt.Graphics2D;

public class Asteroid extends Polygon implements GameObject {
	
	private double xVelocity;
	private double yVelocity;
	private double timeInterval;
	private float health;
	private int level;
	private boolean alive;
	
	public Asteroid(Point[] inShape, Point inPosition, int level, Color color){
		super(inShape, inPosition, 0, color);

		//Randomly setting velocity, as a function of the square of the asteroids level
		timeInterval = Game.GetTimeInterval();
		double sqrdLevel = Math.sqrt(level);
		setVelocity(sqrdLevel*0.1-(sqrdLevel*0.2*Math.random()),sqrdLevel*0.1-(sqrdLevel*0.2*Math.random()));
		
		//Setting health dependent on level
		this.level = level;
		health = 2*level;
		alive = true;
	}

	public void update() {
		move();
		super.update();
	}
	
	public boolean alive() {
		return alive;
	}
	
	public void paint(Graphics2D brush) {
        brush.setColor(color);
        brush.fill(getBoundingBoxPath());
	}
	
	public void onCollision(GameObject object) {
		health -= object.getDamage();
		if(health <= 0) {
			alive = false;
		}
	}
	
	public int getDamage() {
		return (int)area * getVelocity();
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
	
	private void move() {
		position.x += xVelocity*timeInterval;
		position.y += yVelocity*timeInterval;
	}
	
	private int getVelocity() {
		return (int)Math.sqrt(xVelocity*xVelocity + yVelocity*yVelocity);
	}
}
