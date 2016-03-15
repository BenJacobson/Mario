package mario;

import mechanics.Pos;
import mechanics.Vector;
import util.Images;

import java.awt.*;

class Fireball {

	private Image image = Images.fireball;
	private Pos pos;
	private Vector vector = new Vector();

	Fireball(Pos pos) {
		this.pos = pos;
		for ( int i = 0; i < 3; i++ ) {
			vector.moveRight(false);
		}
	}

	public void draw(Graphics2D g2, int offset) {
		update();
		g2.drawImage(image, pos.getX() - offset, pos.getY(), null);
	}

	private void update() {
		pos.move(vector);
	}

}
