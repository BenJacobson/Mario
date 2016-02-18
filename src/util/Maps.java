package util;

import world.Flagpole;
import world.background.Background;
import world.enemy.Enemy;
import world.enemy.Goomba;
import mechanics.Pos;
import window.GameFrame;
import world.BlockCoin;
import world.block.*;
import world.item.Item;
import world.item.PowerUp;

import java.io.*;
import java.util.*;

public class Maps {

	public static List<Block> blocks;
	public static List<Enemy> enemies;
	public static List<BlockCoin> coins;
	public static List<Background> backgrounds;
	public static List<Item> items;
	public static Flagpole flagpole;

	private static char[][] getMap(String mapName) {

		String mapFile = mapName + ".dat";
		String mapPath = "/map/";
		Scanner mapScan = new Scanner(new BufferedInputStream(Maps.class.getResourceAsStream(mapPath + mapFile)));

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

	public static void initBlocks(String mapName) {

		List<Block> blocks = new LinkedList<>();
		List<Enemy> enemies = new LinkedList<>();
		List<BlockCoin> coins = new LinkedList<>();
		List<Background> backgrounds = new LinkedList<>();
		List<Item> items = new LinkedList<>();

		char[][] map = getMap(mapName);

		Map<Character, List<Long>> blockBuildTimes = new HashMap<>();
		int totalSize = 0;
		long totalTime = System.currentTimeMillis();

		for ( int y = 0; y < map.length; y++ ) {
			for ( int x = 0; x < map[y].length; x++ ) {

				char c = map[y][x];
				int xcoord = x * GameFrame.blockDimension();
				int ycoord = (int)((13.3 - y)*GameFrame.blockDimension());
				long start = System.currentTimeMillis();

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
				} else if ( Integer.getInteger(String.valueOf(c),-1) != -1 ) {
					int numCoins = Integer.getInteger(String.valueOf(c), 1);
					Block block = new Brick(new Pos(xcoord, ycoord));
					Item item = new BlockCoin(new Pos(xcoord, ycoord), numCoins);
					items.add(item);
					block.setItem(item);
					blocks.add(block);
				} else if ( c == '?' ) {
					Block block = new Question(new Pos(xcoord, ycoord));
					Item item = new BlockCoin(new Pos(xcoord, ycoord), 1);
					items.add(item);
					block.setItem(item);
					blocks.add(block);
				} else if ( c == 's' ) {
					blocks.add(new Square(new Pos(xcoord, ycoord)));
				} else if ( c == 'e' ) {
					enemies.add(new Goomba(new Pos(xcoord, ycoord)));
				} else if ( c == 'c' ) {
					// coins.add();
				} else if ( c == 'l' ) {
					backgrounds.add(new Background(Images.hill_large, new Pos(xcoord, ycoord)));
				} else if ( c == 'h' ) {
					backgrounds.add(new Background(Images.hill_small, new Pos(xcoord, ycoord)));
				} else if ( c == 'm' ) {
					Block block = new Question(new Pos(xcoord, ycoord));
					Item item = new PowerUp(new Pos(xcoord, ycoord));
					items.add(item);
					block.setItem(item);
					blocks.add(block);
				} else if ( c == 'f' ) {
					flagpole = new Flagpole(new Pos(xcoord, ycoord));
				}

				long end = System.currentTimeMillis();
				List<Long> addTimes = blockBuildTimes.get(c);
				if ( addTimes != null ) {
					addTimes.add(end - start);
				} else {
					addTimes = new LinkedList<>();
					addTimes.add(end - start);
					blockBuildTimes.put(c, addTimes);
				}
				totalSize++;
			}
		}

		/*
		System.out.println("Total size: " + totalSize);

		totalTime = System.currentTimeMillis() - totalTime;
		System.out.println("Total time: " + totalTime);

		for ( Map.Entry entry : blockBuildTimes.entrySet() ) {
			double sum = 0;
			List<Long> times = (List<Long>) entry.getValue();
			for ( Long time : times ) {
				sum += time;
			}
			System.out.printf("'%c' : %f\n", entry.getKey(), sum/times.size());
		}
		*/

		Maps.blocks = blocks;
		Maps.enemies = enemies;
		Maps.coins = coins;
		Maps.backgrounds = backgrounds;
		Maps.items = items;
	}
}
