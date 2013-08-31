package net.javacity.animation;

import org.lwjgl.util.vector.Vector3f;

public class Normal {
	public int index;
	public Vector3f pos;
	
	public Normal(int nindex){
		index = nindex;
	}
	
	public Normal(Normal n){
		index = n.index;
		pos = new Vector3f(n.pos);
	}
}
