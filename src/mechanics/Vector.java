package mechanics;

import window.GameFrame;

public class Vector {

	private final double genSpeed = .166667 * GameFrame.pixelScale();
	private final double fast = 18;
	private final double slow = 12;

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
		dy = -genSpeed*33;
	}

	public void bounce() {
		dy = -genSpeed*20;
	}

	public void jumpHold() {
		dy -= genSpeed*3.75;
	}

	public void gravity() {
		if ( dy < genSpeed*24) {
			dy += genSpeed*3;
		}
	}

	public void moveRight(boolean speedy) {
		if ( dx < genSpeed*getSpeed(speedy) ) {
			dx += genSpeed;
		} else {
			reduceSpeed();
		}
	}

	public void moveLeft(boolean speedy) {
		if ( dx > -genSpeed*getSpeed(speedy) ) {
			dx -= genSpeed;
		} else {
			reduceSpeed();
		}
	}

	private double getSpeed(boolean speedy) {
		return speedy ? fast : slow;
	}

	public boolean isFast() {
		return Math.abs(dx) > genSpeed*(slow+1);
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

	public void reverse() {
		dx = -dx;
	}

	@Override
	public String toString() {
		return String.format("dx:%f dy:%f", dx, dy);
	}
}
