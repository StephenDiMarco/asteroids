package gameContent;
import java.awt.event.*;
import java.util.ArrayList;

public class playerShip extends Ship implements KeyListener {

	//Initial position is left variable to fit the screen size
	public playerShip(Point[] inShape, Point inPosition, int timeInterval,  ArrayList<Bullet> bullets){
		super(inShape, inPosition, timeInterval, bullets);
		
		//Establishing weapon charge properties
		MAX_CHARGE = CHARGE = 15; CHARGE_RATE = 0.025; bullets = new ArrayList<Bullet>();
		//Establishing weapon strength properties
		STRENGTH = 1; BULLET_RANGE = 125; FIRE_DELAY_TIME = 1; 	FIRE_DELAY  = 0.0;  //No delay left
		//Establishing Engine properties
		ACCELERATION = 0.013;  ANGULAR_VELOCITY = .90; STABILIZE_COEFF = 0.99;
		//Establishing Shield properties
		MAX_SHIELDS = SHIELDS =  5;	OVERSHIELDS = true; OVERSHIELDS_DURATION = 200;
		//Initialising keys are false
		keyRotateCW = false; keyRotateCCW = false; keyAccelerate = false; 
		keyStabilize = false; keyFire = false;
	}	
	 

	/**************************************     Controls     ***************************************************/
	/** Handle the button click. */
    @Override
	public void keyPressed(KeyEvent e){
	    	//Checking pause
	    
				if(e.getKeyCode() == KeyEvent.VK_RIGHT){
					keyRotateCW = true; 
				}else if(e.getKeyCode() == KeyEvent.VK_LEFT){
					keyRotateCCW = true; 
				}else if(e.getKeyCode() == KeyEvent.VK_UP){
					keyAccelerate = true;
				//Stabilizer key, slows vehicle
				}else if(e.getKeyCode() == KeyEvent.VK_DOWN){
					keyStabilize = true;
				}else if(e.getKeyCode() == KeyEvent.VK_SPACE){
					keyFire = true; 
				}
				
		}
	    @Override
	    public void keyReleased(KeyEvent e){
				if(e.getKeyCode() == KeyEvent.VK_RIGHT){
					keyRotateCW = false;
				}else if(e.getKeyCode() == KeyEvent.VK_LEFT){
					keyRotateCCW = false;
				}else if(e.getKeyCode() == KeyEvent.VK_UP){
					keyAccelerate = false;
					//Stabilizer key, slows vehicle
				}else if(e.getKeyCode() == KeyEvent.VK_DOWN){
					keyStabilize = false;
				}else if(e.getKeyCode() == KeyEvent.VK_SPACE){
					keyFire = false;
				}	
	    }
	    
	    @Override
	    public void keyTyped(KeyEvent e){
	    	
	    }
}