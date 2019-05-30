package code;

import java.awt.Color;
import java.awt.Graphics2D;

public class Bullet extends Circle implements GameObject {
	
	private int strength;
	private int range;
	private double travelDistancePerFrame;
	private double xVelocity;
	private double yVelocity;
	private Color color;

	public boolean alive; //Determines whether the bullet should be removed array
	
	Bullet(double x, double y, int radius, int strength, double timeInterval, double rotation, int range, double shipXVel, double shipYVel, Color color){
		 super(x,y,radius+1);
		 this.strength =  strength;
		 this.range = range;
		 //Setting up Velocity/Movement
		 createVelocity(rotation, shipXVel,shipYVel);
		 this.travelDistancePerFrame = Math.sqrt(xVelocity*xVelocity + yVelocity*yVelocity);
		 this.color = color;
		 this.alive = true;
	}
	//Sets velocity of bullet based on rotation and ships speed
	private void createVelocity(double rotation, double shipXVel,  double shipYVel){
		xVelocity = 3*shipXVel+5*strength*Math.sin(rotation*Math.PI/180);
		yVelocity = 3*shipYVel-5*strength*Math.cos(rotation*Math.PI/180);
	}
	
	public void update() {
		move();
	}
	
	public void move(){
		position.x += xVelocity;
		position.y += yVelocity;
		range -= travelDistancePerFrame;
		
		//Checking if shot is at the end of its range.
		if(range <= 0){
			alive = false;
		}
	}

	public boolean alive() {
		return alive;
	}
	
	public void paint(Graphics2D brush) {
        brush.setColor(color);
        brush.fillOval((int) position.x, (int) position.y, getRadius(), getRadius());
	}
	
	public void onCollision(GameObject object) {
		alive = false;
	}
	
	public int getDamage() {
		return  strength;
	}
}
