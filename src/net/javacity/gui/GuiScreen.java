package net.javacity.gui;

import net.javacity.Start;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

public class GuiScreen {

	private int width;
	private int height;
	
    private static int fps;
    private static long lastFPS;
    private static final boolean printFPS = true;
	
	public GuiScreen(int width, int height) throws Exception {
		this.width = width;
		this.height = height;
		
		lastFPS = getTime();
	}
	
	
	private static long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }
	
	public void render() {
		
		GL11.glPushMatrix();
		{
			//GL11.glTranslatef(Start.getCamera().x(), Start.getCamera().y(), Start.getCamera().z()+1);
			renderFPS();
		}
		GL11.glPopMatrix();
	}
	
	private void renderFPS() {
		//GuiText.drawText("FPS: "+fps, 0, 0, true, 0xffffff, 14);
	}
	
	public void update(){
		
		if(printFPS)
			updateFPS();
	}
	
	private static void updateFPS() {
        if (getTime() - lastFPS > 1000) {
            if (printFPS) {
            }
            fps = 0;
            lastFPS += 1000;
        }
        fps++;
    }
	
}
