package world;


import mechanics.Pos;
import util.Images;
import window.GameCanvas;

import java.awt.*;

public class Coin {

	Pos pos;
	Image image;

	public Coin(Pos pos) {
		this.pos = pos;
		image = Images.coin;
	}

	void draw() {

	}
}
