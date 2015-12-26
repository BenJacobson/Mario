package main;

public class Vector {

	private double genSpeed = .166667*MarioNes.PIXEL_SCALE;

	private double dx = 0;
	private double dy = 0;

	public double getDx() {
		return dx;
	}

	public double getDy() {
		return dy;
	}

	public void hitX() {
		dx = 0;
	}

	public void hitY() {
		dy = 0;
	}

	public void jump() {
		dy = -genSpeed*42;
	}

	public void moveRight() {
		if ( dx < genSpeed*16 ) {
			dx += genSpeed;
		}
	}

	public void moveLeft() {
		if ( dx > -genSpeed*16 ) {
			dx -= genSpeed;
		}
	}

	public void reduceSpeed() {
		if ( dx >= genSpeed) {
			dx -= genSpeed;
		} else if ( dx <= -genSpeed) {
			dx += genSpeed;
		} else {
			dx = 0;
		}
	}

	public void gravity() {
		if ( dy < genSpeed*24 ) {
			dy += genSpeed*2;
		}
	}

	@Override
	public String toString() {
		return String.format("dx:%f dy:%f", dx, dy);
	}
}
