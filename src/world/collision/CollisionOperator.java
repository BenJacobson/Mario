package world.collision;

import mario.Mario;
import util.mechanics.Pos;
import util.mechanics.Side;
import util.mechanics.Vector;
import window.GameFrame;
import world.block.Block;

import java.awt.geom.Rectangle2D;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class CollisionOperator {

	private Side getSide(Rectangle2D shape, Rectangle2D block, Vector vector) {

		int upDown = (int)(shape.getCenterY() - block.getCenterY());
		int leftRight = (int)(shape.getCenterX() - block.getCenterX());

		// When mario (or anything) is big his center is further from his top and bottom, this will account for that
		double heightBias = shape.getHeight()/GameFrame.blockDimension();
		// Favor top allows mario and enemies to run around smoothly of the top of blocks without hitting top corners
		int favorTop = (int)(5*GameFrame.pixelScale()/heightBias);
		// Disfavor bottom allows mario to jump from below around a block directly above him easier (not hit his head)
		int disfavorBottom = (int)(3*GameFrame.pixelScale()*heightBias);

		// Comparing with mario's vector will make sure he does not stand on walls
		// and allow him to jump up smoothly against walls
		if ( Math.abs(upDown)+favorTop >= Math.abs(leftRight) && upDown < 0 && vector.getDy() > 0 ) {
			return Side.TOP;
		} else if (Math.abs(upDown)-disfavorBottom >= Math.abs(leftRight) && upDown > 0 && vector.getDy() < 0) {
			return Side.BOTTOM;
		} else if ( leftRight > 0 ) {
			return Side.RIGHT;
		} else if ( leftRight < 0 ) {
			return Side.LEFT;
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
			int blockX = block.getX(offset);
			int gameWidth = GameFrame.gameWidth();
			if ( blockX > -gameWidth && blockX < gameWidth*2 ) {
				if ( inputRect.intersects( block.getRect(offset) ) ) {

					switch ( getSide(inputRect, block.getRect(offset), vector) ) {
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

			int inputCenter = (int) inputRect.getCenterX();
			final Comparator<Block> comparator = (b1, b2) -> ( Integer.compare(Math.abs(b1.getCenter(offset).getX()-inputCenter), Math.abs(b2.getCenter(offset).getX()-inputCenter)));
			bottomHit.stream().min(comparator).get().hit(Mario.getInstance().isBig());
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
