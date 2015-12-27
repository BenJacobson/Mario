package main;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;

public class Ground implements Block {

	Pos pos;

	private final String imageFolder = "lib" + File.separator + "pic" + File.separator;
	private final Image groundImage = GameCanvas.initFrame(imageFolder + "block_ground.png");

	public Ground(Pos pos) {
		this.pos = pos;
	}

	@Override
	public void draw(Graphics2D g2, int offset) {
		g2.drawImage(groundImage, pos.getX()-offset, pos.getY(), groundImage.getWidth(null), groundImage.getHeight(null), null);
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
		return groundImage.getWidth(null);
	}

	@Override
	public int getHeight() {
		return groundImage.getHeight(null);
	}

	@Override
	public Pos getCenter(int offset) {
		return pos.copy(-offset + getWidth()/2, getHeight()/2);
	}

	@Override
	public Rectangle2D getRect(int offset) {
		return new Rectangle2D.Double(getX(offset), getY(), getWidth(), getHeight());
	}
}
