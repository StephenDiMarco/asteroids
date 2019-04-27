package gameContent;
/*
CLASS: Polygon
DESCRIPTION: A polygon is a sequence of points in space defined by a set of
             such points, an offset, and a rotation. The offset is the
             distance between the origin and the center of the shape.
             The rotation is measured in degrees, 0-360.
USAGE: You are intended to instantiate this class with a set of points that
       forever defines its shape, and then modify it by repositioning and
       rotating that shape. In defining the shape, the relative positions
       of the points you provide are used, in other words: {(0,1),(1,1),(1,0)}
       is the same shape as {(9,10),(10,10),(10,9)}.
*/

class Polygon extends Shape {
  private Point[] shape;   // An array of points.
  public double rotation;  // Zero degrees is due east.
  private double area; 
  //Do to the formatting of this Polygon object, it requires 
  

  public Polygon(Point[] inShape, Point inPosition, double inRotation) {
	super(inPosition);
    shape = inShape;
    rotation = inRotation;
    
    // First, we find the shape's top-most left-most boundary, its origin.
    Point origin = new Point(shape[0].x,shape[0].y);
    for (Point p : shape) {
      if (p.x < origin.x) origin.x = p.x;
      if (p.y < origin.y) origin.y = p.y;
    }

    
    // Then, we orient all of its points relative to the real origin.
    for (Point p : shape) {
      p.x -= origin.x;
      p.y -= origin.y;
    }
    
    this.area = findArea();
  }
  
  public  Point[] getShape(){
	  return shape;
  }
  // "getPoints" applies the rotation and offset to the shape of the polygon.
  public Point[] getPoints() {
    Point center = findCenter();
    Point[] points = new Point[shape.length];
    int i = 0;
    for (Point p : shape) {
      double x = ((p.x-center.x) * Math.cos(Math.toRadians(rotation)))
               - ((p.y-center.y) * Math.sin(Math.toRadians(rotation)))
               + center.x/2 + position.x;
      double y = ((p.x-center.x) * Math.sin(Math.toRadians(rotation)))
               + ((p.y-center.y) * Math.cos(Math.toRadians(rotation)))
               + center.y/2 + position.y;
      points[i] = new Point(x,y);
      i++;
    }
    return points;
  }
  

  
  // "contains" implements some magical math (i.e. the ray-casting algorithm).
  public boolean contains(Point point) {
    Point[] points = getPoints();
    double crossingNumber = 0;
    for (int i = 0, j = 1; i < shape.length; i++, j=(j+1)%shape.length) {
      if ((((points[i].x < point.x) && (point.x <= points[j].x)) ||
           ((points[j].x < point.x) && (point.x <= points[i].x))) &&
          (point.y > points[i].y + (points[j].y-points[i].y)/
           (points[j].x - points[i].x) * (point.x - points[i].x))) {
        crossingNumber++;
      }
    }
    return crossingNumber%2 == 1;
  }
  
  
  //Checks if one polygon conflicts with another (used for checking if ship hits asteroid)
  public boolean intersect(Polygon otherPolygon){
	 
	  Point[] tempPoints = otherPolygon.getPoints();
	  
	  for(int i = 0; i < tempPoints.length; i++){
		  //Checks if the intersect
		  if(this.contains(tempPoints[i])){ 
			  return true;
		  }
	  }
	  //Not intersecting
	  return false;
  }
  
  protected void rotate(double degrees) {
	  rotation = (rotation+degrees)%360;
	  if( rotation <= 0 ){
		  rotation = 360 - rotation;
	  }
  }
  
  /*
  The following methods are private access restricted because, as this access
  level always implies, they are intended for use only as helpers of the
  methods in this class that are not private. They can't be used anywhere else.
  */
  
  // "findArea" implements some more magic math.
  private double findArea() {
    double sum = 0;
    for (int i = 0, j = 1; i < shape.length; i++, j=(j+1)%shape.length) {
      sum += shape[i].x*shape[j].y-shape[j].x*shape[i].y;
    }
    return Math.abs(sum/2);
  }
  
  // "findCenter" implements another bit of math.
  private Point findCenter() {
    Point sum = new Point(0,0);
    for (int i = 0, j = 1; i < shape.length; i++, j=(j+1)%shape.length) {
      sum.x += (shape[i].x + shape[j].x)
               * (shape[i].x * shape[j].y - shape[j].x * shape[i].y);
      sum.y += (shape[i].y + shape[j].y)
               * (shape[i].x * shape[j].y - shape[j].x * shape[i].y);
    }
    return new Point(Math.abs(sum.x/(6*area)),Math.abs(sum.y/(6*area)));
  }
}