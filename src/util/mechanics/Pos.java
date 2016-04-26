package util.mechanics;

public class Pos {

	private double x;
	private double y;

	public Pos(double x, double y) {
		set(x,y);
	}

	public Pos set(Pos example) {
		this.x = example.getX();
		this.y = example.getY();
		return this;
	}

	public Pos set(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
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
		return new Pos(x+dx, y+dy);
	}

	@Override
	public String toString() {
		return x + " " + y;
	}
}
