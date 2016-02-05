package world.block;

import mechanics.Pos;
import world.item.Item;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;


public class Block {

	protected Pos pos;

	protected Image image;

	protected List<Item> items = null;

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
		return new Rectangle2D.Double(getX(offset), getY(), getWidth(), getHeight());
	}

	public void hit(boolean big) {}

	public void addItem(Item item) {
		if ( items == null ) {
			items = new LinkedList<>();
		}
		items.add(item);
	}

}
