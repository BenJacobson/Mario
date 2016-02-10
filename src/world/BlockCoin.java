package world;


import mechanics.Pos;
import util.Images;
import window.GameCanvas;
import world.item.Item;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class BlockCoin implements Item {

	private Pos pos;
	private Image image;
	private final int numCoins;
	private int numCoinsLeft;

	public BlockCoin(Pos pos, int numCoins) {
		this.pos = pos;
		this.image = Images.coin;
		this.numCoins = numCoins;
		this.numCoinsLeft = numCoins;
	}

	@Override
	public void draw(Graphics2D g2, int offset) {

	}

	@Override
	public boolean ready() {
		return numCoinsLeft > 0;
	}

	@Override
	public void start(boolean big) {

	}

	@Override
	public void end() {

	}

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
}
