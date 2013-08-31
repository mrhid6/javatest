package net.javacity.animation;

import org.lwjgl.util.vector.Vector3f;

public class KeyframePos {
	public float time;
	public Vector3f pos;
	
	public KeyframePos(float ntime, Vector3f npos){
		time = ntime;
		pos = new Vector3f(npos);
	}
}