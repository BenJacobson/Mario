package world;

import enemy.Enemy;
import main.*;
import mechanics.Pos;
import mechanics.Side;
import mechanics.Vector;
import window.GameCanvas;
import world.block.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

public class World {

	private static World instance;

	public static World getInstance() {
		if ( instance == null ) {
			instance = new World();
		}
		return instance;
	}

	private int offset = 0;
	public static final int block_width = 16;
	public static final int block_height = 16;

	private final Image pipeTopImage = GameCanvas.initFrame(GameCanvas.imageFolder + "pipe_top.png");
	private final Image pipeBottomImage = GameCanvas.initFrame(GameCanvas.imageFolder + "pipe_bottom.png");

	private List<Block> blocks = MarioNes.blocks;
	private List<Enemy> enemies = MarioNes.enemies;

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

	private Side getSide(Pos shape, Pos block, Vector vector) {

		int upDown = shape.getY() - block.getY();
		int leftRight = shape.getX() - block.getX();

		if ( Math.abs(upDown)+1 >= Math.abs(leftRight) ) {
			// top or bottom hit
			if ( upDown > 0 && vector.getDy() < 0 ) {
				return Side.BOTTOM;
			} else if ( upDown < 0 && vector.getDy() > 0 ) {
				return Side.TOP;
			}
		} else {
			// left of right hit
			if ( leftRight > 0 && vector.getDx() < 0 ) {
				return Side.RIGHT;
			} else if ( leftRight < 0 && vector.getDx() > 0 ) {
				return Side.LEFT;
			}
		}

		return Side.NONE;
	}

	public boolean collision(Pos pos, int width, int height, Vector vector) {

		Rectangle2D.Double inputRect = new Rectangle2D.Double(pos.getX(), pos.getY(), width, height);

		List<Block> topHit = null;
		List<Block> bottomHit = null;
		List<Block> leftHit = null;
		List<Block> rightHit = null;

		// find the blocks that are hit
		for ( Block block : blocks ) {
			if ( block.getX(offset) > -100 && block.getX(offset) < 256*MarioNes.PIXEL_SCALE + 500) {
				if ( inputRect.intersects( block.getRect(offset) ) ) {

					switch ( getSide(pos.copy(width/2, height/2), block.getCenter(offset), vector) ) {
						case TOP:
							if ( topHit == null ) {topHit = new LinkedList<>();}
							topHit.add(block);
							break;
						case BOTTOM:
							if ( bottomHit == null ) {bottomHit = new LinkedList<>();}
							bottomHit.add(block);
							break;
						case LEFT:
							if ( leftHit == null ) {leftHit = new LinkedList<>();}
							leftHit.add(block);
							break;
						case RIGHT:
							if ( rightHit == null ) {rightHit = new LinkedList<>();}
							rightHit.add(block);
							break;
					}
				}
			}
		}

		// prevent wall standing
		if ( topHit != null && topHit.size() > 0 && leftHit != null && leftHit.size() > 0 ) {
			for (Block block : leftHit ) {
				topHit.removeIf(p -> p.getX(offset) == block.getX(offset) && p.getY() > block.getY() );
			}
		}

		if ( topHit != null && topHit.size() > 0 && rightHit != null && rightHit.size() > 0 ) {
			for (Block block : rightHit ) {
				topHit.removeIf(p -> p.getX(offset) == block.getX(offset) && p.getY() > block.getY() );
			}
		}

		if ( bottomHit != null && bottomHit.size() > 0 && leftHit != null && leftHit.size() > 0 ) {
			for (Block block : leftHit ) {
				bottomHit.removeIf(p -> p.getX(offset) == block.getX(offset) && p.getY() < block.getY() );
			}
		}

		if ( bottomHit != null && bottomHit.size() > 0 && rightHit != null && rightHit.size() > 0 ) {
			for (Block block : rightHit ) {
				bottomHit.removeIf(p -> p.getX(offset) == block.getX(offset) && p.getY() < block.getY() );
			}
		}

		// do the collision logic of the blocks according to how they were hit
		if ( topHit != null && topHit.size() > 0 ) {
			Block block = topHit.get(0);
			pos.moveDown(block.getY() - pos.getY() - height);
			vector.hitY();
		}
		if ( bottomHit != null && bottomHit.size() > 0 ) {
			Block block = bottomHit.get(0);
			pos.moveDown(block.getY() + block.getHeight() - pos.getY());
			vector.hitY();
		}
		if ( leftHit != null && leftHit.size() > 0 ) {
			Block block = leftHit.get(0);
			pos.moveRight(block.getX(offset) - pos.getX() - width);
			vector.hitX();
		}
		if ( rightHit != null && rightHit.size() > 0 ) {
			Block block = rightHit.get(0);
			int move = block.getX(offset) + block.getWidth() - pos.getX();
			pos.moveRight(move);
			vector.hitX();
		}

		// this return tells if there were any top collisions, this is used to say if you can jump again
		return topHit != null && topHit.size() > 0;
	}
}
