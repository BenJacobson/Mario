package world.block;

import mario.Mario;
import mechanics.Pos;
import util.AudioController;
import util.Images;
import window.GameFrame;
import world.BlockCoin;
import world.World;
import world.item.Item;

import java.awt.*;

public class Question extends Block {

	private State state = State.NORMAL;
	private int bounceState = -1;
	private Image usedImage = Images.used;

	public Question(Pos pos) {
		super(pos);
		image = Images.question;
		item = new BlockCoin(pos.copy(), 1);
	}

	@Override
	public void draw(Graphics2D g2, int offset) {
		g2.drawImage( (state != State.USED ? image : usedImage), getX(offset), getBounceY(getY()), null);
	}

	private int getBounceY(int y) {

		if ( bounceState > -1 ) {

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
				doItem();
				bounceState = -1;
				return y;
			}

			bounceState++;
		}

		return y;
	}

	@Override
	public void hit(boolean big) {
		this.big = big;
		Mario.getInstance().stopJumpSound();
		AudioController.play("/sound/wav/block_bump.wav");
		if ( state != State.USED ) {
			bounceState = 0;
			int offset = World.getInstance().getOffest();
			World.getInstance().findEnemyDeadByBlock(this.getRect(offset));
			World.getInstance().findItemHitByBlock(this.getRect(offset));
			state = State.USED;
		}
	}

	@Override
	public void reset() {
		state = State.NORMAL;
	}

	private void doItem() {
		if ( item != null && item.ready() ) {
			item.start(big);
		}
	}

	private enum State {
		NORMAL, USED
	}
}
