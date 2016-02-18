package world;


import mechanics.Pos;
import mechanics.Vector;
import stats.Stats;
import util.AudioController;
import util.Images;
import window.GameFrame;
import world.item.Item;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class BlockCoin implements Item {

	private Image image1 = Images.coin_spin1;
	private Image image2 = Images.coin_spin2;
	private Image image3 = Images.coin_spin3;
	private Image image4 = Images.coin_spin4;

	private Pos originalPos;
	private Pos pos;
	private Vector vector = new Vector();
	private final int numCoins;
	private int numCoinsLeft;
	private State state = State.WAIT;
	private int flingState = 0;

	public BlockCoin(Pos pos, int numCoins) {
		this.originalPos = pos;
		this.numCoins = numCoins;
		this.numCoinsLeft = numCoins;
	}

	@Override
	public void draw(Graphics2D g2, int offset) {
		if ( state == State.FLING ) {
			g2.drawImage(getImage(), getX(offset), getY(), null);
		}
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
			state = State.WAIT;
			Stats.getInstance().gotCoin();
			World.getInstance().addPoints(100, pos.copy());
		} else {
			flingState++;
			pos.move(vector);
			vector.gravity();
		}
	}

	@Override
	public boolean ready() {
		return numCoinsLeft > 0;
	}

	@Override
	public void start(boolean big) {
		if ( numCoinsLeft > 0 && state == State.WAIT) {
			state = State.FLING;
			initVector();
			pos = originalPos.copy();
			AudioController.play("/sound/wav/coin.wav");
		}
	}

	private void initVector() {
		vector.stop();
		vector.jump();
		vector.jumpHold();
		vector.jumpHold();
	}

	@Override
	public void end() {}

	@Override
	public void bounce(boolean front) {}

	@Override
	public Rectangle2D getRect(int offset) {
		return new Rectangle2D.Double(0,0,0,0);
	}

	@Override
	public void reset() {
		numCoinsLeft = numCoins;
	}

	private int getX(int offset) {
		return pos.getX() - offset;
	}

	private int getY() {
		return pos.getY();
	}

	private enum State {
		WAIT, FLING
	}
}
