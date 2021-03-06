package util.mechanics;

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

	public void setDy(double dy) {
		this.dy = dy;
	}

	public void set(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}

	public void hitX() {
		dx = 0;
	}

	public void hitY() {
		dy = 0;
	}

	public void jump() {
		jump(1.0);
	}

	public void jump(double factor) {
		dy = -genSpeed*33*factor;
	}

	public void jumpHold() {
		dy -= genSpeed*3.75;
	}

	public void gravity() {
		gravity(1.0);
	}

	public void gravity(double factor) {
		if ( dy < genSpeed*24) {
			dy += genSpeed*3*factor;
		}
	}

	public void gravityNoTerminalVelocity() {
		dy += genSpeed*3;
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

	public double getFast() {
		double curDX = Math.abs(dx);
		double maxSpeed = genSpeed*getSpeed(true);
		return curDX / maxSpeed;
	}

	public void reduceSpeed() {
		if ( dx >= genSpeed) {
			dx -= genSpeed/1.5;
		} else if ( dx <= -genSpeed) {
			dx += genSpeed/1.5;
		} else {
			dx = 0;
		}
	}

	public void reverse() {
		dx = -dx;
	}

	public void stop() {
		hitX();
		hitY();
	}

	@Override
	public String toString() {
		return String.format("dx:%f dy:%f", dx, dy);
	}
}
