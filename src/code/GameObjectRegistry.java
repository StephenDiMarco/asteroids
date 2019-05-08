package code;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GameObjectRegistry {
	
	private HashMap<Layers, ArrayList<GameObject>> gameObjects;
	
	public enum Layers {
	  PASSIVE_HOSTILE,    // Asteroids
	  ACTIVE_HOSTILE,     // Enemy ships, enemy bullets
	  PASSIVE_FRIENDLY,   // Upgrades
	  ACTIVE_FRIENDLY     // Player ship, drones
	}
	
	private static HashMap<Layers, Layers[]> collisionMap;
	
	public GameObjectRegistry() {
		gameObjects = new HashMap<Layers, ArrayList<GameObject>>();
		initializeCollisionMap();
		for (Layers layer : Layers.values()) { 
			gameObjects.put(layer, new ArrayList<GameObject>()); 
		}
	}
	
	private void initializeCollisionMap() {
		collisionMap.put(Layers.ACTIVE_FRIENDLY, new Layers[] {Layers.PASSIVE_HOSTILE, Layers.ACTIVE_HOSTILE});
		collisionMap.put(Layers.ACTIVE_HOSTILE, new Layers[] {Layers.PASSIVE_HOSTILE, Layers.ACTIVE_FRIENDLY});
		collisionMap.put(Layers.PASSIVE_HOSTILE, new Layers[] {Layers.PASSIVE_HOSTILE, Layers.ACTIVE_HOSTILE, Layers.PASSIVE_FRIENDLY});
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
