package gameContent;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioManager {
		   
    public AudioManager() {
    	loadAudioFile("sound/fx/fire.wav");
    }
   
	private void loadAudioFile(String fileName) {
		try {
			URL url = getClass().getResource(fileName);
			System.out.println(url);
			Clip clip = AudioSystem.getClip();

			AudioInputStream ais = AudioSystem.getAudioInputStream( url );
			clip.open(ais);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		} 	      
	}
}
