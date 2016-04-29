package world.background;

import util.mechanics.Pos;

import java.awt.*;

public class Background implements Backgrounds {

	private Image image;
	private Pos pos;

	public Background(Image image, Pos pos) {
		this.image = image;
		this.pos = pos;
	}

	@Override
	public int getX(int offset) {
		return pos.getX()-offset;
	}

	@Override
	public int getY() {
		return pos.getY();
	}

	@Override
	public void draw(Graphics2D g2, int offset) {
		g2.drawImage(image, getX(offset), getY(), null);
	}
}
