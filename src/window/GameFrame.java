package window;

import mario.Mario;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import util.AudioController;
import util.FlashState;
import util.Images;
import util.Maps;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
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

	private Timer timer = new Timer();

	public GameFrame() {

		Maps.initBlocks("1-1");

		setTitle("Super Mario Bros.");

		setApplicationIcon("mario_stand.png");

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		setKeyListener();

		setSize(gameWidth(), gameHeight());
		setResizable(false);

		setLocation();

		setContentPane(new GameCanvas());

		timer.schedule(new NextFrameTask(), 0, 20);

		setVisible(true);

		AudioController.startTheme();
	}

	private void setApplicationIcon(final String fileName) {
		try {
			final String imageFolder = "/pic/";
			Image img = ImageIO.read(new BufferedInputStream(Images.class.getResourceAsStream(imageFolder + fileName)));
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
			FlashState.advanceState();
			AudioController.loopTheme();
			repaint();
		}
	}
}
