package world.coin;


import mechanics.Pos;
import window.GameCanvas;

import java.awt.*;

public class Coin {

	Pos pos;

	Image image;

	public Coin(Pos pos, String fileName) {
		this.pos = pos;
		image = GameCanvas.initFrame(fileName);
	}
}
