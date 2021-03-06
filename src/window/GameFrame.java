package window;

import util.AudioController;
import util.Images;
import util.input.GameController;
import world.World;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Timer;

// window the surrounds the game
public class GameFrame extends JFrame {

	private static final int PIXEL_SCALE = getOptimalScale();
	private static final int UNSCALED_WIDTH = 256;
	private static final int UNSCALED_HEIGHT = 230;

	private static List<PeriodicTask> periodicTaskList = new LinkedList<>();

	public static int gameWidth() { return UNSCALED_WIDTH*PIXEL_SCALE; }
	public static int gameHeight() { return UNSCALED_HEIGHT*PIXEL_SCALE; }
	public static int blockDimension() { return PIXEL_SCALE*16; }
	public static int pixelScale() { return PIXEL_SCALE; }

	public GameFrame() {

		World.getInstance().loadMap("1-1");

		setTitle("Super Mario Bros.");

		setIconImage(Images.stand_frame);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		setKeyListener();

		setSize(gameWidth(), gameHeight());
		setResizable(false);

		setLocation();

		setContentPane(new GameCanvas());

		setVisible(true);

		Timer timer = new Timer();
		timer.schedule(new TaskExecutor(), 0, 20);
		addPeriodicTask(this::repaint);

		AudioController.startTheme();
	}

	private void setKeyListener() {
		requestFocus();
		GameController gameController = GameController.getInstance();
		if ( !gameController.usingGamepad() ) {
			this.addKeyListener(gameController);
		}
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

	private static int getOptimalScale() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		int scale = 0;

		while ( scale*UNSCALED_HEIGHT < height && scale*UNSCALED_WIDTH < width ) {
			scale++;
		}

		return scale - 2;
	}

	public static void addPeriodicTask(PeriodicTask task) {
		periodicTaskList.add(task);
	}

	private class TaskExecutor extends TimerTask {
		@Override
		public void run() {
			periodicTaskList.stream().parallel().forEach(PeriodicTask::run);
		}
	}
}
