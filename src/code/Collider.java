package code;

import java.awt.geom.Path2D;

public class Collider {

	private Transform transform;
    private Point[] shape;
    private Point[] boundingBox;
    private double area; 
    
	public Collider(Transform transform, Point[] inShape) {
		this.transform = transform;
		this.shape = inShape;
		init();
	}
    
    public void init() {
        // First, we find the shape's top-most left-most boundary, its origin.
        Point origin = new Point(shape[0].x, shape[0].y);
        for (Point p: shape) {
            if (p.x < origin.x) origin.x = p.x;
            if (p.y < origin.y) origin.y = p.y;
        }

        // Then, we orient all of its points relative to the real origin.
        for (Point p: shape) {
            p.x -= origin.x;
            p.y -= origin.y;
        }

        this.area = findArea();
        boundingBox = this.clone();
    }

    public void update() {
        updateBoundingBox();
    }

    public Point[] getBoundingBox() {
        return boundingBox;
    }

    public Point[] clone() {
        Point[] shapeClone = new Point[shape.length];
        for (int i = 0; i < shapeClone.length; i++) {
            shapeClone[i] = shape[i].clone();
        }
        return shapeClone;
    }

    private void updateBoundingBox() {
        Point center = findCenter();
        int i = 0;
        for (Point p: shape) {
            boundingBox[i].x = ((p.x - center.x) * Math.cos(Math.toRadians(transform.getRotation()))) -
                ((p.y - center.y) * Math.sin(Math.toRadians(transform.getRotation()))) +
                center.x / 2 + transform.getPosition().x;
            boundingBox[i].y = ((p.x - center.x) * Math.sin(Math.toRadians(transform.getRotation()))) +
                ((p.y - center.y) * Math.cos(Math.toRadians(transform.getRotation()))) +
                center.y / 2 + transform.getPosition().y;
            i++;
        }
    }

    public Path2D.Double getBoundingBoxPath() {
        Point[] tempPoints = getBoundingBox();
        //Creating a shape
        Path2D.Double path = new Path2D.Double();
        //Creating the first point
        path.moveTo(tempPoints[0].x, tempPoints[0].y);
        //Creating the remaining points
        for (int i = 1; i < tempPoints.length; i++) {
            path.lineTo(tempPoints[i].x, tempPoints[i].y);
        }
        return path;
    }


    // "contains" implements some magical math (i.e. the ray-casting algorithm).
    public boolean contains(Point point) {
        Point[] points = getBoundingBox();
        double crossingNumber = 0;
        for (int i = 0, j = 1; i < shape.length; i++, j = (j + 1) % shape.length) {
            if ((((points[i].x < point.x) && (point.x <= points[j].x)) ||
                    ((points[j].x < point.x) && (point.x <= points[i].x))) &&
                (point.y > points[i].y + (points[j].y - points[i].y) /
                    (points[j].x - points[i].x) * (point.x - points[i].x))) {
                crossingNumber++;
            }
        }
        return crossingNumber % 2 == 1;
    }


    //Checks if one polygon conflicts with another (used for checking if ship hits asteroid)
    public boolean intersect(Polygon otherPolygon) {

        Point[] tempPoints = otherPolygon.getBoundingBox();

        for (int i = 0; i < tempPoints.length; i++) {
            //Checks if the intersect
            if (this.contains(tempPoints[i])) {
                return true;
            }
        }
        //Not intersecting
        return false;
    }

    protected void rotate(double degrees) {
        transform.setRotation((transform.getRotation() + degrees) % 360);
        if (transform.getRotation() <= 0) {
        	transform.setRotation(360 - transform.getRotation());
        }
    }
	
    private double findArea() {
        double sum = 0;
        for (int i = 0, j = 1; i < shape.length; i++, j = (j + 1) % shape.length) {
            sum += shape[i].x * shape[j].y - shape[j].x * shape[i].y;
        }
        return Math.abs(sum / 2);
    }

    private Point findCenter() {
        Point sum = new Point(0, 0);
        for (int i = 0, j = 1; i < shape.length; i++, j = (j + 1) % shape.length) {
            sum.x += (shape[i].x + shape[j].x) *
                (shape[i].x * shape[j].y - shape[j].x * shape[i].y);
            sum.y += (shape[i].y + shape[j].y) *
                (shape[i].x * shape[j].y - shape[j].x * shape[i].y);
        }
        return new Point(Math.abs(sum.x / (6 * area)), Math.abs(sum.y / (6 * area)));
    }

}
