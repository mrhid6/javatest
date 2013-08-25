/*
 * Copyright (c) 2013, Oskar Veerhoek
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

package net.javacity;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_POINT;
import static org.lwjgl.opengl.GL11.GL_POLYGON_MODE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.javacity.lib.EulerCamera;
import net.javacity.lib.ShaderLoader;
import net.javacity.world.Map;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

/**
 * A 3D terrain loaded from a height-map and a lookup texture. Press 'L' to reload the shader and texture files. Press
 * 'P' to switch between normal, point, and wire-frame mode. Press 'F' to flatten the terrain. Click here for an image:
 * https://twitter.com/i/#!/CodingUniverse/media/slideshow?url=pic.twitter.com%2FDgMdZ5jm.
 *
 * @author Oskar Veerhoek
 */
public class Start {

	private static final String WINDOW_TITLE = "Terrain!";
	private static final int[] WINDOW_DIMENSIONS = {1200, 650};
	private static final float ASPECT_RATIO = (float) WINDOW_DIMENSIONS[0] / (float) WINDOW_DIMENSIONS[1];
	private static final EulerCamera camera = new EulerCamera.Builder().setPosition(-5.4f, 19.2f,
			33.2f).setRotation(30, 61, 0).setAspectRatio(ASPECT_RATIO).setFieldOfView(60).build();
	/**
	 * The shader program that will use the lookup texture and the height-map's vertex data to draw the terrain.
	 */
	private static int shaderProgram;
	/**
	 * The texture that will be used to find out which colours correspond to which heights.
	 */
	/**
	 * The display list that will contain the height-map's vertex data.
	 */
	private static int heightmapDisplayList;
	/**
	 * The points of the height. The first dimension represents the z-coordinate. The second dimension represents the
	 * x-coordinate. The float value represents the height.
	 */
	private static float[][] data;
	/**
	 * Whether the terrain should vary in height or be displayed on a grid.
	 */
	private static boolean flatten = false;
	
	static Map worldMap;
	
	public static Texture mapTexture;

	private static void render() throws FileNotFoundException, IOException {
		// Clear the pixels on the screen and clear the contents of the depth buffer (3D contents of the scene)
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		// Reset any translations the camera made last frame update
		glLoadIdentity();
		// Apply the camera position and orientation to the scene
		camera.applyTranslations();
		if (flatten) {
			glScalef(1, 0, 1);
		}
		// Render the heightmap using the shaders that are being used
		glDisable(GL_POLYGON_SMOOTH);
		mapTexture.bind();
		glCallList(worldMap.mapList);
		
		glEnable(GL_POLYGON_SMOOTH);
	}

	private static void input() {
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
					flatten = !flatten;
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_L) {
					// Reload the shaders and the heightmap data.
					/*glUseProgram(0);
					glDeleteProgram(shaderProgram);
					glActiveTexture(GL_TEXTURE0);
					glBindTexture(GL_TEXTURE_2D, 0);
					glActiveTexture(GL_TEXTURE1);
					glBindTexture(GL_TEXTURE_2D, 0);
					glDeleteTextures(lookupTexture);
					glDeleteTextures(textureTerrain);*/
					//setUpShaders();
					try {
						mapTexture = loadTexture("ground.png");
						worldMap.loadMap();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_P) {
					// Switch between normal mode, point mode, and wire-frame mode.
					int polygonMode = glGetInteger(GL_POLYGON_MODE);
					if (polygonMode == GL_LINE) {
						glPolygonMode(GL_FRONT, GL_FILL);
					} else if (polygonMode == GL_FILL) {
						glPolygonMode(GL_FRONT, GL_POINT);
					} else if (polygonMode == GL_POINT) {
						glPolygonMode(GL_FRONT, GL_LINE);
					}
				}
			}
		}
		if (Mouse.isButtonDown(0)) {
			Mouse.setGrabbed(true);
		} else if (Mouse.isButtonDown(1)) {
			Mouse.setGrabbed(false);
		}
		if (Mouse.isGrabbed()) {
			camera.processMouse(1, 80, -80);
		}
		camera.processKeyboard(16, 1);
	}

	private static Texture loadTexture(String path) throws Exception {
		Texture texture = TextureLoader.getTexture("png", new FileInputStream(new File("res/"+path)));
		return texture;
	}

	private static void setUpShaders() {
		shaderProgram = ShaderLoader.loadShaderPair("res/shaders/textured_landscape.vs", "res/shaders/textured_landscape.fs");
		glUseProgram(shaderProgram);
	}

	private static void cleanUp(boolean asCrash) {
		glUseProgram(0);
		glDeleteProgram(shaderProgram);
		glDeleteLists(worldMap.mapList, 1);
		System.err.println(GLU.gluErrorString(glGetError()));
		Display.destroy();
		System.exit(asCrash ? 1 : 0);
	}

	private static void setUpMatrices() {
		camera.applyPerspectiveMatrix();
	}

	private static void setUpStates() {
		camera.applyOptimalStates();
		glPointSize(2);
		// Enable the sorting of shapes from far to near
		glEnable(GL_DEPTH_TEST);
		// Set the background to a blue sky colour
		glClearColor(0, 0.75f, 1, 1);
		// Remove the back (bottom) faces of shapes for performance
		glEnable(GL_CULL_FACE);

		glEnable(GL_TEXTURE_2D);
	}

	private static void update() {
		Display.update();
		Display.sync(60);
	}

	private static void enterGameLoop() throws Exception{
		while (!Display.isCloseRequested()) {
			render();
			input();
			update();
		}
	}

	private static void setUpDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(WINDOW_DIMENSIONS[0], WINDOW_DIMENSIONS[1]));
			Display.setVSyncEnabled(true);
			Display.setTitle(WINDOW_TITLE);
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			cleanUp(true);
		}
	}

	public static void main(String[] args) {
		setUpDisplay();
		setUpStates();
		//setUpHeightmap();
		//setUpShaders();
		
		setUpMatrices();
		worldMap = new Map();
		try {
			mapTexture = loadTexture("ground.png");
			worldMap.loadMap();
			
			enterGameLoop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cleanUp(false);
	}
}