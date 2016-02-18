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
				Block block;
				Item item;
				long start = System.currentTimeMillis();

				switch (c) {
					case 'g':
						blocks.add(new Ground(new Pos(xcoord, ycoord)));
						break;
					case 't':
						blocks.add(new PipeTop(new Pos(xcoord, ycoord)));
						break;
					case 'b':
						blocks.add(new PipeBottom(new Pos(xcoord, ycoord)));
						break;
					case 'i':
						blocks.add(new Invisible(new Pos(xcoord, ycoord)));
						break;
					case 'r':
						blocks.add(new Brick(new Pos(xcoord, ycoord)));
						break;
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						int numCoins = c-48;
						block = new Brick(new Pos(xcoord, ycoord));
						item = new BlockCoin(new Pos(xcoord, ycoord), numCoins);
						block.setItem(item);
						items.add(item);
						blocks.add(block);
						break;
					case '?':
						block = new Question(new Pos(xcoord, ycoord));
						item = new BlockCoin(new Pos(xcoord, ycoord), 1);
						block.setItem(item);
						items.add(item);
						blocks.add(block);
						break;
					case 's':
						blocks.add(new Square(new Pos(xcoord, ycoord)));
						break;
					case 'e':
						enemies.add(new Goomba(new Pos(xcoord, ycoord)));
						break;
					case 'c':
						// coins.add();
						break;
					case 'l':
						backgrounds.add(new Background(Images.hill_large, new Pos(xcoord, ycoord)));
						break;
					case 'h':
						backgrounds.add(new Background(Images.hill_small, new Pos(xcoord, ycoord)));
						break;
					case 'm':
						block = new Question(new Pos(xcoord, ycoord));
						item = new PowerUp(new Pos(xcoord, ycoord));
						block.setItem(item);
						items.add(item);
						blocks.add(block);
						break;
					case 'f':
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

		Maps.blocks = blocks;
		Maps.enemies = enemies;
		Maps.coins = coins;
		Maps.backgrounds = backgrounds;
		Maps.items = items;
	}
}
