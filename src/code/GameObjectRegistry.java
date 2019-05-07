package code;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GameObjectRegistry {
	
	HashMap<Layers, ArrayList<GameObject>> gameObjects;
	
	enum Layers {
	  PASSIVE_HOSTILE,
	  ACTIVE_HOSTILE,
	  PASSIVE_FRIENDLY,
	  ACTIVE_FRIENDLY
	}
	
	public GameObjectRegistry() {
		gameObjects = new HashMap<Layers, ArrayList<GameObject>>();	
		for (Layers layer : Layers.values()) { 
			gameObjects.put(layer, new ArrayList<GameObject>()); 
		}
	}
	
	public int getLayerSize(Layers layer) {
		return gameObjects.get(layer).size();
	}

	public void register(GameObject gameObject, Layers layer) {
		gameObjects.get(layer).add(gameObject);
	}
	
	public void update() {
		for (ArrayList<GameObject> gameObjectLayer : gameObjects.values()) {
			for(GameObject gameObject : gameObjectLayer){
			    gameObject.update();
			}
		}
	}
	
	public void clean() {
		for (ArrayList<GameObject> gameObjectLayer : gameObjects.values()) {
			Iterator<GameObject> objects = gameObjectLayer.iterator();
			while(objects.hasNext()){
				GameObject object = objects.next();
				if(!object.alive()) {
					objects.remove();
				}
			}
		}
	}
	
	public void paint(Graphics2D paint) {
		for (ArrayList<GameObject> gameObjectLayer : gameObjects.values()) {
			for(GameObject gameObject : gameObjectLayer){
			    gameObject.paint(paint);
			}
		}
	}
	
}
