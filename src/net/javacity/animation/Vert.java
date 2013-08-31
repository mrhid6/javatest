package net.javacity.animation;
import org.lwjgl.util.vector.Vector3f;


public class Vert {
	public int index;
	public Vector3f pos;
	public int boneID;
	
	public Vert(int nindex){
		index = nindex;
	}
	
	public Vert(Vert v){
		index = v.index;
		pos = new Vector3f(v.pos);
		boneID = v.boneID;
	}
}
