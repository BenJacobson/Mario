package enemy;


import mario.Mario;
import mechanics.Pos;
import mechanics.Vector;
import window.GameCanvas;
import world.World;
import world.collision.CollisionResult;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Goomba implements Enemy {

	private final Pos originalPos;
	private Pos pos;
	private Vector vector = new Vector();

	private final Image imageLeft = GameCanvas.initFrame(GameCanvas.imageFolder + "enemy_goomba_left.png");
	private final Image imageRight = GameCanvas.initFrame(GameCanvas.imageFolder + "enemy_goomba_right.png");
	private final Image imageSquished = GameCanvas.initFrame(GameCanvas.imageFolder + "enemy_goomba_squished.png");

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
			Image image = state == State.ALIVE ? useLeftImage ? imageLeft : imageRight : imageSquished;
			g2.drawImage(image, pos.getX() - offset, pos.getY(), null);
		}
	}

	@Override
	public int getX(int offset) {
		return pos.getX() - offset;
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
	}

	@Override
	public void reset() {
		state = State.ALIVE;
		this.pos = originalPos.copy();
	}

	private void update() {
		if ( state == State.ALIVE && Mario.getInstance().isNotDead()) {
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
		}
	}

	private enum State {
		ALIVE, SQUISHED, DEAD
	}
}
