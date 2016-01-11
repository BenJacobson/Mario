package world.block;


import mechanics.Pos;
import util.Images;

public class Ground extends Block {

	public Ground(Pos pos) {
		super(pos);
		image = Images.ground;
	}
}
