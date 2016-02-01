package world;

import mechanics.Pos;

import java.awt.*;

public class Points {

	String amount;
	Pos pos;
	int state;
	Font font = new Font(Font.MONOSPACED, Font.BOLD, 28);

	public Points(int amount, Pos pos) {
		this.amount = String.valueOf(amount);
		this.pos = pos;
	}

	public void draw(Graphics2D g2, int offset) {
		g2.setFont(font);
		g2.drawString(amount, pos.getX()-offset, pos.getY()-state*4);
		state++;
	}

	public int getState() {
		return state;
	}
}
