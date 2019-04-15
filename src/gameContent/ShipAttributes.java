package gameContent;
import java.util.Random;

public class ShipAttributes {
	
	/********************   Weapons  ********************/
	protected float STRENGTH;           
	protected int BULLET_RANGE;         
	protected double MAX_CHARGE;			
	protected double CHARGE_RATE;         
	protected double CHARGE;              
	protected double FIRE_DELAY_TIME;        
	protected double FIRE_DELAY;            
	
	/********************   Shields  ********************/
	protected double MAX_SHIELDS;
	protected double SHIELDS;
	protected boolean OVERSHIELDS;
	protected double OVERSHIELDS_DURATION;
	
	/********************   Movement  ********************/
	protected double MAX_ACCELERATION;  //Sets out maximum acceleration
	protected double ACCELERATION ;     //Used to determine change in velocity due to thrusters
	protected double ANGULAR_VELOCITY;  //Used to determine turning rate
	protected double STABILIZE_COEFF;    //Used to slow down craft
	
	public ShipAttributes() {
		CHARGE = MAX_CHARGE;
		SHIELDS = MAX_SHIELDS;
		OVERSHIELDS = true;
		ACCELERATION = 0;
		ANGULAR_VELOCITY = 0;
	}
	
	public enum ModifiableAttributeTypes {
		STRENGTH("Weapon Strength"),
		STABILIZER("Stabilizer"),
		BULLET_RANGE("Weapon Range"),
		MAX_CHARGE("Weapon Capacity"),
		CHARGE_RATE("Weapon Charge Rate"),
		MAX_SHIELDS("Shield Capacity"), 
		SHIELDS("Shields");

	    private String name;

	    private ModifiableAttributeTypes(String name) {
	        this.name = name;
	    }

	    public String getName() {
	        return name;
	    }
	    	    
	    public static ModifiableAttributeTypes Random() {
	    	return ModifiableAttributeTypes.values()[(int)(Math.random() * ModifiableAttributeTypes.values().length)];
	    }
	}

	public void modifyAttribute(ModifiableAttributeTypes type, float modifier) {
		 switch(type){
		 	case STRENGTH:  
		 		STRENGTH += (int)modifier;
        		break;
		 	case STABILIZER:  
		 		STABILIZE_COEFF -= modifier;
    			break;
	   		case BULLET_RANGE:  
	   			BULLET_RANGE += modifier; 
	        	break;
	   		case MAX_CHARGE:  
	   			MAX_CHARGE += modifier; 
	     		break;
	   		case CHARGE_RATE:
	   			CHARGE_RATE += modifier;
	   		case MAX_SHIELDS: 
	   			MAX_SHIELDS += modifier; 
	     		break;
	 		case SHIELDS: 
	 			SHIELDS += modifier; 
	   			break;
		 }
	}
}
