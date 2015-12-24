package main;

import javax.swing.*;
import java.awt.*;

public class GameCanvas extends JComponent {

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		drawBackground(g2);
		drawWorld(g2);
		drawEnemies(g2);
		drawMario(g2);
	}

	private void drawBackground(Graphics2D g2) {
		g2.setColor(new Color(181, 232, 255));
		g2.fillRect(0, 0, getWidth(), getHeight());
	}

	private void drawWorld(Graphics2D g2) {
		World.getInstance().draw(g2);
	}

	private void drawEnemies(Graphics2D g2) {

	}

	private void drawMario(Graphics2D g2) {
		Mario.getInstance().draw(g2);
	}

}
