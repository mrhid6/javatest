package net.javacity.animation;

import org.lwjgl.util.vector.Vector3f;

public class Tri {
	public int index;
	public int vert[];

	
	public UV[] uvs;
	
	public float radius;
	
	public Normal n1;
	public Normal n2;
	public Normal n3;
//	public Normal cn;
	
	
	public Tri(int nindex, int[] nvi, UV[] nuv,
			Normal[] nn/*, Normal ncn*/){
		vert = new int[nvi.length];
		uvs = new UV[nuv.length];
		index = nindex;
		for(int i = 0; i < nvi.length; i++){
			vert[i] = nvi[i];
		}
		
		for(int i = 0; i < nuv.length; i++){
			uvs[i] = nuv[i];
		}

		n1 = new Normal(nn[0]);
		n2 = new Normal(nn[1]);
		n3 = new Normal(nn[2]);
		//cn = new Normal(ncn);
	}
}
