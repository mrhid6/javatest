package net.javacity.lib;

import net.javacity.lib.managers.TextureManager;

import org.lwjgl.opengl.GL11;

public class Texture {
	
	private int textureId;
	
	public Texture(String filename){
		textureId = TextureManager.setupTextures(filename);
	}
	
	public Texture(ResourceLocation location){
		this(location.getLocation());
	}
	
	public void bind(){
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
	}
	
	public void unbind(){
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
	}
	
}
