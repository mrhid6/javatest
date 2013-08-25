
package net.javacity;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_POINT;
import static org.lwjgl.opengl.GL11.GL_POLYGON_MODE;
import static org.lwjgl.opengl.GL11.GL_POLYGON_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glScalef;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.javacity.lib.EulerCamera;
import net.javacity.world.Map;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class Start {

	private static final String WINDOW_TITLE = "Terrain!";
	private static final int[] WINDOW_DIMENSIONS = {1200, 650};
	private static final float ASPECT_RATIO = (float) WINDOW_DIMENSIONS[0] / (float) WINDOW_DIMENSIONS[1];
	private static final EulerCamera camera = new EulerCamera.Builder().setPosition(-5.4f, 19.2f,
			33.2f).setRotation(30, 61, 0).setAspectRatio(ASPECT_RATIO).setFieldOfView(60).build();

	private static boolean flatten = false;
	
	static Map worldMap;
	
	public static Texture mapTexture;

	private static void render() throws FileNotFoundException, IOException {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glLoadIdentity();

		camera.applyTranslations();
		if (flatten) {
			glScalef(1, 0, 1);
		}

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
					try {
						mapTexture = loadTexture("ground.png");
						worldMap.loadMap();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_P) {
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

	private static void cleanUp(boolean asCrash) {
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
		
		glEnable(GL_DEPTH_TEST);
		glClearColor(0, 0.75f, 1, 1);
		
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
		
		setUpMatrices();
		worldMap = new Map();
		try {
			mapTexture = loadTexture("ground.png");
			worldMap.loadMap();
			
			enterGameLoop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		cleanUp(false);
	}
}