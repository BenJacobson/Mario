package world.item;


import util.mechanics.Pos;
import util.mechanics.Vector;
import util.AudioController;
import util.Images;
import window.GameFrame;
import world.World;
import world.collision.CollisionResult;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class PowerUp implements Item {

	private Pos originalPos;
	private Pos pos;
	private Vector vector = new Vector();
	private Rectangle2D rect = new Rectangle2D.Double();
	private Image imageMushroom = Images.mushroom;
	private Image imageFireFlower = Images.fireflower;
	private State state = State.READY;
	private int riseState;
	private int riseFrames = 40;
	private boolean mushroom = true;

	public PowerUp(Pos pos) {
		this.pos = pos;
		this.originalPos = pos.copy();
		this.vector.setDy((double)-GameFrame.blockDimension()/riseFrames);
	}

	public int getX(int offset) {
		return pos.getX() - offset;
	}

	public int getY() {
		return pos.getY();
	}

	public void draw(Graphics2D g2, int offset) {
		update();
		if ( state == State.RISE || state == State.NORMAL ) {
			g2.drawImage((mushroom ? imageMushroom : imageFireFlower), getX(offset), getY(), null);
		}

	}

	private void update() {
		if ( state == State.RISE ) {
			pos.move(vector);
			if ( ++riseState >= riseFrames ) {
				state = State.NORMAL;
				vector.hitY();
				for ( int i = 0; i < 6; i++ ) {
					vector.moveRight(false);
				}
			}
		} else if ( state == State.NORMAL && mushroom ) {
			vector.gravity();
			pos.move(vector);
			checkCollision();
		}
	}

	private void checkCollision() {

		int offset = World.getInstance().getOffest();
		CollisionResult collisionResult = World.getInstance().blockCollisions(getRect(offset), vector);

		pos.moveDown(collisionResult.getDy());
		if ( Math.abs(collisionResult.getDx()) > 0 ) {
			pos.moveRight(collisionResult.getDx());
			reverse();
		}
	}

	public Rectangle2D getRect(int offset) {
		if ( state == State.NORMAL ) {
			rect.setRect(pos.getX() - offset, pos.getY(), imageMushroom.getWidth(null), imageMushroom.getHeight(null));
		} else {
			rect.setRect(0,0,0,0);
		}
		return rect;
	}

	@Override
	public void start(boolean big) {
		mushroom = !big;
		state = State.RISE;
		AudioController.play("/sound/powerup_appears.wav");
	}

	@Override
	public void end() {
		World.getInstance().addPoints(1000, pos.copy());
		state = State.EATEN;
		AudioController.play("/sound/powerup_eaten.wav");
	}

	@Override
	public boolean ready() {
		return state == State.READY;
	}

	public void reverse() {
		vector.reverse();
	}

	@Override
	public void bounce(boolean front) {
		if ( front && vector.getDx() > 0 ) {
			vector.reverse();
		} else if ( !front && vector.getDx() < 0 ) {
			vector.reverse();
		}
		vector.jump();
	}

	@Override
	public void reset() {
		pos.set(originalPos);
		state = State.READY;
		riseState = 0;
		vector.hitX();
		vector.setDy((double)-GameFrame.blockDimension()/riseFrames);
	}

	private enum State {
		READY, RISE, NORMAL, EATEN
	}
}
