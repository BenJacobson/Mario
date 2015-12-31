package world.block;

import mechanics.Pos;
import window.GameCanvas;

import java.awt.*;
import java.awt.geom.Rectangle2D;


public class GeneralBlock implements Block {

	Pos pos;

	private Image image;

	public GeneralBlock(Pos pos, String imageName) {
		this.pos = pos;
		image = GameCanvas.initFrame(GameCanvas.imageFolder + imageName);
	}

	@Override
	public void draw(Graphics2D g2, int offset) {
		g2.drawImage(image, pos.getX()-offset, pos.getY(), null);
	}

	@Override
	public int getX(int offset) {
		return pos.getX() - offset;
	}

	@Override
	public int getY() {
		return pos.getY();
	}

	@Override
	public int getWidth() {
		return image.getWidth(null);
	}

	@Override
	public int getHeight() {
		return image.getHeight(null);
	}

	@Override
	public Pos getCenter(int offset) {
		return pos.copy(-offset + getWidth()/2, getHeight()/2);
	}

	@Override
	public Rectangle2D getRect(int offset) {
		return new Rectangle2D.Double(getX(offset), getY(), getWidth(), getHeight());
	}

	@Override
	public void hit() {

	}
}
