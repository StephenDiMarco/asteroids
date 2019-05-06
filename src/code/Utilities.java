package code;

public class Utilities {
	 public static Polygon CreateObject(double[] shape, Point inPosition, int inRotation){
	    Point[] shapePoints = new Point[(shape.length)/2];

	    for(int i= 0, j = 0; i<shapePoints.length; i++, j+=2){
	    	shapePoints[i] = new Point(shape[j],shape[j+1]);
	    }

	    return new Polygon(shapePoints, inPosition, inRotation);
	 }
	 
}
