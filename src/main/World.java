package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
	private Image groundImage = initGroundImage();
	private List<Pos> ground = initGround();

	private Image initGroundImage() {
		final String filePath = "lib" + File.separator + "pic" + File.separator + "ground.png";
		try {
			Image frame = ImageIO.read(new File(filePath));
			return frame.getScaledInstance(frame.getWidth(null)*MarioNes.PIXEL_SCALE, frame.getHeight(null)*MarioNes.PIXEL_SCALE, 0);
		} catch (IOException e) {
			System.out.println("Could not load " + filePath);
			System.exit(0);
			return null;
		}
	}

	private List<Pos> initGround() {
		List<Pos> ground = new ArrayList<>();

		for ( int i = 0; i < 100; i++ ) {
			ground.add(new Pos(i*groundImage.getWidth(null), 205*MarioNes.PIXEL_SCALE));
		}
		ground.add(new Pos(5*groundImage.getWidth(null), 205*MarioNes.PIXEL_SCALE - groundImage.getHeight(null)));

		return ground;
	}

	public void addOffset(int add) {
		offset += add;
	}

	public int getOffset() {
		return offset;
	}

	public void draw(Graphics2D g2) {
		for ( Pos pos : ground ) {
			g2.drawImage(groundImage, pos.getX()-offset, pos.getY(), groundImage.getWidth(null), groundImage.getHeight(null), null);
		}
	}

	private Side getSide(Pos shape, Pos block) {

		int upDown = shape.getY() - block.getY();
		int leftRight = shape.getX() - block.getX();

		if ( Math.abs(upDown) >= Math.abs(leftRight) ) {
			if ( upDown > 0 ) {
				return Side.BOTTOM;
			} else {
				return Side.TOP;
			}
		} else {
			if ( leftRight > 0 ) {
				return Side.RIGHT;
			} else {
				return Side.LEFT;
			}
		}
	}

	public boolean collision(Pos pos, int width, int height, Vector vector) {

		Rectangle2D.Double inputRect = new Rectangle2D.Double(pos.getX(), pos.getY(), width, height);

		List<Pos> topHit = null;
		List<Pos> bottomHit = null;
		List<Pos> leftHit = null;
		List<Pos> rightHit = null;

		for ( Pos block : ground ) {
			block = block.copy(-offset, 0);
			if ( block.getX() > -100 && block.getX() < 256*MarioNes.PIXEL_SCALE/2 ) {
				if ( inputRect.intersects(block.getX(), block.getY(), groundImage.getWidth(null), groundImage.getHeight(null)) ) {

					switch ( getSide(pos.copy(width/2, height/2), block.copy(groundImage.getWidth(null)/2, groundImage.getHeight(null)/2)) ) {
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

		if ( topHit != null && topHit.size() > 0 ) {
			Pos block = topHit.get(0);
			pos.moveDown(block.getY() - pos.getY() - height);
			vector.hitY();
		}
		if ( bottomHit != null && bottomHit.size() > 0 ) {
			Pos block = bottomHit.get(0);
			pos.moveDown(block.getY() + groundImage.getHeight(null) - pos.getY());
			vector.hitY();
		}
		if ( leftHit != null && leftHit.size() > 0 ) {
			Pos block = leftHit.get(0);
			pos.moveRight(block.getX() - pos.getX() - width);
			vector.hitX();
		}
		if ( rightHit != null && rightHit.size() > 0 ) {
			Pos block = rightHit.get(0);
			int move = block.getX() + groundImage.getWidth(null) - pos.getX();
			pos.moveRight(move);
			vector.hitX();
		}

		return topHit != null && topHit.size() > 0;
	}
}
