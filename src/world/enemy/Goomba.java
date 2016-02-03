package world.enemy;


import mario.Mario;
import mechanics.Pos;
import mechanics.Vector;
import util.Images;
import window.GameFrame;
import world.World;
import world.collision.CollisionResult;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Goomba implements Enemy {

	private final Pos originalPos;
	private Pos pos;
	private Vector vector = new Vector();

	private final Image imageLeft = Images.goombaLeft;
	private final Image imageRight = Images.goombaRight;
	private final Image imageSquished = Images.goombaSquished;
	private final Image imageFlipped = Images.goombaFlipped;


	private State state = State.ALIVE;

	boolean useLeftImage;
	private int numberOfPasses = 0;
	private int drawSquished = 0;
	public Goomba(Pos pos) {
		this.originalPos = pos;
		this.pos = pos.copy();
		vector.moveLeft(false);
		vector.moveLeft(false);
		vector.moveLeft(false);
	}


	@Override
	public void draw(Graphics2D g2, int offset) {
		if ( state != State.DEAD ) {
			update();
			Image image = getStateImage();
			g2.drawImage(image, pos.getX() - offset, pos.getY(), null);
		}
	}

	private Image getStateImage() {
		switch(state) {
			case ALIVE:
				if ( useLeftImage ) {
					return imageLeft;
				} else {
					return imageRight;
				}
			case FLIPPED:
				return imageFlipped;
			default:
				return imageSquished;
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
		if ( state == State.ALIVE ) {
			return new Rectangle2D.Double(pos.getX() - offset, pos.getY(), imageLeft.getWidth(null), imageLeft.getHeight(null));
		} else {
			return new Rectangle2D.Double(0,0,0,0);
		}
	}

	@Override
	public void reverse() {
		vector.reverse();
	}

	@Override
	public void hit() {
		state = State.SQUISHED;
		drawSquished = 0;

		World.getInstance().addPoints(100,pos.copy());
	}

	@Override
	public void blockHit() {
		state = State.FLIPPED;

		vector.jump();
		if ( vector.getDx() > 0 ) {
			for ( int i = 0; i < 8; i++ ) {
				vector.moveRight(false);
			}
		} else {
			for ( int i = 0; i < 8; i++ ) {
				vector.moveLeft(false);
			}
		}

		World.getInstance().addPoints(100,pos.copy());
	}

	@Override
	public void reset() {
		state = State.ALIVE;
		this.pos = originalPos.copy();
		vector.hitX();
		vector.hitY();
		for ( int i = 0; i < 3; i++ ) {
			vector.moveLeft(false);
		}
	}

	private void update() {
		if ( (state == State.ALIVE || state == State.FLIPPED) && Mario.getInstance().isNotDead() ) {
			vector.gravity();
			pos.move(vector);
			checkCollision();
		}
		updateFrame();
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

	private void updateFrame() {
		if ( state == State.ALIVE && Mario.getInstance().isNotDead()) {
			numberOfPasses++;

			int passesBetweenFrames = 5;
			if (numberOfPasses > passesBetweenFrames) {
				numberOfPasses = 0;
				useLeftImage = !useLeftImage;
			}
		} else if ( state == State.SQUISHED ) {
			if ( drawSquished++ > 30 ) {
				state = State.DEAD;
			}
		} else if ( state == State.FLIPPED ) {
			if ( this.getY() > GameFrame.gameHeight() + GameFrame.blockDimension() ) {
				state = State.DEAD;
			}
		}
	}

	private enum State {
		ALIVE, SQUISHED, FLIPPED, DEAD
	}
}