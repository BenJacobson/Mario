package world.collision;

import main.MarioNes;
import mechanics.Pos;
import mechanics.Side;
import mechanics.Vector;
import world.block.Block;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

public class CollisionOperator {

	public Side getSide(Pos shape, Pos block, Vector vector) {

		int upDown = shape.getY() - block.getY();
		int leftRight = shape.getX() - block.getX();

		int favorUpDown = 4*MarioNes.PIXEL_SCALE;

		if ( Math.abs(upDown)+favorUpDown >= Math.abs(leftRight) ) {
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

	public CollisionResult collision(List<Block> blocks, Rectangle2D inputRect, Vector vector, int offset) {

		List<Block> topHit = null;
		List<Block> bottomHit = null;
		List<Block> leftHit = null;
		List<Block> rightHit = null;

		// find the blocks that are hit
		for ( Block block : blocks ) {
			if ( block.getX(offset) > -100 && block.getX(offset) < 256* MarioNes.PIXEL_SCALE) {
				if ( inputRect.intersects( block.getRect(offset) ) ) {

					switch ( getSide(new Pos((int)inputRect.getCenterX(), (int)inputRect.getCenterY()), block.getCenter(offset), vector) ) {
						case TOP:
							if ( topHit == null ) {topHit = new LinkedList<>();}
							topHit.add(block);
							break;
						case BOTTOM:
							if ( bottomHit == null ) {bottomHit = new LinkedList<>();}
							bottomHit.add(block);
							block.hit();
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

		preventWallStanding(topHit, bottomHit, leftHit, rightHit, offset);

		CollisionResult result = new CollisionResult();

		if ( topHit != null && topHit.size() > 0 ) {
			result.setTopHit(true);
			Block block = topHit.get(0);
			result.setDy(block.getY() - inputRect.getY() - inputRect.getHeight());
		}
		if ( bottomHit != null && bottomHit.size() > 0 ) {
			result.setBottomHit(true);
			Block block = bottomHit.get(0);
			result.setDy(block.getY() + block.getHeight() - inputRect.getY());
			bottomHit.stream().forEach( b -> b.hit() );
		}
		if ( leftHit != null && leftHit.size() > 0 ) {
			result.setLeftHit(true);
			Block block = leftHit.get(0);
			result.setDx(block.getX(offset) - inputRect.getX() - inputRect.getWidth());
		}
		if ( rightHit != null && rightHit.size() > 0 ) {
			result.setRightHit(true);
			Block block = rightHit.get(0);
			result.setDx(block.getX(offset) + block.getWidth() - inputRect.getX());
		}

		return result;
	}

	private void preventWallStanding(List<Block> topHit, List<Block> bottomHit, List<Block> leftHit, List<Block> rightHit, int offset) {
		// prevent mario from standing on walls
		// remove blocks from top and bottom hit that are above or below blocks you are hitting on the left and right
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
	}
}
