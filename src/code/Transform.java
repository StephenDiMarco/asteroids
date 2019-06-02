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
	
	public double getVelocity(){
		return Math.sqrt(xVelocity*xVelocity + yVelocity*yVelocity);
	}
	

	 protected void rotate(double angularVelocity){
		 rotateByDegree(angularVelocity*timeInterval);
	 }
	
	 protected void rotateByDegree(double degrees) {
	     rotation = (rotation+degrees)%360;
	     if( rotation <= 0 ){
	    	 rotation = 360 - rotation;
	     }
	 }
	
	 public void accelerate (double acceleration, double MaxVelocity) {
		 double dvx = (acceleration * Math.sin(Math.toRadians(rotation)));
		 double dvy = (acceleration * -Math.cos(Math.toRadians(rotation)));
		 xVelocity += dvx;
		 yVelocity += dvy;
		 if(getVelocity() > MaxVelocity ) {
			 double theta = -Math.atan2(xVelocity, yVelocity);
			 theta += Math.PI;
			 theta = Math.toDegrees(theta);
			 xVelocity = (MaxVelocity * Math.sin(Math.toRadians(theta)));
			 yVelocity = (MaxVelocity * -Math.cos(Math.toRadians(theta)));
		 }
	 }
	
	public void update() {
		position.x += xVelocity*timeInterval;
		position.y += yVelocity*timeInterval;
		Asteroids.isOutOfBounds(position);
	}
}
