package code;

import java.awt.Color;

public class DebrisFactory {
	
    private int BASE_RADIUS = 2;
    private int RADIUS_MOD = 3;
    private int RADIUS_MOD_VARIANCE = 2;
    
    private int MIN_POINTS = 4;
    private int RANDOM_POINTS = 6;
    
	private float VEL_CONST = 0.15f;
	
	private Pallet greys;
	private AudioManager audioManager;
	
	public DebrisFactory(AudioManager audioManager) {
		this.audioManager = audioManager;
		greys = new Pallet(new Color(89, 115, 100));
	}
	
    public Debris createDebris(Point inPosition) {
        int points = (int)(MIN_POINTS + Math.floor(RANDOM_POINTS * Math.random()));
        Point[] shape = new Point[points];
        
        //Creating first point
        shape[0] = new Point(0, BASE_RADIUS);
        
        //Creating points other points which vary in distance from the center
        for (int i = 1; i < points; i++) {
            int x = (int) Math.round(BASE_RADIUS * (Math.sin(2 * Math.PI * i / points)) + RADIUS_MOD - (int) Math.round((RADIUS_MOD_VARIANCE) * Math.random()));
            int y = (int) Math.round(BASE_RADIUS * (Math.cos(2 * Math.PI * i / points)) + RADIUS_MOD - (int) Math.round((RADIUS_MOD_VARIANCE) * Math.random()));
            shape[i] = new Point(x, y);
        }
        
		Transform transform = new Transform(inPosition, VEL_CONST*0.1-(VEL_CONST*0.2*Math.random()), VEL_CONST*0.1-(VEL_CONST*0.2*Math.random()));
		PolygonCollider collider = new PolygonCollider(transform, shape);
		ColliderSprite sprite = new ColliderSprite(collider, greys.getRandomColor());
		
        return new Debris(transform, collider, sprite, audioManager);
    }
}
