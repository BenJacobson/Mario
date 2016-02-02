package world;

import world.enemy.Enemy;
import mechanics.Pos;
import mechanics.Side;
import mechanics.Vector;
import stats.Stats;
import util.Maps;
import window.GameFrame;
import world.block.Block;
import world.collision.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

	private List<Block> blocks = Maps.blocks;
	private List<Enemy> enemies = Maps.enemies;
	private List<Points> points = new ArrayList<>();

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
		enemies.forEach( enemy -> enemy.reset() );
	}

	public void draw(Graphics2D g2) {

		findEnemyEnemyCollisions();

		points.stream().forEach( p -> p.draw(g2, offset) );
		points = points.stream().filter( p -> p.getState() < 50 ).collect(Collectors.toList());

		blocks.stream()
				.filter( block -> block.getX(offset) > -GameFrame.gameWidth() && block.getX(offset) < GameFrame.gameWidth()*2 )
				.forEach( block -> block.draw(g2, offset) );

		enemies.stream()
				.filter( enemy -> enemy.getX(offset) > -GameFrame.gameWidth()/2 && enemy.getX(offset) < GameFrame.gameWidth()*1.5 )
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

		boolean marioHit = false, enemyHit = false, alreadyHitEnemy = false;

		for ( Enemy enemy : enemies ) {
			if ( enemy.getRect(offset).intersects(marioRect) ) {
				Pos marioPos = new Pos(marioRect.getCenterX(), marioRect.getCenterY());
				Pos enemyPos = new Pos(enemy.getRect(offset).getCenterX(), enemy.getRect(offset).getCenterY());
				Side sideHit = getSide(marioPos, enemyPos);

				if ( sideHit == Side.TOP ) {
					if ( !alreadyHitEnemy ) {
						enemy.hit();
						alreadyHitEnemy = true;
					}
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

	public void addPoints(int amount, Pos pos) {
		Stats.getInstance().addPoints(amount);
		points.add(new Points(amount, pos));
	}

	public void enemyDeadByBlock(Rectangle2D blockRect) {
		Rectangle2D deadRect = new Rectangle2D.Double(blockRect.getX(), blockRect.getY()-GameFrame.blockDimension(),
				blockRect.getWidth(), blockRect.getHeight());

		enemies.stream().filter( enemy -> enemy.getRect(offset).intersects(deadRect) )
				.forEach( enemy -> enemy.blockHit() );
	}
}
