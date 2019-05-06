package code;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public abstract class Game extends Frame implements KeyListener, WindowListener  {

	protected boolean keyNewGame;
	
	//New Game getter
	public boolean getNewGame(){return keyNewGame;}
	//New Game setter
	public void setNewGame(boolean newGame){keyNewGame = newGame;}
	
	protected boolean on = true;
	protected long elapsedTime;
	protected long lastTimeStamp;

	//Graphics support
	protected BufferedImage buffer;
	//Time in 1/100 of a second
	private static int TIME_INTERVAL = 10;
	
	public static int GetTimeInterval( ) {
		return  TIME_INTERVAL;
	}
	
	public Game(){
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
      elapsedTime = (int) (System.currentTimeMillis() - lastTimeStamp);
      if (on) {sleep(TIME_INTERVAL-(int)elapsedTime); repaint();}
    }
    
    // 'sleep' is a simple helper function used in 'update'.
    private void sleep(int time) {
      try {Thread.sleep(time);} catch(Exception exc){};
    }
 	

  @Override
  public void keyPressed(KeyEvent e){
			
  }
  
  @Override
  public void keyReleased(KeyEvent e){

  }
  
  @Override
  public void keyTyped(KeyEvent e){
  	
  }
  
  public void windowClosing(WindowEvent e)
  {
	dispose();
	System.exit(0);
  }
  
  public void windowOpened(WindowEvent e) { }
  
  public void windowIconified(WindowEvent e){ }
  
  public void windowClosed(WindowEvent e){ }
  
  public void windowDeiconified(WindowEvent e){ }
  
  public void windowActivated(WindowEvent e){ }
  
  public void windowDeactivated(WindowEvent e) { }
  
}