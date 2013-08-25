package net.javacity;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtils {
	
	public static void clearScreen()
	{
		//TODO: Stencil Buffer
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	
}
