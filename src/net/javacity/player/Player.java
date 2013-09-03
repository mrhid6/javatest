package net.javacity.player;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.javacity.Start;
import net.javacity.lib.Camera;
import net.javacity.lib.managers.Animations;
import net.javacity.models.PlayableModel;
import net.javacity.world.Position;

public class Player {

	private PlayableModel model;
	private Position location;

	private float yaw = 80;
	private float pitch;

	private boolean walking = false;

	public Player(PlayableModel model, Position location) {
		this.model = model;
		this.model.setLocation(location);
		this.location = location;
	}

	public void update(){
		
		model.updateEntity();
		model.setLocation(location);
		
	}

	public Position getLocation() {
		return location;
	}

	public void setLocation(Position location) {
		this.location = location;
	}

	public void render() {
		GL11.glPushMatrix();{
			model.render();

		}GL11.glPopMatrix();
	}

	public void moveFromLook(float dx, float dy, float dz) {
		Start.getCamera().moveFromLook(dx, dy, dz);
	}

	public Position getCameraPosition(float dx, float dy, float dz) {
		Position pos = new Position(Start.getCamera().x(), Start.getCamera().y(), Start.getCamera().z());

		pos.z = (float) (dx * (float) cos(toRadians(yaw - 90)) + dz * cos(toRadians(yaw)));
		pos.x = (float) (dx * (float) sin(toRadians(yaw - 90)) + dz * sin(toRadians(yaw)));
		pos.y = (float) (dy * (float) sin(toRadians(pitch - 90)) + dz * sin(toRadians(pitch)));

		return pos;
	}

	public void setWalkAnimation(){
		if(!walking){
			model.setWalkAnimation();
			walking = true;
		}
	}

	public void handleKey(){
		
		float delta = 16.0F;
		float speedX = 5.0F;
		float speedZ = 5.0F;

		boolean keyUp = Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W);
		boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S);
		boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A);
		boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D);
		boolean flyUp = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
		boolean flyDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);

		if (keyUp && keyRight && !keyLeft && !keyDown) {
			moveFromLook(speedX * delta * 0.003f, 0, -speedZ * delta * 0.003f);
		}
		if (keyUp && keyLeft && !keyRight && !keyDown) {
			moveFromLook(-speedX * delta * 0.003f, 0, -speedZ * delta * 0.003f);
		}
		if (keyUp && !keyLeft && !keyRight && !keyDown) {
			moveFromLook(0, 0, -speedZ * delta * 0.003f);
		}
		if (keyDown && keyLeft && !keyRight && !keyUp) {
			moveFromLook(-speedX * delta * 0.003f, 0, speedZ * delta * 0.003f);
		}
		if (keyDown && keyRight && !keyLeft && !keyUp) {
			moveFromLook(speedX * delta * 0.003f, 0, speedZ * delta * 0.003f);
		}
		if (keyDown && !keyUp && !keyLeft && !keyRight) {
			moveFromLook(0, 0, speedZ * delta * 0.003f);
		}
		if (keyLeft && !keyRight && !keyUp && !keyDown) {
			moveFromLook(-speedX * delta * 0.003f, 0, 0);
		}
		if (keyRight && !keyLeft && !keyUp && !keyDown) {
			moveFromLook(speedX * delta * 0.003f, 0, 0);
		}
	}

}
