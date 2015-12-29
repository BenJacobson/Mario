package world;

import enemy.Enemy;
import main.*;
import mechanics.Vector;
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

	public void draw(Graphics2D g2) {
		for ( Block block : blocks ) {
			if ( block.getX(offset) > -100 && block.getX(offset) < 256*MarioNes.PIXEL_SCALE ) {
				block.draw(g2, offset);
			}
		}
		for ( Enemy enemy : enemies ) {
			if ( enemy.getX(offset) > -100 && enemy.getX(offset) < 256*MarioNes.PIXEL_SCALE ) {
				enemy.draw(g2, offset);
			}
		}
	}

	public CollisionResult collision(Rectangle2D inputRect, Vector vector) {
		return collisionOperator.collision(blocks, inputRect, vector, offset);
	}
}
