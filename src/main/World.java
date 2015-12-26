package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

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

		char[][] map = getMap();

		for ( int y = 0; y < map.length; y++ ) {
			for ( int x = 0; x < map[y].length; x++ ) {
				if ( map[y][x] == 'g' ) {
					ground.add(new Pos(x * groundImage.getWidth(null), (205 - y * 16) * MarioNes.PIXEL_SCALE));
				}
			}
		}

		return ground;
	}

	private char[][] getMap() {

		String fileName = "lib" + File.separator + "world" + File.separator + "1-1.dat";
		Scanner mapScan = null;

		try {
			mapScan = new Scanner(new BufferedInputStream(new FileInputStream(new File(fileName))));
		} catch (IOException ex) {
			System.out.println("Cannot load map 1-1");
			System.exit(0);
		}

		List<char[]> linesList = new LinkedList<>();

		while ( mapScan.hasNextLine() ) {
			char[] line = mapScan.nextLine().toCharArray();
			linesList.add(line);
		}

		char[][] map = new char[linesList.size()][];
		int index = linesList.size() - 1;

		for ( char[] line : linesList ) {
			map[index] = line;
			index--;
		}

		return map;
	}

	public void addOffset(int add) {
		offset += add;
	}

	public void resetOffset() {
		offset = 0;
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
			if ( block.getX() > -100 && block.getX() < 256*MarioNes.PIXEL_SCALE ) {
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

		// prevent wall standing
		if ( topHit != null && topHit.size() > 0 && leftHit != null && leftHit.size() > 0 ) {
			for (Pos block : leftHit ) {
				topHit.removeIf(p -> p.getX() == block.getX() && p.getY() > block.getY() );
			}
		}

		if ( topHit != null && topHit.size() > 0 && rightHit != null && rightHit.size() > 0 ) {
			for (Pos block : rightHit ) {
				topHit.removeIf(p -> p.getX() == block.getX() && p.getY() > block.getY() );
			}
		}

		if ( bottomHit != null && bottomHit.size() > 0 && leftHit != null && leftHit.size() > 0 ) {
			for (Pos block : leftHit ) {
				bottomHit.removeIf(p -> p.getX() == block.getX() && p.getY() < block.getY() );
			}
		}

		if ( bottomHit != null && bottomHit.size() > 0 && rightHit != null && rightHit.size() > 0 ) {
			for (Pos block : rightHit ) {
				bottomHit.removeIf(p -> p.getX() == block.getX() && p.getY() < block.getY() );
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
