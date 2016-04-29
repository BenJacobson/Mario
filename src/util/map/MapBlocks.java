package util.map;

import world.BlockCoin;
import world.Flagpole;
import world.background.Backgrounds;
import world.block.Block;
import world.enemy.Enemy;
import world.item.Item;

import java.util.LinkedList;
import java.util.List;

public class MapBlocks {

	private List<Block> blocks = new LinkedList<>();
	private List<Enemy> enemies = new LinkedList<>();
	private List<BlockCoin> coins = new LinkedList<>();
	private List<Backgrounds> backgrounds = new LinkedList<>();
	private List<Item> items = new LinkedList<>();
	private Flagpole flagpole = null;

	MapBlocks (List<Block> blocks, List<Enemy> enemies, List<BlockCoin> coins,
			   List<Backgrounds> backgrounds, List<Item> items, Flagpole flagpole) {
		this.blocks = blocks;
		this.enemies = enemies;
		this.coins = coins;
		this.backgrounds = backgrounds;
		this.items = items;
		this.flagpole = flagpole;
	}

	public List<Block> getBlocks() {
		return blocks;
	}

	public List<Enemy> getEnemies() {
		return enemies;
	}

	public List<BlockCoin> getCoins() {
		return coins;
	}

	public List<Backgrounds> getBackgrounds() {
		return backgrounds;
	}

	public List<Item> getItems() {
		return items;
	}

	public Flagpole getFlagpole() {
		return flagpole;
	}
}
