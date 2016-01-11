package world.block;

import mechanics.Pos;
import util.Images;

public class Square extends Block {
	public Square(Pos pos) {
		super(pos);
		image = Images.square;
	}
}
