package gameContent;

public class Utilities {
	 public static Polygon CreateObject(double[] shape, Point inPosition, int inRotation){
		//Drawing object shape, begining
	    Point[] shapePoints = new Point[(shape.length)/2];
	    //Filling in points
	    for(int i= 0, j = 0; i<shapePoints.length; i++, j+=2){
	    	shapePoints[i] = new Point(shape[j],shape[j+1]);
	    }
	    //Creating object
	    return new Polygon(shapePoints, inPosition, inRotation);
	 }
	 
}
