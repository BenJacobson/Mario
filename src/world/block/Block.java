package world.block;

import mechanics.Pos;

import java.awt.*;
import java.awt.geom.Rectangle2D;

// a general block
public interface Block {

	void draw(Graphics2D g2, int offset);

	int getX(int offset);
	int getY();
	int getWidth();
	int getHeight();

	Pos getCenter(int offset);

	Rectangle2D getRect(int offset);
}