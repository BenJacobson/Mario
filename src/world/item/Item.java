package world.item;

import java.awt.*;

public interface Item {

	void draw(Graphics2D g2, int offset);
	void start();
}
