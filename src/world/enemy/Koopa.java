package world.enemy;

import util.Images;
import util.mechanics.Pos;
import util.mechanics.Vector;
import window.GameFrame;
import world.World;
import world.collision.CollisionResult;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Koopa implements Enemy {

	private Pos originalPos;
	private Pos pos;
	private Vector vector = new Vector();
	private Image[] frames = { Images.koopa_down, Images.koopa_up, Images.koopa_down_back, Images.koopa_up_back };
	private Rectangle2D rect = new Rectangle2D.Double();

	private boolean forward = false;
	private int numberOfPasses = 0;
	private State state = State.UP;

	private final int blockDimension = GameFrame.blockDimension();

	public Koopa(Pos pos) {
		this.originalPos = pos;
		this.pos = originalPos.copy();
		for ( int i = 0; i < 3; i++ ) {
			vector.moveLeft(false);
		}
	}

	@Override
	public void draw(Graphics2D g2, int offset) {
		update();
		Image frame = getFrame();
		g2.drawImage(frame, getX(offset), getY() - blockDimension, null);
	}

	private Image getFrame() {
		int index = (forward ? 0 : 2) + (state == State.DOWN ? 0 : 1);
		return frames[index];
	}

	private void update() {
		vector.gravity();
		pos.move(vector);
		checkCollision();
		updateFrame();
	}

	private void updateFrame() {
		int passesBetweenFrames = 10;
		if (numberOfPasses++ > passesBetweenFrames) {
			numberOfPasses = 0;
			if ( state == State.UP ) {
				state = State.DOWN;
			}else {
				state = State.UP;
			}
		}
	}

	private void checkCollision() {

		int offset = World.getInstance().getOffest();
		CollisionResult collisionResult = World.getInstance().blockCollisions(getRect(offset), vector, false);

		pos.moveDown(collisionResult.getDy());
		pos.moveRight(collisionResult.getDx());
		if ( (collisionResult.isLeftHit() && vector.getDx() > 0) ||
				(collisionResult.isRightHit() && vector.getDx() < 0) ) {
			reverse();
			forward = !forward;
		}
	}

	@Override
	public int getX(int offset) {
		return pos.getX() - offset;
	}

	@Override
	public int getY() {
		return pos.getY();
	}

	@Override
	public Rectangle2D getRect(int offset) {
		rect.setRect(getX(offset), getY(), blockDimension, blockDimension);
		return rect;
	}

	@Override
	public void reverse() {
		vector.reverse();
	}

	@Override
	public void hit() {
		System.out.println("Koopa hit!");
	}

	@Override
	public void flip() {
		System.out.println("Koopa flipped!");
	}

	@Override
	public void reset() {
		state = State.UP;
		this.pos = originalPos.copy();
		vector.stop();
		for ( int i = 0; i < 3; i++ ) {
			vector.moveLeft(false);
		}
	}

	private enum State {
		UP, DOWN, STOP, SLIDE, LEGS
	}
}
