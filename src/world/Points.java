package world;

import java.awt.*;

public class Points {

	String amount;
	int x, y;
	int state;

	public Points(int amount, int x, int y) {
		this.amount = String.valueOf(amount);
		this.x = x;
		this.y = y;
	}

	public void draw(Graphics2D g2) {
		g2.drawString(amount, x, y);

		if ( state++ > 30 ) {

		}
	}
}
