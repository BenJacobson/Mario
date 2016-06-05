package util.map;

import util.Images;
import world.Flagpole;
import world.background.Background;
import world.background.Backgrounds;
import world.background.SmallCastle;
import world.enemy.Enemy;
import world.enemy.Goomba;
import util.mechanics.Pos;
import window.GameFrame;
import world.BlockCoin;
import world.block.*;
import world.enemy.Koopa;
import world.item.Item;
import world.item.PowerUp;

import java.io.*;
import java.util.*;

public class MapLoader {

	private static char[][] getMap(String mapName) {

		String mapFile = mapName + ".dat";
		String mapPath = "/map/";
		Scanner mapScan = null;
		List<char[]> linesList = new LinkedList<>();

		try {
			mapScan = new Scanner(new BufferedInputStream(MapLoader.class.getResourceAsStream(mapPath + mapFile)));
			while (mapScan.hasNextLine()) {
				char[] line = mapScan.nextLine().toCharArray();
				linesList.add(line);
			}
		} finally {
			if ( mapScan != null ) {
				mapScan.close();
			}
		}

		Collections.reverse(linesList);
		return linesList.toArray(new char[linesList.size()][]);
	}

	public static MapBlocks loadMap(String mapName) {

		List<Block> blocks = new LinkedList<>();
		List<Enemy> enemies = new LinkedList<>();
		List<BlockCoin> coins = new LinkedList<>();
		List<Backgrounds> backgrounds = new LinkedList<>();
		List<Item> items = new LinkedList<>();
		Flagpole flagpole = null;

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
					case 'k':
						enemies.add(new Koopa(new Pos(xcoord, ycoord)));
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
					case 'p':
						backgrounds.add(new Background(Images.cloud_small, new Pos(xcoord, ycoord)));
						break;
					case 'o':
						backgrounds.add(new Background(Images.cloud_medium, new Pos(xcoord, ycoord)));
						break;
					case 'u':
						backgrounds.add(new Background(Images.cloud_large, new Pos(xcoord, ycoord)));
						break;
					case 'n':
						backgrounds.add(new Background(Images.bush_small, new Pos(xcoord+GameFrame.blockDimension()/2, ycoord)));
						break;
					case 'v':
						backgrounds.add(new Background(Images.bush_medium, new Pos(xcoord+GameFrame.blockDimension()/2, ycoord)));
						break;
					case 'x':
						backgrounds.add(new Background(Images.bush_large, new Pos(xcoord+GameFrame.blockDimension()/2, ycoord)));
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
						break;
					case 'q':
						backgrounds.add(new SmallCastle(new Pos(xcoord, ycoord)));
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
			List times = (List) entry.getValue();
			for ( Object time : times ) {
				sum += (Long)time;
			}
			char c = (char)entry.getKey();
			System.out.printf("'%c' : %f\n", c, sum/times.size());
		}

		return new MapBlocks(blocks, enemies, coins, backgrounds, items, flagpole);
	}
}
