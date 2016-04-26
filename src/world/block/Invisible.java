package world.block;


import util.mechanics.Pos;
import util.Images;

public class Invisible extends Block {

	public Invisible(Pos pos) {
		super(pos);
		image = Images.invisible;
	}
}
