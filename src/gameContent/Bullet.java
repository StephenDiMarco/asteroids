package gameContent;

public class Bullet extends  Circle{
	
	private float strength;
	private int range;
	private double xVelocity;
	private double yVelocity;

	public boolean depleted; //Determines whether the bullet should be removed array
	
	Bullet(double x, double y, int radius, float strength, double timeInterval, double rotation, int range, double shipXVel, double shipYVel){
		 super(x,y,radius+1);
		 this.strength =  strength;
		 this.range = range;
		 //Setting up Velocity/Movement
		 createVelocity(rotation, shipXVel,shipYVel);
	}
	//Sets velocity of bullet based on rotation and ships speed
	private void createVelocity(double rotation, double shipXVel,  double shipYVel){
		xVelocity = shipXVel+2*strength*Math.sin(rotation*Math.PI/180);
		yVelocity = shipYVel-2*strength*Math.cos(rotation*Math.PI/180);
	}
	
	public void move(){
		position.x += xVelocity;
		position.y += yVelocity;
		range--;
		//Checking if shot is at the end of its range.
		if(range <= 0){
			depleted = true;
		}
	}
	
	public float getStrength(){
		return strength;
	}
}
