package net.javacity.models;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import net.javacity.animation.AnimatedModel;
import net.javacity.lib.ResourceLocation;
import net.javacity.world.Position;

public class Dwarf {
	
	public AnimatedModel model;
	public AnimatedModel axe;
	
	private Position location;
	
	ResourceLocation texture;
	ResourceLocation texture2;
	
	public Dwarf(Position location) {
		texture	= new ResourceLocation("res/mesh/dwarf2.jpg");
		texture2	= new ResourceLocation("res/mesh/axe.jpg");
		
		model = new AnimatedModel("res/mesh/dwarf2.ms3d", new Vector3f(1f,1f,1f),texture);
		axe = new AnimatedModel("res/mesh/axe.ms3d", new Vector3f(1f,1f,1f),texture2);
		
		this.location = location;
		model.init();
		axe.init();
		
		axe.syncAnimationTime(model.getAnimationTime());
	}
	
	public Position getLocation() {
		return location;
	}
	
	public void render(){
		GL11.glPushMatrix();{
			
			GL11.glTranslatef(location.getX(), location.getY(), location.getY());
			model.render();
		
			
			Vector3f[] accInfo = model.getJointInfo(model.getAnimationTime().getCurrentTime(), 0, 0);
			Matrix4f accMatrix = model.getJointMatrix(model.getAnimationTime().getCurrentTime(), 0, new Vector3f());
			//accMatrix.setIdentity();
			//accMatrix.m03 = accInfo[0].x;
			//accMatrix.m13 = accInfo[0].y;
			//accMatrix.m23 = accInfo[0].z;
			//System.out.println(-accInfo[0].x);
			GL11.glTranslatef(-accInfo[0].x,accInfo[0].y,-accInfo[0].z);
			
			//GL11.glRotatef( accInfo[1].x, 1, 0, 0);
			//GL11.glRotatef( accInfo[1].y, 0, 1, 0);
			//GL11.glRotatef( accInfo[1].z, 0, 0, 1);
			
			axe.render();
			
		}GL11.glPopMatrix();
	}
	
	public void updateEntity(){
		model.animate();
		axe.syncAnimationTime(model.getAnimationTime());
		
		
	}
	
}
