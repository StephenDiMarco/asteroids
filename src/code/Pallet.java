package code;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class Pallet {

	private ArrayList<Color> pallet;
	private Random random; 
	
	public Pallet(Color baseColor) {
		pallet = new ArrayList<Color>();
		pallet.add(baseColor);
		pallet.add(baseColor.brighter());
		pallet.add(baseColor.darker());
		
		random = new Random();
	}
	
	public Color getRandomColor() {
	    return pallet.get(random.nextInt(pallet.size())); 
	}
}
