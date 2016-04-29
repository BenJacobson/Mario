package mario;

import util.AudioController;
import util.mechanics.Pos;
import util.mechanics.Vector;
import util.Images;
import window.GameFrame;
import world.World;
import world.collision.CollisionResult;

import java.awt.*;
import java.awt.geom.Rectangle2D;

class Fireball {

	private Pos pos;
	private Vector vector = new Vector();
	private boolean done = false;
	private Rectangle2D rect = new Rectangle2D.Double();

	private Image[] images = { Images.fireball1, Images.fireball2, Images.fireball3, Images.fireball4,
			Images.explosion1, Images.explosion2, Images.explosion3 };
	private State state = State.FIRE1;
	private int holdState = 0;

	Fireball(Pos pos, boolean forward) {
		this.pos = pos;
		double fireballSpeed = 5.0*GameFrame.pixelScale();
		vector.set( (forward ? fireballSpeed : -fireballSpeed), 0.0);
	}

	public void draw(Graphics2D g2, int offset) {
		if ( !done ) {
			update(offset);
			g2.drawImage(images[state.ordinal()], pos.getX() - offset, pos.getY(), null);
		}
	}

	private void update(int offset) {

		// only update and move fireball if it is not already exploding
		if ( state == State.FIRE1 || state == State.FIRE2 || state == State.FIRE3 || state == State.FIRE4 ) {
			CollisionResult collisions = World.getInstance().blockCollisions(getRect(offset), vector);
			if (collisions.isTopHit()) { // bounce the fireball on the top of blocks
				vector.hitY();
				vector.jump(0.6);
			}

			vector.gravity(0.7);
			pos.move(vector);

			// possible ways for the fireball to end
			if ( World.getInstance().findFireEnemyCollisions(this.getRect(World.getInstance().getOffest())) ) { // fireball hits an enemy
				AudioController.play("/sound/kick.wav");
				holdState = 0;
				state = State.EXPLODE1;
			} else if ( collisions.isLeftHit() || collisions.isRightHit() ) { // fire ball hits a block on the side
				AudioController.play("/sound/bump.wav");
				holdState = 0;
				state = State.EXPLODE1;
			} else if ( pos.getY() > GameFrame.gameHeight() ||
					pos.getX() > World.getInstance().getOffest() + GameFrame.gameWidth() || // fireball goes off screen
					pos.getX() < World.getInstance().getOffest() ) {
				done = true;
			}
		}

		int holdMax = 1;
		if ( holdState++ > holdMax) {
			holdState = 0;
			switch (state) {
				case FIRE1:
					state = State.FIRE2;
					break;
				case FIRE2:
					state = State.FIRE3;
					break;
				case FIRE3:
					state = State.FIRE4;
					break;
				case FIRE4:
					state = State.FIRE1;
					break;
				case EXPLODE1:
					state = State.EXPLODE2;
					break;
				case EXPLODE2:
					state = State.EXPLODE3;
					break;
				case EXPLODE3:
					done = true;
					break;
				default:
					state = State.FIRE1;
			}
		}
	}

	private Rectangle2D getRect(int offset) {
		double oneFourthBlock = GameFrame.blockDimension()/4;
		rect.setRect(pos.getX()-offset+oneFourthBlock, pos.getY()+oneFourthBlock, oneFourthBlock*2, oneFourthBlock*2);
		return rect;
	}

	boolean isDone() {
		return done;
	}

	private enum State {
		FIRE1, FIRE2, FIRE3, FIRE4, EXPLODE1, EXPLODE2, EXPLODE3
	}
}
