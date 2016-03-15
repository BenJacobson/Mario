package mario;

import mechanics.Pos;
import util.Images;
import world.World;

import java.awt.*;

class Fireball {

	private Image image = Images.fireball;
	Pos pos;

	Fireball(Pos pos) {
		this.pos = pos;
	}

	public void draw(Graphics2D g2, int offset) {
		update();
		g2.drawImage(image, pos.getX() - offset, pos.getY(), null);
	}

	private void update() {

	}

}
