package world;


import mechanics.Pos;
import mechanics.Vector;
import org.w3c.dom.css.Rect;
import stats.Stats;
import util.AudioController;
import util.Images;
import window.GameFrame;
import world.item.Item;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

public class BlockCoin implements Item {

	private Pos originalPos;
	private Rectangle2D rect = new Rectangle2D.Double(0,0,0,0);
	private final int numCoins;
	private int numCoinsLeft;
	private List<FlingingCoin> flingingCoins = new LinkedList<>();

	public BlockCoin(Pos pos, int numCoins) {
		this.originalPos = pos;
		this.numCoins = numCoins;
		this.numCoinsLeft = numCoins;
	}

	@Override
	public void draw(Graphics2D g2, int offset) {

		FlingingCoin doneCoin = null;

		for ( FlingingCoin coin : flingingCoins ) {
			g2.drawImage(coin.getImage(), coin.getX(offset), coin.getY(), null);
			if ( coin.done() ) {
				doneCoin = coin;
			}
		}

		if ( doneCoin != null ) {
			flingingCoins.remove(doneCoin);
		}
	}

	@Override
	public boolean ready() {
 		return numCoinsLeft > 0;
	}

	@Override
	public void start(boolean big) {
		if ( numCoinsLeft > 0 ) {
			numCoinsLeft--;
			flingingCoins.add(new FlingingCoin(originalPos.copy()));
			AudioController.play("/sound/coin.wav");
		}
	}

	@Override
	public void end() {}

	@Override
	public void bounce(boolean front) {}

	@Override
	public Rectangle2D getRect(int offset) {
		return rect;
	}

	@Override
	public void reset() {
		numCoinsLeft = numCoins;
	}

	private int getX(int offset) {
		return 0;
	}

	private int getY() {
		return 0;
	}

	private class FlingingCoin {

		private Image image1 = Images.coin_spin1;
		private Image image2 = Images.coin_spin2;
		private Image image3 = Images.coin_spin3;
		private Image image4 = Images.coin_spin4;

		private Pos pos;
		private Vector vector = new Vector();
		private int flingState = 0;
		boolean done = false;

		FlingingCoin(Pos pos) {
			this.pos = pos;
			initVector();
		}

		private Image getImage() {
			updateFlingState();
			int imageSelector = (flingState/2) % 4;
			switch (imageSelector) {
				case 0:
					return image1;
				case 1:
					return image2;
				case 2:
					return image3;
				default:
					return image4;
			}
		}

		private void updateFlingState() {
			if ( pos.getY() > originalPos.getY() - GameFrame.blockDimension() && flingState > 10 ) {
				done = true;
				Stats.getInstance().gotCoin();
				World.getInstance().addPoints(100, pos.copy());
			} else {
				flingState++;
				pos.move(vector);
				vector.gravity();
			}
		}

		private void initVector() {
			vector.stop();
			vector.jump();
			for ( int i = 0; i < 5; i++ ) {
				vector.jumpHold();
			}
		}

		private int getX(int offset) {
			return pos.getX() - offset;
		}

		private int getY() {
			return pos.getY();
		}

		public boolean done() {
			return done;
		}
	}
}
