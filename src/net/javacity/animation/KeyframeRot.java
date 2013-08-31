package net.javacity.animation;

import org.lwjgl.util.vector.Vector3f;

public class KeyframeRot {
	public float time;
	public Vector3f rot;
	
	public KeyframeRot(float ntime, Vector3f nrot){
		time = ntime;
		rot = new Vector3f(nrot);
	}
}
