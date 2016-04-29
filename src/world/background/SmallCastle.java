package world.background;

import util.Images;
import util.mechanics.Pos;
import window.GameFrame;

import java.awt.*;

public class SmallCastle implements Backgrounds {

	private Pos pos;
	private Image[][] castleImageArray = initCastleImageArray();

	public SmallCastle(Pos pos) {
		this.pos = pos;
	}

	private Image[][] initCastleImageArray() {
		Image castle_arch = Images.castle_arch;
		Image castle_brick = Images.castle_brick;
		Image castle_entry = Images.castle_entry;
		Image castle_tier_closed = Images.castle_tier_closed;
		Image castle_tier_open = Images.castle_tier_open;
		Image castle_window_left = Images.castle_window_left;
		Image castle_window_right = Images.castle_window_right;
		return new Image[][] {
				{ null, castle_tier_open, castle_tier_open, castle_tier_open, null },
				{ null, castle_window_left, castle_brick, castle_window_right, null },
				{ castle_tier_open, castle_tier_closed, castle_tier_closed, castle_tier_closed, castle_tier_open },
				{ castle_brick, castle_brick, castle_arch, castle_brick, castle_brick },
				{ castle_brick, castle_brick, castle_entry, castle_brick, castle_brick}
		};
	}

	@Override
	public void draw(Graphics2D g2, int offset) {

		// if the castle is off-screen, don't waste the time
		if ( getX(offset) > GameFrame.gameWidth() )
			return;

		int blockDimension = GameFrame.blockDimension();
		int x = getX(offset);
		int y = getY();
		for ( int i = 0; i < castleImageArray.length; i++ ) {
			for ( int j = 0; j < castleImageArray[i].length; j++ ) {
				g2.drawImage(castleImageArray[i][j], x + j*blockDimension, y + i*blockDimension, null);
			}
		}
	}

	@Override
	public int getX(int offset) {
		return pos.getX() - offset;
	}

	@Override
	public int getY() {
		return pos.getY();
	}
}
