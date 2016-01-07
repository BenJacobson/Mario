package world;

import enemy.Enemy;
import main.*;
import mechanics.Pos;
import mechanics.Side;
import mechanics.Vector;
import window.GameFrame;
import world.block.*;
import world.collision.CollisionOperator;
import world.collision.CollisionResult;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class World {

	private static World instance;

	public static World getInstance() {
		if ( instance == null ) {
			instance = new World();
		}
		return instance;
	}

	private CollisionOperator collisionOperator = new CollisionOperator();

	private int offset = 0;
	public static final int block_width = 16;
	public static final int block_height = 16;

	private List<Block> blocks = MarioNes.blocks;
	private List<Enemy> enemies = MarioNes.enemies;

	public int getOffest() {
		return offset;
	}

	public void addOffset(int add) {
		offset += add;
	}

	public void resetOffset() {
		offset = 0;
	}

	public void resetEnemies(){
		for ( Enemy enemy : enemies ) {
			enemy.reset();
		}
	}

	public void draw(Graphics2D g2) {

		findEnemyEnemyCollisions();

		blocks.stream()
				.filter( block -> block.getX(offset) > -100 && block.getX(offset) < GameFrame.UNSCALED_WIDTH*GameFrame.PIXEL_SCALE )
				.forEach( block -> block.draw(g2, offset) );

		enemies.stream()
				.filter( enemy -> enemy.getX(offset) > -100 && enemy.getX(offset) < GameFrame.UNSCALED_HEIGHT*GameFrame.PIXEL_SCALE)
				.forEach( enemy -> enemy.draw(g2, offset) );
	}

	private void findEnemyEnemyCollisions() {
		for ( int i = 0; i < enemies.size(); i++ ) {
			Enemy enemy = enemies.get(i);
			for ( int j = i+1; j < enemies.size(); j++ ) {
				Enemy other = enemies.get(j);
				if ( enemy.getRect(offset).intersects(other.getRect(offset)) ) {
					enemy.reverse();
					other.reverse();
				}
			}
		}
	}

	public Boolean[] findMarioEnemyCollisions(Rectangle2D marioRect) {

		boolean marioHit = false, enemyHit = false;

		for ( Enemy enemy : enemies ) {
			if ( enemy.getRect(offset).intersects(marioRect) ) {
				Pos marioPos = new Pos(marioRect.getCenterX(), marioRect.getCenterY());
				Pos enemyPos = new Pos(enemy.getRect(offset).getCenterX(), enemy.getRect(offset).getCenterY());
				Side sideHit = getSide(marioPos, enemyPos);

				if ( sideHit == Side.TOP ) {
					enemy.hit();
					enemyHit = true;
				} else {
					marioHit = true;
				}
			}
		}

		return new Boolean[] { marioHit, enemyHit };
	}

	private Side getSide(Pos marioPos, Pos enemyPos) {

		int upDown = marioPos.getY() - enemyPos.getY();
		int leftRight = marioPos.getX() - enemyPos.getX();

		if ( Math.abs(upDown) >= Math.abs(leftRight) ) {
			// top or bottom hit
			if ( upDown > 0 ) {
				return Side.BOTTOM;
			} else {
				return Side.TOP;
			}
		} else {
			// left of right hit
			if ( leftRight > 0 ) {
				return Side.RIGHT;
			} else {
				return Side.LEFT;
			}
		}
	}

	public CollisionResult blockCollisions(Rectangle2D inputRect, Vector vector) {
		return collisionOperator.collision(blocks, inputRect, vector, offset);
	}
}
