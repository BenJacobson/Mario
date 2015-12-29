package main;

import enemy.Enemy;
import enemy.Goomba;
import mechanics.Pos;
import window.GameFrame;
import world.World;
import world.block.*;
import world.coin.Coin;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

	public static void initBlocks() {

		blocks = new LinkedList<>();
		enemies = new LinkedList<>();
		coins = new LinkedList<>();

		char[][] map = getMap();

		for ( int y = 0; y < map.length; y++ ) {
			for ( int x = 0; x < map[y].length; x++ ) {
				if ( map[y][x] == 'g' ) {
					blocks.add(new Ground(new Pos(x * World.block_width* MarioNes.PIXEL_SCALE, (212 - y * World.block_height) * MarioNes.PIXEL_SCALE)));
				} else if ( map[y][x] == 't' ) {
					blocks.add(new PipeTop(new Pos(x * World.block_width* MarioNes.PIXEL_SCALE, (212 - y * World.block_height) * MarioNes.PIXEL_SCALE)));
				} else if ( map[y][x] == 'b' ) {
					blocks.add(new PipeBottom(new Pos(x * World.block_width* MarioNes.PIXEL_SCALE, (212 - y * World.block_height) * MarioNes.PIXEL_SCALE)));
				} else if ( map[y][x] == 'i' ) {
					blocks.add(new GeneralBlock(new Pos(x * World.block_width* MarioNes.PIXEL_SCALE, (212 - y * World.block_height) * MarioNes.PIXEL_SCALE), "block_invisible.png"));
				} else if ( map[y][x] == 'r' ) {
					blocks.add(new GeneralBlock(new Pos(x * World.block_width* MarioNes.PIXEL_SCALE, (212 - y * World.block_height) * MarioNes.PIXEL_SCALE), "block_brick.png"));
				} else if ( map[y][x] == '?' ) {
					blocks.add(new GeneralBlock(new Pos(x * World.block_width* MarioNes.PIXEL_SCALE, (212 - y * World.block_height) * MarioNes.PIXEL_SCALE), "block_question.png"));
				} else if ( map[y][x] == 's' ) {
					blocks.add(new GeneralBlock(new Pos(x * World.block_width* MarioNes.PIXEL_SCALE, (212 - y * World.block_height) * MarioNes.PIXEL_SCALE), "block_square.png"));
				} else if ( map[y][x] == 'e' ) {
					enemies.add(new Goomba(new Pos(x * World.block_width* MarioNes.PIXEL_SCALE, (212 - y * World.block_height) * MarioNes.PIXEL_SCALE)));
				} else if ( map[y][x] == 'c' ) {
					coins.add(new Coin(new Pos(x * World.block_width* MarioNes.PIXEL_SCALE, (212 - y * World.block_height) * MarioNes.PIXEL_SCALE), "Coin.png"));
				}
			}
		}
	}
}
