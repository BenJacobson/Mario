package world.enemy;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public interface Enemy {

	void draw(Graphics2D g2, int offset);

	int getX(int offset);
	int getY();
	Rectangle2D getRect(int offset);

	void reverse();
	void hit();
	void blockHit();
	void reset();
}
