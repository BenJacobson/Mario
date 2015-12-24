package main;

import sun.util.calendar.BaseCalendar;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class GameFrame extends JFrame {

	private GameCanvas gameCanvas = new GameCanvas();

	Timer timer = new Timer();

	public GameFrame() {

		setTitle("Super Mario Bros.");

		setApplicationIcon();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setKeyListener();

		setSize(256*MarioNes.PIXEL_SCALE,230*MarioNes.PIXEL_SCALE);
		setResizable(false);

		setLocation();

		setContentPane(gameCanvas);

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
			Mario.getInstance().move();
			repaint();
		}
	}
}
