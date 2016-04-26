package world;

import world.enemy.Enemy;
import util.mechanics.Pos;
import util.mechanics.Side;
import util.mechanics.Vector;
import stats.Stats;
import util.map.MapLoader;
import window.GameFrame;
import world.block.Block;
import world.collision.*;
import world.item.Item;

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
	private final int halfway = 1300*GameFrame.pixelScale();
	private boolean madeHalfway = false;

	private List<Block> blocks = MapLoader.blocks;
	private List<Enemy> enemies = MapLoader.enemies;
	private List<Points> points = new ArrayList<>();
	private List<Item> items = MapLoader.items;

	public int getOffest() {
		return offset;
	}

	public void addOffset(int add) {
		offset += add;

		if (offset > halfway) {
			madeHalfway = true;
		}
	}

	public void reset() {
		resetOffset();
		resetEnemies();
		resetItems();
		resetBlocks();
	}

	private void resetBlocks() {
		blocks.forEach( block -> block.reset() );
	}

	private void resetOffset() {
		if ( madeHalfway ) {
			offset = halfway;
		} else {
			offset = 0;
		}
	}

	private void resetEnemies() {
		enemies.forEach( enemy -> enemy.reset() );
	}

	private void resetItems() {
		items.forEach( item -> item.reset() );
	}

	public void draw(Graphics2D g2) {

		findEnemyEnemyCollisions();

		drawPoints(g2);

		if ( MapLoader.flagpole != null ) MapLoader.flagpole.draw(g2, offset);

		items.stream().forEach( item -> item.draw(g2, offset));

		blocks.stream()
				.filter( block -> block.getX(offset) > -GameFrame.gameWidth() && block.getX(offset) < GameFrame.gameWidth()*2 )
				.forEach( block -> block.draw(g2, offset) );

		enemies.stream()
				.filter( enemy -> enemy.getX(offset) > -GameFrame.gameWidth()/2 && enemy.getX(offset) < GameFrame.gameWidth()*1.5 )
				.forEach( enemy -> enemy.draw(g2, offset) );
	}

	private void drawPoints(Graphics2D g2) {
		points.stream().forEach( p -> p.draw(g2, offset) );
		points = points.stream().filter( p -> !p.isDone() ).collect(Collectors.toList());
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

	public boolean findMarioItemCollisions(Rectangle2D marioRect) {
		boolean powerUp = false;

		for ( Item item : items ) {
			if ( item.getRect(offset).intersects(marioRect) ) {
				item.end();
				powerUp = true;
			}
		}

		return powerUp;
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

	public void findEnemyDeadByBlock(Rectangle2D blockRect) {
		Rectangle2D deadRect = new Rectangle2D.Double(blockRect.getX(), blockRect.getY()-GameFrame.blockDimension(),
				blockRect.getWidth(), blockRect.getHeight());

		enemies.stream().filter( enemy -> enemy.getRect(offset).intersects(deadRect) )
				.forEach( enemy -> enemy.flip() );
	}

	public void findItemHitByBlock(Rectangle2D blockRect) {
		Rectangle2D hitRect = new Rectangle2D.Double(blockRect.getX(), blockRect.getY()-GameFrame.blockDimension(),
				blockRect.getWidth(), blockRect.getHeight());

		items.stream().filter( item -> item.getRect(offset).intersects(hitRect) )
				.forEach( item -> item.bounce( hitRect.getCenterX() - item.getRect(offset).getCenterX() > 0 ) );
	}

	public boolean findFireEnemyCollisions(Rectangle2D fireRect) {
		boolean hit = false;
		for ( Enemy enemy : enemies ) {
			if ( enemy.getRect(offset).intersects(fireRect) ) {
				enemy.flip();
				hit = true;
			}
		}
		return hit;
	}
}
