package gameContent;

public class Star extends Circle{
	
	private boolean twinkle;
	
	Star(double x, double y, int radius, boolean twinkle){
	   super(x,y,radius);
	   this.twinkle = twinkle;
	}
	
	public void flipTwinkle(){
		twinkle ^= true;
	}
	@Override
	public int getRadius(){
		if(twinkle){
			return (int)super.getRadius()+1;
		}else{
			return (int)super.getRadius();
		}
	}
}
