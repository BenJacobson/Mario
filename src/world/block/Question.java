package world.block;

import mario.Mario;
import mechanics.Pos;
import util.AudioController;
import util.FlashState;
import util.Images;
import window.GameFrame;
import world.BlockCoin;
import world.World;
import world.item.Item;
import world.item.PowerUp;

import java.awt.*;

public class Question extends Block {

	private State state = State.NORMAL;
	private int bounceState = -1;
	private Image usedImage = Images.used;
	private Image darkImage = Images.question_dark;
	private Image brownImage = Images.question_brown;

	public Question(Pos pos) {
		super(pos);
		image = Images.question_normal;
	}

	@Override
	public void draw(Graphics2D g2, int offset) {
		g2.drawImage( (state != State.USED ? getFlashImage() : usedImage), getX(offset), getBounceY(getY()), null);
	}

	private Image getFlashImage() {
		switch (FlashState.getFlashState()) {
			case TWO:
			case FOUR:
				return brownImage;
			case THREE:
				return darkImage;
			default:
				return image;
		}
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
				if (item instanceof PowerUp )doItem();
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
		AudioController.play("/sound/block_bump.wav");
		if ( state != State.USED ) {
			if ( item instanceof BlockCoin ) doItem();
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
