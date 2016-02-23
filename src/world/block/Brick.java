package world.block;


import mechanics.Pos;
import mechanics.Vector;
import util.AudioController;
import util.Images;
import window.GameFrame;
import world.World;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Brick extends Block {

	private BlockState blockState = BlockState.NORMAL;
	private ImageState imageState = ImageState.NORMAL;
	private int bounceState = 0;
	private int breakState = 0;
	private BrokenPositions brokenPositions = new BrokenPositions();
	private Image brokenImage, usedImage;

	public Brick(Pos pos) {
		super(pos);
		image = Images.brick;
		brokenImage = image.getScaledInstance(image.getWidth(null)/3, image.getHeight(null)/3, 0);
		usedImage = Images.used;
	}

	@Override
	public void draw(Graphics2D g2, int offset) {
		if ( imageState == ImageState.USED ) {
			g2.drawImage(usedImage, getX(offset), getBounceY(getY()), null);
		} else if ( blockState == BlockState.BREAK ) {
			for ( Pos broken :  brokenPositions.get() ) {
				g2.drawImage(brokenImage, broken.getX() - offset, broken.getY(), null);
			}
		} else if ( !(blockState == BlockState.GONE) ) {
			g2.drawImage(image, getX(offset), getBounceY(getY()), null);
		}
	}

	private int getBounceY(int y) {

		if ( blockState == BlockState.BOUNCE ) {
			
			if ( bounceState < 2 ) {
				y -= 2 * GameFrame.pixelScale();
			} else if ( bounceState < 4 ) {
				y -= 4 * GameFrame.pixelScale();
			} else if ( bounceState < 6 ) {
				y -= 6 * GameFrame.pixelScale();
			} else if ( bounceState < 7 ) {
				y -= 4 * GameFrame.pixelScale();
			} else if ( bounceState < 8 ) {
				y -= 2 * GameFrame.pixelScale();
			} else if ( bounceState < 9 ) {
				// do nothing
			} else if ( bounceState < 10 ) {
				y += 2 * GameFrame.pixelScale();
			} else {
				blockState = BlockState.NORMAL;
			}

			bounceState++;
		}

		return y;
	}

	@Override
	public Rectangle2D getRect(int offset) {
		return blockState == BlockState.GONE || blockState == BlockState.BREAK ? new Rectangle2D.Double(0,0,0,0) : super.getRect(offset);
	}

	@Override
	public void hit(boolean big) {
		if ( blockState == BlockState.GONE ) {
			return;
		} else if ( imageState == ImageState.USED ) {
			AudioController.play("/sound/block_bump.wav");
		} else if ( item != null ) {
			// if the block has more items, do it
			if ( item.ready() ) {
				item.start(big);
				blockState = BlockState.BOUNCE;
			}
			// if there are no more items now, the block goes blank
			if ( !item.ready() ) {
				imageState = ImageState.USED;
			}
			bounceState = 0;
			AudioController.play("/sound/block_bump.wav");
		} else if ( big ) {
			blockState = BlockState.BREAK;
			breakState = 0;
			brokenPositions.set(pos);
			AudioController.play("/sound/break_block.wav");
		} else {
			blockState = BlockState.BOUNCE;
			bounceState = 0;
			AudioController.play("/sound/block_bump.wav");
		}
		int offset = World.getInstance().getOffest();
		World.getInstance().findEnemyDeadByBlock(this.getRect(offset));
		World.getInstance().findItemHitByBlock(this.getRect(offset));
	}

	@Override
	public void reset() {
		blockState = BlockState.NORMAL;
		imageState = ImageState.NORMAL;
		bounceState = 0;
		breakState = 0;
		if ( item != null ) item.reset();
	}

	private enum BlockState {
		NORMAL, BOUNCE, BREAK, GONE
	}

	private enum ImageState {
		NORMAL, USED
	}

	private class BrokenPositions {

		Pos part1, part2, part3, part4;
		Vector vector1 = new Vector(), vector2 = new Vector(), vector3 = new Vector(), vector4 = new Vector();
		double dx = 4;
		double dy = -15;

		public void set(Pos startPos) {
			part1 = startPos.copy();
			part2 = startPos.copy();
			part3 = startPos.copy();
			part4 = startPos.copy();

			vector1.set(dx, dy);
			vector2.set(dx, 2*dy);
			vector3.set(-dx, 2*dy);
			vector4.set(-dx, dy);
		}

		private void update() {
			vector1.gravity();
			vector2.gravity();
			vector3.gravity();
			vector4.gravity();

			part1.move(vector1);
			part2.move(vector2);
			part3.move(vector3);
			part4.move(vector4);

			if ( breakState++ > 100 ) {
				blockState = BlockState.GONE;
			}
		}

		public Pos[] get() {
			update();
			return new Pos[] {part1, part2, part3, part4};
		}
	}
}
