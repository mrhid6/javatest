package net.javacity.world;

public class Position {
	
	public float x;
	public float y;
	public float z;
	
	public Position(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Position copy(){
		return new Position(x, y, z);
	}
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getZ() {
		return z;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public void setZ(float z) {
		this.z = z;
	}
}
