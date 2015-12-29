package world.collision;

public class CollisionResult {

	private boolean leftHit = false;
	private boolean rightHit = false;
	private boolean topHit = false;
	private boolean bottomHit = false;
	private double dy;
	private double dx;

	public boolean isLeftHit() {
		return leftHit;
	}

	public void setLeftHit(boolean leftHit) {
		this.leftHit = leftHit;
	}

	public boolean isRightHit() {
		return rightHit;
	}

	public void setRightHit(boolean rightHit) {
		this.rightHit = rightHit;
	}

	public boolean isTopHit() {
		return topHit;
	}

	public void setTopHit(boolean topHit) {
		this.topHit = topHit;
	}

	public boolean isBottomHit() {
		return bottomHit;
	}

	public void setBottomHit(boolean bottomHit) {
		this.bottomHit = bottomHit;
	}

	public double getDy() {
		return dy;
	}

	public void setDy(double dy) {
		this.dy = dy;
	}

	public double getDx() {
		return dx;
	}

	public void setDx(double dx) {
		this.dx = dx;
	}
}