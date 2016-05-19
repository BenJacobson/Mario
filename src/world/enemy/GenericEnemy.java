package world.enemy;

import util.mechanics.Pos;
import util.mechanics.Vector;

import java.awt.geom.Rectangle2D;

abstract class GenericEnemy implements Enemy {

	Pos pos;
	Pos originalPos;
	Vector vector = new Vector();
	Rectangle2D rect = new Rectangle2D.Double();

	@Override
	public int getX(int offset) {
		return pos.getX() - offset;
	}

	@Override
	public int getY() {
		return pos.getY();
	}

	@Override
	public void reverse() {
		vector.reverse();
	}

	void speedUp(int times, boolean left) {
		for ( int i = 0; i < times; i++ ) {
			vector.moveRight(true);
		}
		if ( left ) {
			vector.reverse();
		}
	}

	@Override
	public boolean directionRight() {
		return vector.getDx() > 0;
	}

	@Override
	public boolean isDeadly() {
		return false;
	}

}
