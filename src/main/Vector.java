package main;

public class Vector {

	private double genSpeed = .5;

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
		if ( dx >= genSpeed*2) {
			dx -= genSpeed*2;
		} else if ( dx <= -genSpeed*2) {
			dx += genSpeed*2;
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
