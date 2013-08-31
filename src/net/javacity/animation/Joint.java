package net.javacity.animation;

import org.lwjgl.util.vector.Vector3f;

public class Joint {
	public String name;
	public String parentName;
	public Vector3f rot;
	public Vector3f pos;
	
	public int numKeyframesRot;
	public int numKeyframesTrans;
	
	public KeyframeRot[] KeyframeRot;
	public KeyframePos[] KeyframePos;
	public int parentIndice = -1;
	public int indice = -1;
	
	public int curTransFrame = 0;
	public int curRotFrame = 0;
	
	public Joint(int ni){
		indice = ni;
	}
	
	public Vector3f[] getRotFrameByTime(float frameTime){
		
		int frameNum = 0;
		
		for(int i = 0; i < numKeyframesRot; i++)
		{
			if(frameTime >= KeyframeRot[i].time) frameNum = i;
		}
		
		int prevFrame = frameNum-1;
		if(prevFrame < 0) prevFrame = 0;
		//System.out.println("KeyframeRot[frameNum].rot.x: " + KeyframeRot[frameNum].rot.x + "\t" + KeyframeRot[frameNum].rot.y + "\t" + KeyframeRot[frameNum].rot.z);
		return new Vector3f[] {KeyframeRot[prevFrame].rot, KeyframeRot[frameNum].rot};
	}
	
	public Vector3f[] getTransFrameByTime(float frameTime){
		
		int frameNum = 0;
		
		for(int i = 0; i < numKeyframesTrans; i++){
			
			if(frameTime >= KeyframePos[i].time) frameNum = i;
		}
		
		int prevFrame = frameNum-1;
		if(prevFrame < 0) prevFrame = 0;
		//System.out.println("KeyframeRot[frameNum].rot.x: " + KeyframeRot[frameNum].rot.x + "\t" + KeyframeRot[frameNum].rot.y + "\t" + KeyframeRot[frameNum].rot.z);
		return new Vector3f[] {KeyframePos[prevFrame].pos, KeyframePos[frameNum].pos};
	}
}
