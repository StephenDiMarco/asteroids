package gameContent;
/*
CLASS: Game
DESCRIPTION: A painted canvas in its own window, updated every tenth second.
USAGE: Extended by Asteroids.
NOTE: You don't need to understand the details here, no fiddling neccessary.
*/
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;


@SuppressWarnings("serial")
public abstract class Game extends Applet implements KeyListener{
	//Controls for aspects outside of ship control
	protected boolean keyPause;   		//Keeps track of pauses button hits
	protected boolean keyNewGame;  		//Keeps track of new game button hits
	protected int screenOverlay; 		//Keeps track of screenOverlays (and serves as a count down)
	protected String screenOverlayMessage;
	protected boolean on = true;
	//Graphics support
	protected Image buffer;
	//Time in 1/100 of a second
	private static int TIME_INTERVAL = 10;
	
	public static int GetTimeInterval( ) {
		return  TIME_INTERVAL;
	}
	
	public Game(){
		//Creating the control interface
		this.addKeyListener(this);
	}
	  /************************************Game Threading*******************************/
    // 'update' paints to a buffer then to the screen, then waits a tenth of
    // a second before repeating itself, assuming the game is on. This is done
    // to avoid a choppy painting experience if repainted in pieces.
    @Override
    public void update(Graphics brush) {
      paint(buffer.getGraphics());
      brush.drawImage(buffer,0,0,this);
  		
      if (on) {sleep(TIME_INTERVAL); repaint();}
    }
    
    // 'sleep' is a simple helper function used in 'update'.
    private void sleep(int time) {
      try {Thread.sleep(time);} catch(Exception exc){};
    }
    /************************************ Screen Overlay Methods *******************************/
	//Pause getter
	public boolean getPause(){return keyPause;}
	//New Game getter
	public boolean getNewGame(){return keyNewGame;}
	//New Game setter
	public void setNewGame(boolean newGame){keyNewGame = newGame;}
		

    /************************************ Misc Game Controls *******************************/
  public void keyPressed(KeyEvent e){
  	//Checking pause
	if(e.getKeyCode() == KeyEvent.VK_P){
		keyPause ^= true;
		screenOverlayMessage = "Pause";
	}else if(e.getKeyCode() == KeyEvent.VK_R){
		setNewGame(true);
	}
			
  }
  @Override
  public void keyReleased(KeyEvent e){

  }
  
  @Override
  public void keyTyped(KeyEvent e){
  	
  }
  
}