package window;

import mario.Mario;
import main.MarioNes;
import stats.Stats;
import util.Maps;
import world.World;
import world.background.Background;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

// class to handle the painting of the game
class GameCanvas extends JComponent {

	private Color backgroundColor = new Color(86, 151, 255);

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		drawBackground(g2);
		drawStats(g2);
		drawWorld(g2);
		drawMario(g2);
	}

	private void drawBackground(Graphics2D g2) {
		g2.setColor(backgroundColor);
		g2.fillRect(0, 0, getWidth(), getHeight());

		for (Background background : Maps.backgrounds ) {
			background.draw(g2, World.getInstance().getOffest());
		}
	}

	private void drawStats(Graphics2D g2) {
		Stats.getInstance().draw(g2);
	}

	private void drawWorld(Graphics2D g2) {
		World.getInstance().draw(g2);
	}

	private void drawMario(Graphics2D g2) {
		Mario.getInstance().draw(g2);
	}

}
