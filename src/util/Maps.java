package util;

import enemy.Enemy;
import enemy.Goomba;
import mechanics.Pos;
import window.GameFrame;
import world.Coin;
import world.World;
import world.block.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Maps {

	public static List<Block> blocks;
	public static List<Enemy> enemies;
	public static List<Coin> coins;

	private static char[][] getMap(String mapName) {

		String gameFile = mapName + ".dat";
		String fileName = "lib" + File.separator + "map" + File.separator + gameFile;
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

	public static void initBlocks(String mapName) {

		List<Block> blocks = new LinkedList<>();
		List<Enemy> enemies = new LinkedList<>();
		List<Coin> coins = new LinkedList<>();

		int width = World.block_width;
		int height = World.block_height;
		int scale = GameFrame.PIXEL_SCALE;

		char[][] map = getMap(mapName);

		Map<Character, List<Long>> blockBuildTimes = new HashMap<>();
		int totalSize = 0;
		long totalTime = System.currentTimeMillis();

		for ( int y = 0; y < map.length; y++ ) {
			for ( int x = 0; x < map[y].length; x++ ) {

				char c = map[y][x];
				int xcoord = x * width * scale;
				int ycoord = (212 - y * height) * scale;
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
				} else if ( c == '?' ) {
					blocks.add(new Question(new Pos(xcoord, ycoord)));
				} else if ( c == 's' ) {
					blocks.add(new Square(new Pos(xcoord, ycoord)));
				} else if ( c == 'e' ) {
					enemies.add(new Goomba(new Pos(xcoord, ycoord)));
				} else if ( c == 'c' ) {
					coins.add(new Coin(new Pos(xcoord, ycoord)));
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
	}
}
