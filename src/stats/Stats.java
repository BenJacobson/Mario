package stats;

import java.awt.*;

public class Stats {

	int score = 0;
	int coins = 0;
	long startTime;
	long pauseTime;
	State state = State.STOP;

	public void draw(Graphics2D g2) {
		g2.drawString("MARIO\n"+score, 100, 100);
	}

	public void start() {
		state = State.START;
		startTime = System.currentTimeMillis();
	}

	private enum State {
		START, STOP, PAUSE
	}
}
