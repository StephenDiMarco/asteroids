package code;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GameObjectRegistry {
	
	private HashMap<Layers, ArrayList<ColliderSpriteGameObject>> gameObjects;
	
	public enum Layers {
	  PASSIVE_HOSTILE,    // Asteroids
	  ACTIVE_HOSTILE,     // Enemy ships, enemy bullets
	  PASSIVE_FRIENDLY,   // Upgrades
	  PLAYER              // Player ship, drones
	}
	
	private HashMap<Layers, Layers[]> collisionMap;
	
	public GameObjectRegistry() {
		gameObjects = new HashMap<Layers, ArrayList<ColliderSpriteGameObject>>();
		initializeCollisionMap();
		for (Layers layer : Layers.values()) { 
			gameObjects.put(layer, new ArrayList<ColliderSpriteGameObject>()); 
		}
	}
	
	private void initializeCollisionMap() {
		collisionMap = new HashMap<Layers, Layers[]>();
		collisionMap.put(Layers.PLAYER, new Layers[] {Layers.PASSIVE_HOSTILE, Layers.ACTIVE_HOSTILE});
		collisionMap.put(Layers.ACTIVE_HOSTILE, new Layers[] {Layers.PASSIVE_HOSTILE, Layers.PLAYER});
		collisionMap.put(Layers.PASSIVE_FRIENDLY, new Layers[] {Layers.ACTIVE_HOSTILE, Layers.PASSIVE_HOSTILE});
		collisionMap.put(Layers.PASSIVE_HOSTILE, new Layers[] {Layers.PLAYER, Layers.PASSIVE_FRIENDLY});

	}

	
	public int getLayerSize(Layers layer) {
		return gameObjects.get(layer).size();
	}

	public void register(ColliderSpriteGameObject gameObject, Layers layer) {
		gameObjects.get(layer).add(gameObject);
	}
	
	public void update() {
		for (ArrayList<ColliderSpriteGameObject> gameObjectLayer : gameObjects.values()) {
 			for(ColliderSpriteGameObject gameObject : gameObjectLayer){
 				gameObject.update();
			}
		}
	}
	
	public void checkCollisions() {
		for (Layers activeLayer : gameObjects.keySet()) {
			for(Layers targetLayer : collisionMap.get(activeLayer)) {
				checkCollisionOnLayer(activeLayer, targetLayer);
			}
		}	
	}

	private void checkCollisionOnLayer(Layers activeLayer, Layers targetLayer) {
		for(ColliderSpriteGameObject activeObject : gameObjects.get(activeLayer)){
			for(ColliderSpriteGameObject targetObject : gameObjects.get(targetLayer)){
                if(activeObject.collides(targetObject)) {
                	activeObject.onCollision(targetObject);
                }
			}
		}
	}
	
	public void clean() {
		for (ArrayList<ColliderSpriteGameObject> gameObjectLayer : gameObjects.values()) {
			Iterator<ColliderSpriteGameObject> objects = gameObjectLayer.iterator();
			while(objects.hasNext()){
				ColliderSpriteGameObject object = objects.next();
				if(!object.isAlive()) {
					object.onDeath();
					objects.remove();
				}
			}
		}
	}
	
	public void paint(Graphics2D paint) {
		for (ArrayList<ColliderSpriteGameObject> gameObjectLayer : gameObjects.values()) {
			for(ColliderSpriteGameObject gameObject : gameObjectLayer){
			    gameObject.paint(paint);
			}
		}
	}
	
	public void resetNonPlayerGameObjects() {
		for (Layers layer : Layers.values()) { 
			if(layer != Layers.PLAYER) {
				gameObjects.put(layer, new ArrayList<ColliderSpriteGameObject>()); 				
			}
		}
	}
	
}
