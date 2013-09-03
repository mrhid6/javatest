
package net.javacity;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_POINT;
import static org.lwjgl.opengl.GL11.GL_POLYGON_MODE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL11.glPolygonMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import net.javacity.animation.AnimatedModel;
import net.javacity.gui.GuiScreen;
import net.javacity.lib.EulerCamera;
import net.javacity.lib.Mesh;
import net.javacity.lib.ResourceLocation;
import net.javacity.lib.managers.Animations;
import net.javacity.lib.managers.AssetManager;
import net.javacity.lib.managers.ObjectLoader;
import net.javacity.lib.managers.ShaderLoader;
import net.javacity.lib.managers.TextureManager;
import net.javacity.models.Dwarf;
import net.javacity.player.Player;
import net.javacity.player.Skill;
import net.javacity.world.Map;
import net.javacity.world.Position;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;

public class Start {

	public static Start s;
	public static final int[] WINDOW_DIMENSIONS = {1200, 650};
	private static final float ASPECT_RATIO = (float) WINDOW_DIMENSIONS[0] / (float) WINDOW_DIMENSIONS[1];
	private static final EulerCamera camera = new EulerCamera.Builder().setPosition(-5.4f, 19.2f,
			33.2f).setRotation(30, 61, 0).setAspectRatio(ASPECT_RATIO).setFieldOfView(60).build();

	private static Dwarf d;


	private static Dwarf d2;
	
	private static Mesh lamp;
	
	public static Player thePlayer;

	private static final String WINDOW_TITLE = "Working Title!";

	static AnimatedModel acc;

	static Mesh mesh;

	static AnimatedModel model;
	
	static Texture tex;
	static float timeToNextFrame = -1;
	
	static Map worldMap;
	
	public static GuiScreen gui;
	
	public Start(){
		
		setUpDisplay();
		setUpShaders();
		setUpStates();
		
		setUpMatrices();

		try {
			
			gui = new GuiScreen(WINDOW_DIMENSIONS[0], WINDOW_DIMENSIONS[1]);
			AssetManager.loadAssets();
			initGL();
			d = new Dwarf(new Position(0, 0, 0));
			thePlayer = new Player(d, new Position(0, 0, 0));
			
			d2 = new Dwarf(new Position(0, 0, 0));
			d2.setAnimation(Animations.getAnimation("dwarf_end"));
			
			lamp = ObjectLoader.loadobject(new BufferedReader(new FileReader(new File("res/mesh/lamp.obj"))));
			lamp.applyTexture(new ResourceLocation("res/textures/lamp.png"));
			lamp.setLocation(new Position(0, 0, 0));
			lamp.setScale(new Vector3f(5,5,5));
			
			Skill s = new Skill(2);
			enterGameLoop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		cleanUp(false,true);
	}
	public static void main(String[] args) {
		s = new Start();
	}
	private static void cleanUp(boolean asCrash,boolean end) {
		//glDeleteLists(mesh.getObjectlist(), 1);
		System.err.println(GLU.gluErrorString(glGetError()));
		Display.destroy();
		if(end)
			System.exit(asCrash ? 1 : 0);
	}
	
	private static void enterGameLoop() throws Exception{
		while (!Display.isCloseRequested()) {
			renderGL();
			input();
			update();
		}
	}
	
	private static void initGL() {
		GL11.glViewport(0, 0, WINDOW_DIMENSIONS[0], WINDOW_DIMENSIONS[1]); // Reset The Current Viewport
		//GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
		GL11.glLoadIdentity(); // Reset The Projection Matrix
		GLU.gluPerspective(45.0f, ((float) WINDOW_DIMENSIONS[0] / (float) WINDOW_DIMENSIONS[1]), 0.1f, 1000); // Calculate The Aspect Ratio Of The Window
		GL11.glMatrixMode(GL11.GL_MODELVIEW); // Select The Modelview Matrix
		GL11.glLoadIdentity(); // Reset The Modelview Matrix

		GL11.glShadeModel(GL11.GL_SMOOTH); // Enables Smooth Shading
		GL11.glClearColor(0f, 0.3f, 0.5f, 1f); // Black Background
		GL11.glClearDepth(1.0f); // Depth Buffer Setup
		GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
		GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Test To Do
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST); // Really Nice Perspective Calculations
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.9f);
	}
	
	private static void input() {
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				
				if (Keyboard.getEventKey() == Keyboard.KEY_L) {
					
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
				
				if(Keyboard.getEventKey() == Keyboard.KEY_X){
					cleanUp(false, false);
					TextureManager.resetTextureManager();
					s = new Start();
				}if(Keyboard.getEventKey() == Keyboard.KEY_R){
					TextureManager.reloadTextures();
				}
				if(Keyboard.getEventKey() == Keyboard.KEY_T){
					d.updateEntity();
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
	
	private static void renderGL() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glLoadIdentity();
		gui.render();

		camera.applyTranslations();
		
		thePlayer.render();
		//d2.render();
		//lamp.render();

	}

	private static void setUpDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(WINDOW_DIMENSIONS[0], WINDOW_DIMENSIONS[1]));
			Display.setVSyncEnabled(true);
			Display.setTitle(WINDOW_TITLE);
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			cleanUp(true,true);
		}
	}
	
	//TODO
	private static void setUpShaders(){
		int shaderprogram = ShaderLoader.loadShaderPair("res/shaders/ao.vs", "res/shaders/ao.fs");
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
	public static EulerCamera getCamera() {
		return camera;
	}
	
	private static void update() {
		gui.update();
		
		thePlayer.update();
		//d.updateEntity();
		//d2.updateEntity();
		Display.update();
		Display.sync(60);
	}
}