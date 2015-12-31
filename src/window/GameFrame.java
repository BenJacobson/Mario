package window;

import mario.Mario;
import main.MarioNes;

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

	Timer timer = new Timer();

	public GameFrame() {

		MarioNes.initBlocks();

		setTitle("Super Mario Bros.");

		setApplicationIcon();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		setKeyListener();

		setSize(256*MarioNes.PIXEL_SCALE, 230*MarioNes.PIXEL_SCALE);
		setResizable(false);

		setLocation();

		setContentPane(new GameCanvas());

		timer.schedule(new NextFrameTask(), 0, 20);

		setVisible(true);
	}

	private void setApplicationIcon() {
		try {
			final String filePath = "lib" + File.separator + "pic" + File.separator + "mario_stand.png";
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
