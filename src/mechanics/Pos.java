package mechanics;

public class Pos {

	private double x;
	private double y;

	public Pos(int x, int y) {
		set(x,y);
	}

	public Pos set(int x, int y) {
		this.x = x;
		this.y = y;

		return this;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void moveRight(double dx) {
		this.x += dx;
	}

	public void moveDown(double dy) {
		this.y += dy;
	}

	public void move(Vector vector) {

		moveRight(vector.getDx());
		moveDown(vector.getDy());
	}

	public int getX() {
		return (int) x;
	}

	public int getY() {
		return (int) y;
	}

	public Pos copy() {
		return copy(0,0);
	}

	public Pos copy(double dx, double dy) {
		return new Pos((int)(x+dx), (int)(y+dy));
	}

	@Override
	public String toString() {
		return x + " " + y;
	}
}
