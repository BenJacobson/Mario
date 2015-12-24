package main;

public class Vector {

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
		dy = -20;
	}

	public void moveRight() {
		if ( dx < 8 ) {
			dx += 1;
		}
	}

	public void moveLeft() {
		if ( dx > -8 ) {
			dx -= 1;
		}
	}

	public void reduceSpeed() {
		if ( dx >= 1) {
			dx -= 1;
		} else if ( dx <= -1) {
			dx += 1;
		} else {
			dx = 0;
		}
	}

	public void gravity() {
		if ( dy < 12 ) {
			dy += 1;
		}
	}

	@Override
	public String toString() {
		return String.format("dx:%f dy:%f", dx, dy);
	}
}
