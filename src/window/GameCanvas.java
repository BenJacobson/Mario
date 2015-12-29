package window;

import enemy.Enemy;
import mario.Mario;
import main.MarioNes;
import world.World;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

// class to handle the painting of the game
public class GameCanvas extends JComponent {

	public static final String imageFolder = "lib" + File.separator + "pic" + File.separator;

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		drawBackground(g2);
		drawWorld(g2);
		drawMario(g2);
	}

	private void drawBackground(Graphics2D g2) {
		g2.setColor(new Color(132, 205, 254));
		g2.fillRect(0, 0, getWidth(), getHeight());
	}

	private void drawWorld(Graphics2D g2) {
		World.getInstance().draw(g2);
	}

	private void drawMario(Graphics2D g2) {
		Mario.getInstance().draw(g2);
	}

	public static Image initFrame(String filePath) {
		try {
			Image frame = ImageIO.read(new File(filePath));
			return frame.getScaledInstance(frame.getWidth(null)* MarioNes.PIXEL_SCALE, frame.getHeight(null)*MarioNes.PIXEL_SCALE, 0);
		} catch (IOException e) {
			System.out.println("Could not load " + filePath);
			System.exit(0);
			return null;
		}
	}

}
