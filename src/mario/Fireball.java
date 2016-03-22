package mario;

import mechanics.Pos;
import mechanics.Vector;
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

	private Image[] images = { Images.fireball1, Images.fireball2, Images.fireball3, Images.fireball4 };
	private State state = State.ONE;
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

		CollisionResult collisions = World.getInstance().blockCollisions(getRect(offset), vector);
		if ( collisions.isTopHit() ) {
			vector.hitY();
			vector.jump(0.6);
		}

		vector.gravity(0.7);
		pos.move(vector);

		int holdMax = 2;
		if ( holdState++ > holdMax) {
			holdState = 0;
			switch (state) {
				case ONE:
					state = State.TWO;
					break;
				case TWO:
					state = State.THREE;
					break;
				case THREE:
					state = State.FOUR;
					break;
				default:
					state = State.ONE;
			}
		}

		if ( World.getInstance().findFireEnemyCollisions(this.getRect(World.getInstance().getOffest())) ||
				collisions.isLeftHit() || collisions.isRightHit() ||
				pos.getY() > GameFrame.gameHeight() ||
				pos.getX() > World.getInstance().getOffest() + GameFrame.gameWidth() ||
				pos.getX() < World.getInstance().getOffest() ) {
			done = true;
		}
	}

	private Rectangle2D getRect(int offset) {
		return new Rectangle2D.Double(pos.getX() - offset, pos.getY(), GameFrame.blockDimension(), GameFrame.blockDimension());
	}

	public boolean isDone() {
		return done;
	}

	private enum State {
		ONE, TWO, THREE, FOUR
	}
}
