package window;

import mario.Mario;
import world.World;

import javax.swing.*;
import java.awt.*;

// class to handle the painting of the game
class GameCanvas extends JComponent {

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		drawWorld(g2);
		drawMario(g2);
	}

	private void drawWorld(Graphics2D g2) {
		World.getInstance().draw(g2);
	}

	private void drawMario(Graphics2D g2) {
		Mario.getInstance().draw(g2);
	}

}
