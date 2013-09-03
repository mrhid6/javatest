package net.javacity.lib.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import de.matthiasmann.twl.utils.PNGDecoder;

public class TextureManager {
	
	private static HashMap<String, Texture>textureSheet = new HashMap<String, Texture>();
	private static HashMap<String, Integer>textureSheetInt = new HashMap<String, Integer>();
	
	public static Texture loadTexture(String path){
		return loadTexture(path, false);
	}
	
	public static Texture loadTexture(String path, boolean forceLoad){
		
		if(textureSheet.get(path)!=null && textureSheet.containsKey(path) && !forceLoad){
			Texture texture = textureSheet.get(path);
			return texture;
		}
		
		try {
			Texture texture = TextureLoader.getTexture("png", new FileInputStream(new File(path)));
			textureSheet.put(path, texture);
			return texture;
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void reloadTextures(){
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
		for(String key : textureSheet.keySet()){
			loadTexture(key, true);
		}
		
		for(String key : textureSheetInt.keySet()){
			setupTextures(key, true);
		}
	}
	
	public static void resetTextureManager(){
		textureSheet.clear();
		textureSheetInt.clear();
	}
	
	public static int setupTextures(String path) {
		return setupTextures(path, false);
	}
	
	public static int setupTextures(String path, boolean forceLoad) {
		
		if(textureSheetInt.get(path)!=null && textureSheetInt.containsKey(path) && !forceLoad){
			int texture = textureSheetInt.get(path);
			return texture;
		}
		
	    IntBuffer tmp = BufferUtils.createIntBuffer(1);
	    GL11.glGenTextures(tmp);
	    tmp.rewind();
	    try {
	        InputStream in = new FileInputStream(path);
	        PNGDecoder decoder = new PNGDecoder(in);

	        ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
	        decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
	        buf.flip();

	        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tmp.get(0));
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
	                GL11.GL_NEAREST);
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
	                GL11.GL_NEAREST);
	        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4);
	        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
	        
	        GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);

	    } catch (java.io.FileNotFoundException ex) {
	        System.out.println("Error " + path + " not found");
	    } catch (java.io.IOException e) {
	        System.out.println("Error decoding " + path);
	    }
	    tmp.rewind();
	    
	    textureSheetInt.put(path, tmp.get(0));
	    return tmp.get(0);
	}
	
}
