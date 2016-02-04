package world.block;

import mechanics.Pos;
import util.Images;
import window.GameFrame;
import world.World;
import world.item.Item;

import java.awt.*;

public class Question extends Block {

	private State state = State.NORMAL;
	private int bounceState = 0;

	public Question(Pos pos) {
		super(pos);
		image = Images.question;
	}

	@Override
	public void draw(Graphics2D g2, int offset) {
		g2.drawImage(image, getX(offset), getBounceY(getY()), null);
	}

	private int getBounceY(int y) {

		if ( state == State.BOUNCE ) {

			if ( bounceState < 2 ) {
				y -= 2 * GameFrame.pixelScale();
			} else if ( bounceState < 4 ) {
				y -= 4 * GameFrame.pixelScale();
			} else if ( bounceState < 6 ) {
				y -= 6 * GameFrame.pixelScale();
			} else if ( bounceState < 7 ) {
				y -= 4 * GameFrame.pixelScale();
			} else if ( bounceState < 8 ) {
				y -= 2 * GameFrame.pixelScale();
			} else if ( bounceState < 9 ) {
				// do nothing
			} else if ( bounceState < 10 ) {
				y += 2 * GameFrame.pixelScale();
			} else {
				state = State.NORMAL;
				if (items != null) doItem();
			}

			bounceState++;
		}

		return y;
	}

	@Override
	public void hit() {
		state = State.BOUNCE;
		bounceState = 0;
		int offset = World.getInstance().getOffest();
		World.getInstance().findEnemyDeadByBlock(this.getRect(offset));
	}

	private void doItem() {
		for ( Item item : items ) {
			if ( item.ready() ) {
				item.start();
				return;
			}
		}
	}

	private enum State {
		NORMAL, BOUNCE
	}
}
