package code;

public class Controller {

	protected boolean keyRotateCW;
	protected boolean keyRotateCCW;
	protected boolean keyAccelerate;
	protected boolean keyStabilize;
	protected boolean keyFire;
	
	public boolean isKeyRotateCW() {
		return keyRotateCW;
	}
	public boolean isKeyRotateCCW() {
		return keyRotateCCW;
	}
	public boolean isKeyAccelerate() {
		return keyAccelerate;
	}
	public boolean isKeyStabilize() {
		return keyStabilize;
	}
	public boolean isKeyFire() {
		return keyFire;
	}
	
	public Controller(){
		keyRotateCW = false; 
		keyRotateCCW = false; 
		keyAccelerate = false; 
		keyFire = false;
		keyStabilize = false; 
	}
	
	protected void resetKeys() {
		keyRotateCW = false; 
		keyRotateCCW = false; 
		keyAccelerate = false; 
		keyFire = false;
		keyStabilize = true; 
	}
	
	public void update() {
		
	}
}
