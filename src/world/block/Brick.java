package world.block;


import main.MarioNes;
import mechanics.Pos;

import java.awt.*;

public class Brick extends GeneralBlock {

	private State state = State.NORMAL;
	private int bounceState = 0;

	public Brick(Pos pos) {
		super(pos, "block_brick.png");
	}

	@Override
	public void draw(Graphics2D g2, int offset) {
		g2.drawImage(image, getX(offset), getBounceY(getY()), null);
	}

	private int getBounceY(int y) {

		if ( state == State.BOUNCE ) {
			
			if ( bounceState < 2 ) {
				y -= 2 * MarioNes.PIXEL_SCALE;
			} else if ( bounceState < 4 ) {
				y -= 4 * MarioNes.PIXEL_SCALE;
			} else if ( bounceState < 6 ) {
				y -= 6 * MarioNes.PIXEL_SCALE;
			} else if ( bounceState < 7 ) {
				y -= 4 * MarioNes.PIXEL_SCALE;
			} else if ( bounceState < 8 ) {
				y -= 2 * MarioNes.PIXEL_SCALE;
			} else if ( bounceState < 9 ) {
				// do nothing
			} else if ( bounceState < 10 ) {
				y += 2 * MarioNes.PIXEL_SCALE;
			} else {
				state = State.NORMAL;
			}

			bounceState++;
		}

		return y;
	}

	@Override
	public void hit() {
		state = State.BOUNCE;
		bounceState = 0;
	}

	private enum State {
		NORMAL, BOUNCE
	}
}
