package world;

import mechanics.Pos;
import util.Images;
import window.GameFrame;

import java.awt.*;

public class Flagpole {

	Pos pos;
	Image pole = Images.flagpole_pole;
	Image ball = Images.flagpole_ball;
	Image flag = Images.flagpole_flag;

	public Flagpole(Pos pos) {
		this.pos = pos;
	}

	private int getX(int offset) {
		return pos.getX()-offset;
	}

	private int getY() {
		return pos.getY();
	}

	public void draw(Graphics2D g2, int offset) {

		g2.drawImage(ball, getX(offset), getY() - GameFrame.blockDimension(), null);

		for ( int i = 0; i < 9; i++ ) {
			g2.drawImage(pole, getX(offset), getY() + GameFrame.blockDimension()*i, null);
		}

		g2.drawImage(flag, getX(offset) - GameFrame.blockDimension(), getY(), null);
	}
}
