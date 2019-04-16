package gameContent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class PlayerController extends Controller implements KeyListener {

	//Initial position is left variable to fit the screen size
	public PlayerController(){
		super();
	}	
	
	@Override	
	public void keyPressed(KeyEvent e)
    {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			keyRotateCW = true; 
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			keyRotateCCW = true; 
		}
		else if(e.getKeyCode() == KeyEvent.VK_UP)
		{
			keyAccelerate = true;
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			keyStabilize = true;
		}
		else if(e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			keyFire = true; 
		}				
	}
	
	@Override
    public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			keyRotateCW = false;
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT){
			keyRotateCCW = false;
		}
		else if(e.getKeyCode() == KeyEvent.VK_UP){
			keyAccelerate = false;
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN){
			keyStabilize = false;
		}
		else if(e.getKeyCode() == KeyEvent.VK_SPACE){
			keyFire = false;
		}	
    }
    
	@Override
    public void keyTyped(KeyEvent e)    {
    	
    }
}