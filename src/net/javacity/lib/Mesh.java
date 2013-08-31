package net.javacity.lib;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import net.javacity.world.Position;

public class Mesh {
	
	ArrayList<Object3D> objs = new ArrayList<Object3D>();
	public Texture texture;
	
	private Position location;
	private Vector3f scale;
	
	public Mesh() {
	}
	
	public Mesh setLocation(Position location) {
		this.location = location;
		return this;
	}
	public Mesh setScale(Vector3f scale){
		this.scale = scale;
		return this;
	}
	
	public void addObject(Object3D obj){
		objs.add(obj);
	}
	
	public ArrayList<Object3D> getObjs() {
		return objs;
	}
	
	public void build(){
		int i=0;
		for(Object3D obj :objs){
			obj.build(true);
		}
	}
	
	public void render(){
		GL11.glPushMatrix();{
			GL11.glTranslatef(location.getX(), location.getY(), location.getY());
			
			GL11.glScalef(scale.x, scale.y, scale.z);
			if(texture!=null)
				texture.bind();
			
			for(Object3D obj :objs){
				obj.render();
			}
			if(texture!=null)
				texture.unbind();
		
		}GL11.glPopMatrix();
	}
	
	public void applyTexture(Texture texture){
		this.texture = texture;
	}
	
	public void applyTexture(ResourceLocation rl){
		this.texture = new Texture(rl);
	}
	
}
