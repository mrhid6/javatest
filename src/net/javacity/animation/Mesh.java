package net.javacity.animation;

public class Mesh {
	public String name;
	public int numTris;
	public int[] triIndices;
	public int matIndex;
	
	public boolean noTex = false;
	
	public Mesh(String nname, int nnumTris, int[] ntriIndices, int nmatIndex){
		name = nname;
		numTris = nnumTris;
		triIndices = ntriIndices;
		matIndex = nmatIndex;
	}
}