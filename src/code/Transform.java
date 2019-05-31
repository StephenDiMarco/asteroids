package code;

public class Transform {
	
	private Point position;
	private double xVelocity;
	private double yVelocity;
    private double rotation;
	private double timeInterval;

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}
	
	public Transform(Point position, double xVelocity, double yVelocity) {
		this(position, xVelocity, yVelocity, 0);
	}
	
	public Transform(Point position, double xVelocity, double yVelocity, double rotation) {
		this.position = position;
		this.xVelocity = xVelocity;
		this.yVelocity = yVelocity;
		this.rotation = rotation;
		this.timeInterval = Game.GetTimeInterval();
	}
	
	public void setVelocity(double xVelocity, double yVelocity){
		this.xVelocity = xVelocity;
		this.yVelocity = yVelocity;
	}
	
	public double getVelocity(double xVelocity, double yVelocity){
		return Math.sqrt(xVelocity*xVelocity + yVelocity*yVelocity);
	}
	
	protected void rotate(double degrees) {
	 rotation = (rotation+degrees)%360;
	 if( rotation <= 0 ){
	  rotation = 360 - rotation;
	 }
	}  
	
	public void move() {
		position.x += xVelocity*timeInterval;
		position.y += yVelocity*timeInterval;
	}

}
