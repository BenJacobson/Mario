package world.block;

import util.mechanics.Pos;
import util.AudioController;
import world.item.Item;

import java.awt.*;
import java.awt.geom.Rectangle2D;


public abstract class Block {

	protected Pos pos;
	private Rectangle2D rect = new Rectangle2D.Double();
	protected Image image;
	protected Item item = null;
	protected boolean big;

	protected Block(Pos pos) {
		this.pos = pos;
	}

	public void draw(Graphics2D g2, int offset) {
		g2.drawImage(image, pos.getX()-offset, pos.getY(), null);
	}

	public int getX(int offset) {
		return pos.getX() - offset;
	}

	public int getY() {
		return pos.getY();
	}

	public int getWidth() {
		return image.getWidth(null);
	}

	public int getHeight() {
		return image.getHeight(null);
	}

	public Pos getCenter(int offset) {
		return pos.copy(-offset + getWidth()/2, getHeight()/2);
	}

	public Rectangle2D getRect(int offset) {
		rect.setRect(getX(offset), getY(), getWidth(), getHeight());
		return rect;
	}

	public void hit(boolean big) {
		this.big = big;
		AudioController.play("/sound/bump.wav");
	}

	public void reset() {}

	public void setItem(Item item) {
		this.item = item;
	}

}
