package world.enemy;

import mario.Mario;
import util.AudioController;
import util.Images;
import util.mechanics.Pos;
import util.mechanics.Vector;
import window.GameFrame;
import world.World;
import world.collision.CollisionResult;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Koopa extends GenericEnemy {

	private Image[] frames = { Images.koopa_down, Images.koopa_up, Images.koopa_down_back, Images.koopa_up_back,
			Images.koopa_shell, Images.koopa_shell_legs, Images.koopa_flipped };

	private boolean forward = false;
	private int numberOfPasses = 0;
	private boolean up = true;
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
			case FLIPPED:
				g2.drawImage(frames[6], getX(offset), getY(), null);
				break;
		}
	}

	public boolean isStopped() {
		return state == State.STOP;
	}

	private void update() {
		if (Mario.getInstance().isPaused() ) {
			return;
		}
		vector.gravity();
		pos.move(vector);
		checkCollision();
		updateFrame();

		if ( state == State.FLIPPED ) {
			if ( this.getY() > GameFrame.gameHeight() + GameFrame.blockDimension() ) {
				state = State.DEAD;
			}
		}
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
	public Rectangle2D getRect(int offset) {
		if ( state == State.DEAD || state == State.FLIPPED ) {
			rect.setRect(0,0,0,0);
		} else {
			rect.setRect(getX(offset), getY(), blockDimension, blockDimension);
		}
		return rect;
	}

	@Override
	public void hit(boolean leftHit) {
		if ( state == State.WALK || state == State.SLIDE ) {
			state = State.STOP;
			vector.hitX();
		} else if ( state == State.STOP ) {
			state = State.SLIDE;
			speedUp(25, leftHit);
		}
		AudioController.play("/sound/stomp_enemy.wav");
	}

	@Override
	public void reverse() {
		super.reverse();
		if ( state == State.SLIDE ) {
			AudioController.play("/sound/kick.wav");
		}
	}

	@Override
	public void flip() {
		state = State.FLIPPED;
		vector.jump();
		boolean movingLeft = vector.getDx() < 0;
		speedUp(8, movingLeft);
		AudioController.play("/sound/kick.wav");
		World.getInstance().addPoints(100, pos.copy());
	}

	@Override
	public void reset() {
		state = State.WALK;
		forward = false;
		this.pos = originalPos.copy();
		vector.stop();
		speedUp(3, !forward);
	}

	@Override
	public boolean isDeadly() {
		return state == State.SLIDE;
	}

	private enum State {
		WALK, STOP, SLIDE, LEGS, FLIPPED, DEAD
	}
}
