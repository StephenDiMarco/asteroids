package gameContent;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioManager {
		  
	private int ONCE = 0;
	private String path = "src/sound/";
	
	public String THEME = "music/theme.wav";
	
	private String FX = "fx/";
	public String PLAYER_WEAPON 		= FX + "player-weapon.wav";
	public String ENEMY_WEAPON 			= FX + "enemy-weapon.wav";
	public String PLAYER_DESTROYED 		= FX + "player-destroyed.wav";
	public String SHIP_DESTROYED 		= FX + "ship-destroyed.wav";
	public String ASTEROID_DESTROYED 	= FX + "asteroid-destroyed.wav";
	public String UPGRADE_PICKUP	 	= FX + "upgrade-pickup.wav";
	
    public AudioManager() {
    	playLoop(THEME);
    }
   
    public void playLoop(String fileName) {
    	loadAudioFile(fileName, Clip.LOOP_CONTINUOUSLY);
    }
    
    public void playOnce(String fileName) {
    	loadAudioFile(fileName, ONCE);
    }
    
	private void loadAudioFile(String fileName, int mode) {
		try {

			Clip clip = AudioSystem.getClip();
			AudioInputStream ais = AudioSystem.getAudioInputStream( new File(path + fileName) );
			clip.open(ais);
			clip.loop(mode);
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		} 	      
	}
}
