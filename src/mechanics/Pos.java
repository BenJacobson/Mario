package mechanics;

public class Pos {

	private int x;
	private int y;

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

	public void moveRight(int dx) {
		this.x += dx;
	}

	public void moveDown(int dy) {
		this.y += dy;
	}

	public void move(Vector vector) {

		moveRight((int) vector.getDx());
		moveDown((int) vector.getDy());
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Pos copy() {
		return copy(0,0);
	}

	public Pos copy(int dx, int dy) {
		return new Pos(x+dx, y+dy);
	}

	@Override
	public String toString() {
		return x + " " + y;
	}
}
