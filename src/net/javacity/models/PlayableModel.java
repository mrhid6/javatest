package net.javacity.models;

import net.javacity.animation.AnimationTime;
import net.javacity.world.Position;

public abstract class PlayableModel {
	
	public Position location;
	
	public PlayableModel(float x, float y, float z) {
		
		this(new Position(x, y, z));
	}

	public PlayableModel(Position position) {
		this.location = position;
	}
	
	public Position getLocation() {
		return location;
	}
	
	public void setLocation(Position location) {
		this.location = location;
	}
	
	public void updateEntity(){}
	
	public void render(){}
	
	public void setAnimation(AnimationTime animation){}
	
	public abstract void setWalkAnimation();
	
	
}
