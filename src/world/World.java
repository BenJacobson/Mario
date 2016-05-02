package world;

import util.map.MapBlocks;
import world.background.Backgrounds;
import world.enemy.Enemy;
import util.mechanics.Pos;
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

	private Color backgroundColor = new Color(86, 151, 255);

	private List<Block> blocks;
	private List<Enemy> enemies;
	private List<BlockCoin> coins;
	private List<Backgrounds> backgrounds;
	private List<Item> items;
	private Flagpole flagpole;

	private List<Points> points = new ArrayList<>();

	public void loadMap(String mapName) {
		MapBlocks mapBlocks = MapLoader.loadMap(mapName);
		blocks = mapBlocks.getBlocks();
		enemies = mapBlocks.getEnemies();
		coins = mapBlocks.getCoins();
		backgrounds = mapBlocks.getBackgrounds();
		items = mapBlocks.getItems();
		flagpole = mapBlocks.getFlagpole();
	}

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

	private void resetOffset() {
		if ( madeHalfway ) {
			offset = halfway;
		} else {
			offset = 0;
		}
	}

	private void resetEnemies() {
		enemies.forEach(Enemy::reset);
	}

	private void resetItems() {
		items.forEach(Item::reset);
	}

	private void resetBlocks() {
		blocks.forEach(Block::reset);
	}

	public void draw(Graphics2D g2) {

		drawBackground(g2);

		drawStats(g2);

		findEnemyEnemyCollisions();

		drawPoints(g2);

		drawCoins(g2);

		if ( flagpole != null ) flagpole.draw(g2, offset);

		items.stream().forEach( item -> item.draw(g2, offset));

		blocks.stream()
				.filter( block -> block.getX(offset) > -GameFrame.gameWidth() && block.getX(offset) < GameFrame.gameWidth()*2 )
				.forEach( block -> block.draw(g2, offset) );

		enemies.stream()
				.filter( enemy -> enemy.getX(offset) > -GameFrame.gameWidth()/2 && enemy.getX(offset) < GameFrame.gameWidth()*1.5 )
				.forEach( enemy -> enemy.draw(g2, offset) );
	}

	private void drawCoins(Graphics2D g2) {
		for ( BlockCoin coin : coins ) {
			coin.draw(g2, offset);
		}
	}

	private void drawPoints(Graphics2D g2) {
		points.stream().forEach( p -> p.draw(g2, offset) );
		points = points.stream().filter( p -> !p.isDone() ).collect(Collectors.toList());
	}

	private void drawBackground(Graphics2D g2) {
		g2.setColor(backgroundColor);
		g2.fillRect(0, 0, GameFrame.gameWidth(), GameFrame.gameHeight());

		int offset = World.getInstance().getOffest();
		for (Backgrounds background : backgrounds ) {
			background.draw(g2, offset);
		}
	}

	private void drawStats(Graphics2D g2) {
		Stats.getInstance().draw(g2);
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
			} else if ( item.getRect(offset).getMinX() < 0 || item.getRect(offset).getMaxX() > GameFrame.gameWidth() ) {
				item.end();
			}
		}

		return powerUp;
	}

	public Boolean[] findMarioEnemyCollisions(Rectangle2D marioRect, boolean falling) {

		boolean marioHit = false, enemyHit = false, alreadyHitEnemy = false;

		for ( Enemy enemy : enemies ) {
			if ( enemy.getRect(offset).intersects(marioRect) ) {
				/*
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
				*/
				if ( falling ) {
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

	public CollisionResult blockCollisions(Rectangle2D inputRect, Vector vector, boolean isMario) {
		return collisionOperator.collision(blocks, inputRect, vector, offset, isMario);
	}

	public void addPoints(int amount, Pos pos) {
		Stats.getInstance().addPoints(amount);
		points.add(new Points(amount, pos));
	}

	public void findEnemyDeadByBlock(Rectangle2D blockRect) {
		Rectangle2D deadRect = new Rectangle2D.Double(blockRect.getX(), blockRect.getY()-GameFrame.blockDimension(),
				blockRect.getWidth(), blockRect.getHeight());

		enemies.stream().filter( enemy -> enemy.getRect(offset).intersects(deadRect) )
				.forEach(Enemy::flip);
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
