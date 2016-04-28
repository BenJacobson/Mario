package util.map;

import com.sun.media.sound.FFT;
import world.BlockCoin;
import world.Flagpole;
import world.background.Background;
import world.block.Block;
import world.enemy.Enemy;
import world.item.Item;

import java.util.LinkedList;
import java.util.List;

public class MapBlocks {

	private List<Block> blocks = new LinkedList<>();
	private List<Enemy> enemies = new LinkedList<>();
	private List<BlockCoin> coins = new LinkedList<>();
	private List<Background> backgrounds = new LinkedList<>();
	private List<Item> items = new LinkedList<>();
	private Flagpole flagpole = null;

	MapBlocks (List<Block> blocks, List<Enemy> enemies, List<BlockCoin> coins,
			   List<Background> backgrounds, List<Item> items, Flagpole flagpole) {
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

	public List<Background> getBackgrounds() {
		return backgrounds;
	}

	public List<Item> getItems() {
		return items;
	}

	public Flagpole getFlagpole() {
		return flagpole;
	}
}
