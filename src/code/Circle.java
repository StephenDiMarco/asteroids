package code;

public class Circle extends Shape{
	
	private int radius;
	
	Circle(double x, double y, int radius){
		super(new Point(x,y));
		this.radius = radius;
	}
	
	public int getRadius(){
		return radius;
	}
}
