package world.item;


import mechanics.Pos;
import mechanics.Vector;
import util.Images;
import window.GameFrame;
import world.World;
import world.collision.CollisionResult;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Mushroom implements Item {

	Pos originalPos;
	Pos pos;
	Vector vector = new Vector();
	Image image;
	State state = State.WAIT;
	int riseState;
	int riseFrames = 40;

	public Mushroom(Pos pos) {
		this.pos = pos;
		this.originalPos = pos.copy();
		this.image = Images.mushroom;
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
			g2.drawImage(image, getX(offset), getY(), null);
		}

	}

	private void update() {
		if ( state == State.RISE ) {
			pos.move(vector);
			if ( riseState++ > riseFrames ) {
				state = State.NORMAL;
				vector.hitY();
				for ( int i = 0; i < 6; i++ ) {
					vector.moveRight(false);
				}
			}
		} else if ( state == State.NORMAL ) {
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
			return new Rectangle2D.Double(pos.getX() - offset, pos.getY(), image.getWidth(null), image.getHeight(null));
		} else {
			return new Rectangle2D.Double(0,0,0,0);
		}
	}

	@Override
	public void start() {
		state = State.RISE;
	}

	@Override
	public void end() {
		state = State.EATEN;
	}

	public void reverse() {
		vector.reverse();
	}

	private enum State {
		WAIT, RISE, NORMAL, EATEN
	}
}
