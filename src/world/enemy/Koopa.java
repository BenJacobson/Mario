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
	private Image[] frames = { Images.koopa_down, Images.koopa_up, Images.koopa_down_back, Images.koopa_up_back,
			Images.koopa_shell, Images.koopa_shell_legs };
	private Rectangle2D rect = new Rectangle2D.Double();

	private boolean forward = false;
	private int numberOfPasses = 0;
	private boolean  up = true;
	private State state = State.WALK;

	private final int blockDimension = GameFrame.blockDimension();

	public Koopa(Pos pos) {
		this.originalPos = pos;
		this.pos = originalPos.copy();
		speedUp(3, true);
	}

	@Override
	public void draw(Graphics2D g2, int offset) {
		update();
		switch(state) {
			case WALK:
				int index = (forward ? 0 : 2) + (up ? 1 : 0);
				g2.drawImage(frames[index], getX(offset), getY() - blockDimension, null);
				break;
			case STOP:
			case SLIDE:
				g2.drawImage(frames[4], getX(offset), getY(), null);
				break;
			case LEGS:
				g2.drawImage(frames[5], getX(offset), getY(), null);
				break;
		}
	}

	public boolean isStopped() {
		return state == State.STOP;
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
			up = !up;
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
	public void hit(boolean leftHit) {
		if ( state == State.WALK || state == State.SLIDE ) {
			state = State.STOP;
			vector.hitX();
		} else if ( state == State.STOP ) {
			state = State.SLIDE;
			speedUp(12, leftHit);
		}
	}

	private void speedUp(int times, boolean left) {
		for ( int i = 0; i < times; i++ ) {
			vector.moveRight(false);
		}
		if ( left ) {
			vector.reverse();
		}
	}

	@Override
	public void flip() {
		System.out.println("Koopa flipped!");
	}

	@Override
	public void reset() {
		state = State.WALK;
		forward = false;
		this.pos = originalPos.copy();
		vector.stop();
		for ( int i = 0; i < 3; i++ ) {
			vector.moveLeft(false);
		}
	}

	private enum State {
		WALK, STOP, SLIDE, LEGS
	}
}
