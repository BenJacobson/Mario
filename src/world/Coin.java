package world;


import mechanics.Pos;
import window.GameCanvas;

import java.awt.*;

public class Coin {

	Pos pos;

	Image image;

	public Coin(Pos pos) {
		this.pos = pos;
		image = GameCanvas.initFrame(GameCanvas.imageFolder + "coin.png");
	}
}
