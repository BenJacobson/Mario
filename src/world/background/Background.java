package world.background;

import mechanics.Pos;

import java.awt.*;

public class Background {

	private Image image;
	private Pos pos;

	public Background(Image image, Pos pos) {
		this.image = image;
		this.pos = pos;
	}

	public int getX(int offset) {
		return pos.getX()-offset;
	}

	public int getY() {
		return pos.getY();
	}

	public void draw(Graphics2D g2, int offset) {
		g2.drawImage(image, getX(offset), getY(), null);
	}
}
