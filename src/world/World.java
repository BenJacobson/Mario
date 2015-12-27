package world;

import main.*;
import mechanics.Pos;
import mechanics.Side;
import mechanics.Vector;
import window.GameCanvas;
import world.block.Block;
import world.block.Ground;

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
	private final int block_width = 16;
	private final int block_height = 16;

	private final String imageFolder = "lib" + File.separator + "pic" + File.separator;
	private final Image pipeTopImage = GameCanvas.initFrame(imageFolder + "pipe_top.png");
	private final Image pipeBottomImage = GameCanvas.initFrame(imageFolder + "pipe_bottom.png");

	private List<Block> blocks = initBlocks();


	private List<Block> initBlocks() {
		List<Block> block = new ArrayList<>();

		char[][] map = getMap();

		for ( int y = 0; y < map.length; y++ ) {
			for ( int x = 0; x < map[y].length; x++ ) {
				if ( map[y][x] == 'g' ) {
					block.add(new Ground(new Pos(x * block_width* MarioNes.PIXEL_SCALE, (205 - y * block_height) * MarioNes.PIXEL_SCALE)));
				} else if ( map[y][x] == 't' ) {

				}
			}
		}

		return block;
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
		for ( Block block : blocks) {
			block.draw(g2, offset);
		}
	}

	private Side getSide(Pos shape, Pos block, Vector vector) {

		int upDown = shape.getY() - block.getY();
		int leftRight = shape.getX() - block.getX();

		if ( Math.abs(upDown) >= Math.abs(leftRight) ) {
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
			if ( block.getX(offset) > -100 && block.getX(offset) < 256*MarioNes.PIXEL_SCALE ) {
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
