package world.item;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public interface Item {

	void draw(Graphics2D g2, int offset);
	boolean ready();
	void start();
	void end();
	void bounce(boolean front);

	Rectangle2D getRect(int offset);

	void reset();
}
