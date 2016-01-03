package main;

import enemy.Enemy;
import enemy.Goomba;
import mechanics.Pos;
import window.GameFrame;
import world.World;
import world.block.*;
import world.coin.Coin;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

// starts the main frame of game
public class MarioNes {

	public static void main(String args[]) {
		EventQueue.invokeLater( () -> new GameFrame() );
	}


	public static int PIXEL_SCALE = 4;

	public static List<Block> blocks;
	public static List<Enemy> enemies;
	public static List<Coin> coins;

	private static char[][] getMap() {

		String gameFile = "1-1.dat";
		String fileName = "lib" + File.separator + "world" + File.separator + gameFile;
		Scanner mapScan = null;

		try {
			mapScan = new Scanner(new BufferedInputStream(new FileInputStream(new File(fileName))));
		} catch (IOException ex) {
			System.out.println("Cannot load " + fileName);
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

	public static void initBlocks() {

		List<Block> blocks = new LinkedList<>();
		List<Enemy> enemies = new LinkedList<>();
		List<Coin> coins = new LinkedList<>();

		int width = World.block_width;
		int height = World.block_height;
		int scale = MarioNes.PIXEL_SCALE;

		char[][] map = getMap();

		for ( int y = 0; y < map.length; y++ ) {
			for ( int x = 0; x < map[y].length; x++ ) {

				char c = map[y][x];
				int xcoord = x * width * scale;
				int ycoord = (212 - y * height) * scale;

				if ( c == 'g' ) {
					blocks.add(new Ground(new Pos(xcoord, ycoord)));
				} else if ( c == 't' ) {
					blocks.add(new PipeTop(new Pos(xcoord, ycoord)));
				} else if ( c == 'b' ) {
					blocks.add(new PipeBottom(new Pos(xcoord, ycoord)));
				} else if ( c == 'i' ) {
					blocks.add(new Invisible(new Pos(xcoord, ycoord)));
				} else if ( c == 'r' ) {
					blocks.add(new Brick(new Pos(xcoord, ycoord)));
				} else if ( c == '?' ) {
					blocks.add(new GeneralBlock(new Pos(xcoord, ycoord), "block_question.png"));
				} else if ( c == 's' ) {
					blocks.add(new GeneralBlock(new Pos(xcoord, ycoord), "block_square.png"));
				} else if ( c == 'e' ) {
					enemies.add(new Goomba(new Pos(xcoord, ycoord)));
				} else if ( c == 'c' ) {
					coins.add(new Coin(new Pos(xcoord, ycoord)));
				}
			}
		}

		MarioNes.blocks = blocks;
		MarioNes.enemies = enemies;
		MarioNes.coins = coins;
	}
}
