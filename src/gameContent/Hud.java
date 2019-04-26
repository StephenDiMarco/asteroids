package gameContent;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Hud {

	public int SCREEN_OVERLAY_TIMER_DEFAULT = 4000;
	public int UPGRADE_OVERLAY_TIME = 3000;
	public int SCORE_OVERLAY_TIME = 1500;
	
	private String screenOverlayMessage;
	private String previousScreenOverlayMessage;
	private long screenOverlayTimer;
	private int width;
	private int height;
	
	private Ship ship;
	private int score;
	private boolean pause; 
	private long lastTimeStamp;
	
	private class ScoreOverlay{
	    public String score;
	    public long duration;
	    public int x;
	    public int y;
	    
		public ScoreOverlay(int score, int duration, Point coordinates) {
			this.score = String.valueOf(score);
			this.duration = duration;
			this.x = (int)coordinates.x;
			this.y = (int)coordinates.y;
		}
	}
	
	private ArrayList <ScoreOverlay> scoreOverlays;

	public Hud(int width, int height, Ship ship) {
		this.width = width;
		this.height = height;
		this.score = 0;
		this.ship = ship;
		this.scoreOverlays = new ArrayList <ScoreOverlay>();
		this.pause = false;
		this.lastTimeStamp = System.currentTimeMillis();
	}
	
	public void updateOverlayMessage(String message) {
		updateOverlayMessage(message, SCREEN_OVERLAY_TIMER_DEFAULT);
	}
	
	public void updateOverlayMessage(String message, int time) {
		screenOverlayTimer = time;
		screenOverlayMessage = message;
	}

	public void updateScore(int points) {
		score += points;
	}
	
	public void updateScore(int points, Point coordinates) {
		score += points;
		scoreOverlays.add(new ScoreOverlay(points, SCORE_OVERLAY_TIME, coordinates));
	}
	
	public int getScore() {
		return score;
	}
	
	public void togglePause() {
		setPause(!pause);
	}
	
	public void setPause(boolean value) {
		pause = value;
		if(pause) {
			previousScreenOverlayMessage = screenOverlayMessage;
			updateOverlayMessage("Pause");
		}else {
			updateOverlayMessage(previousScreenOverlayMessage);
		}
	}
	
	public boolean getPause() {
		return pause;
	}
	
    public void update(Graphics2D brush) {
        long elsapedTime = System.currentTimeMillis() - lastTimeStamp;
		this.lastTimeStamp = System.currentTimeMillis();
		
        brush.setColor(Color.white);
        brush.drawString("Lives: " + ship.lives, 10, 20);
        
        updateChargeMeter(brush);
        
        updateHealthMeter(brush);
        
        brush.setColor(Color.white);
        brush.drawString("Score: " + score, 10, 95);
        
        if (screenOverlayTimer > 0) {
            updateScreenOverlay(brush, elsapedTime);
        } 
        
        if (!scoreOverlays.isEmpty() && !pause) {
        	updateScoreOverlays(brush, elsapedTime);
        }
    }

    private void updateChargeMeter(Graphics2D brush) {
        brush.setColor(Color.white);
        brush.drawString("Charge: ", 10, 35);
        brush.fillRect(55, 25, (int) Math.round(2 * ship.getMaxCharge()), 10);
        brush.setColor(Color.green);
        brush.fillRect(56, 26, (int) Math.round(2 * (ship.getCharge())) - 2, 8);
    }
    
    private void updateHealthMeter(Graphics2D brush) {
        brush.setColor(Color.white);
        brush.drawString("Health: ", 10, 50);
        brush.fillRect(55, 40, (int) Math.round(6 * ship.getMaxShield()), 10);
        brush.setColor(Color.red);
        brush.fillRect(56, 41, (int) Math.round(6 * (ship.getShield())) - 2, 8);
    }
    
    private void updateScreenOverlay(Graphics2D brush, long elsapedTime) {
        brush.setColor(Color.white);
        brush.setFont(new Font("Overlay", Font.BOLD, 30));
        if (pause) {
            brush.drawString(screenOverlayMessage, width / 2 - 6 * screenOverlayMessage.length(), height / 2);
        } else {
            brush.drawString(screenOverlayMessage, width / 2 - 6 * screenOverlayMessage.length(), 40);
        	screenOverlayTimer -= elsapedTime;
        }
    }
    
    private void updateScoreOverlays(Graphics2D brush, long elsapedTime) {
        brush.setColor(Color.gray);
        brush.setFont(new Font("Overlay", Font.BOLD, 15));
    	for (int i = 0; i < scoreOverlays.size(); i++) {
    		ScoreOverlay scoreOverlay = scoreOverlays.get(i); 
    		scoreOverlay.duration -= elsapedTime;
    		brush.drawString(scoreOverlay.score, scoreOverlay.x, scoreOverlay.y);
    		scoreOverlay.y--;
    		if(scoreOverlay.duration <= 0) {
    			scoreOverlays.remove(scoreOverlay);
    		}
    	}
    }
}
