package window;

import mario.Mario;
import main.MarioNes;
import util.Maps;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

// window the surrounds the game
public class GameFrame extends JFrame {

	private static final int PIXEL_SCALE = 4;
	private static final int UNSCALED_WIDTH = 256;
	private static final int UNSCALED_HEIGHT = 230;


	public static int gameWidth() { return UNSCALED_WIDTH*PIXEL_SCALE; }
	public static int gameHeight() { return UNSCALED_HEIGHT*PIXEL_SCALE; }
	public static int blockDimension() { return PIXEL_SCALE*16; }
	public static int pixelScale() { return PIXEL_SCALE; }

	Timer timer = new Timer();

	public GameFrame() {

		Maps.initBlocks("1-1");

		setTitle("Super Mario Bros.");

		setApplicationIcon();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		setKeyListener();

		setSize(gameWidth(), gameHeight());
		setResizable(false);

		setLocation();

		setContentPane(new GameCanvas());

		timer.schedule(new NextFrameTask(), 0, 20);

		setVisible(true);
	}

	private void setApplicationIcon() {
		try {
			final String filePath = (MarioNes.jar ? "/pic/" : "lib" + File.separator + "pic" + File.separator) + "mario_stand.png";
			Image img = ImageIO.read(new File(filePath));
			setIconImage(img);
		} catch (IOException e) {
			// do nothing
		}
	}

	private void setKeyListener() {
		requestFocus();
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				Mario.getInstance().setDirection(e.getKeyCode());
			}

			@Override
			public void keyReleased(KeyEvent e) {
				Mario.getInstance().unsetDirection(e.getKeyCode());
			}
		});
	}

	private void setLocation() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;

		Dimension windowSize = getSize();
		int windowWidth = windowSize.width;
		int windowHeight = windowSize.height;

		super.setLocation( (screenWidth-windowWidth)/2, (screenHeight-windowHeight)/2 );
	}


	private class NextFrameTask extends TimerTask {
		public void run() {
			repaint();
		}
	}
}
