package world.background;

import java.awt.*;

public interface Backgrounds {
	void draw(Graphics2D g2, int offset);
	int getX(int offset);
	int getY();
}
