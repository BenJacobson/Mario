package stats;

import main.MarioNes;

import java.awt.*;

public class Stats {

	int score = 0;
	int coins = 0;
	long startTime = System.currentTimeMillis();
	long pauseTime;
	State state = State.STOP;
	Font font = new Font("fixedsys", Font.PLAIN, 40);

	public void draw(Graphics2D g2) {
		g2.setColor(Color.WHITE);
		g2.setFont(font);
		FontMetrics metrics = g2.getFontMetrics();

		g2.drawString("MARIO", 256*MarioNes.PIXEL_SCALE/8, 100);
		g2.drawString(String.valueOf(score), 256*MarioNes.PIXEL_SCALE/8, 100 + metrics.getHeight());

		g2.drawString("x"+coins, 256*MarioNes.PIXEL_SCALE*3/8, 100 + metrics.getHeight());

		g2.drawString("WORLD", 256*MarioNes.PIXEL_SCALE*5/8, 100);
		g2.drawString("1-1", 256*MarioNes.PIXEL_SCALE*5/8, 100 + metrics.getHeight());

		g2.drawString("TIME", 256*MarioNes.PIXEL_SCALE*7/8, 100);
		g2.drawString(getTime(), 256*MarioNes.PIXEL_SCALE*7/8, 100 + metrics.getHeight());
	}

	private String getTime() {
		long time = System.currentTimeMillis() - startTime;
		int sec = 400 - (int) time/500;

		return String.valueOf(sec);
	}

	public void start() {
		state = State.START;
		startTime = System.currentTimeMillis();
	}

	public void pause() {
		pauseTime = System.currentTimeMillis();
	}

	public void resume() {
		long timeStopped = System.currentTimeMillis() - pauseTime;
		startTime += timeStopped;
	}

	public void gotCoin() {
		coins++;
	}

	private enum State {
		START, STOP, PAUSE
	}
}
