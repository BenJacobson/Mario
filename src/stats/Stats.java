package stats;

import util.FlashState;
import util.Images;
import window.GameFrame;

import java.awt.*;

public class Stats {

	private static Stats instance;

	public static Stats getInstance() {
		if ( instance == null ) {
			instance = new Stats();
		}
		return instance;
	}

	private int score = 0;
	private int coins = 0;
	private long startTime = System.currentTimeMillis();
	private long pauseTime;
	private String name = "";
	private State state = State.NORMAL;
	private Font font = new Font(Font.MONOSPACED, Font.BOLD, 10*GameFrame.pixelScale());

	private Stats() {}

	public void draw(Graphics2D g2) {
		g2.setColor(Color.WHITE);
		g2.setFont(font);
		FontMetrics metrics = g2.getFontMetrics();
		int y = GameFrame.blockDimension();
		int line2 = metrics.getHeight()/2 + GameFrame.pixelScale();
		int width = GameFrame.gameWidth();

		g2.drawString("MARIO", width/16, y);
		g2.drawString(String.format("%06d", score), width/16, y + line2);

		g2.drawImage(getCoinImage(), width*5/16 + GameFrame.pixelScale()*2,
				y/2 + line2 + GameFrame.pixelScale(), null);
		g2.drawString(String.format("×%02d",coins), width*11/32, y + line2);

		g2.drawString("WORLD", width*9/16, y);
		g2.drawString(name, width*9/16, y + line2);

		g2.drawString("TIME", width*13/16, y);
		g2.drawString(String.format(" %03d",getTime()), width*13/16, y + line2);
	}

	private Image getCoinImage() {
		switch(FlashState.getFlashState()) {
			case TWO:
			case FOUR:
				return Images.coin_brown;
			case THREE:
				return Images.coin_dark;
			default:
				return Images.coin_normal;
		}
	}

	public long getTime() {

		long timeToUse;

		if ( state == State.NORMAL ) {
			timeToUse = System.currentTimeMillis();
		} else {
			timeToUse = pauseTime;
		}

		long time = timeToUse - startTime;
		long sec = 400 - (time / 500);

		return sec;
	}

	public void pause() {
		if ( state != State.PAUSED ) {
			pauseTime = System.currentTimeMillis();
			state = State.PAUSED;
		}
	}

	public void resume() {
		if ( state == State.PAUSED ) {
			long timeStopped = System.currentTimeMillis() - pauseTime;
			startTime += timeStopped;
			state = State.NORMAL;
		}
	}

	public void reset() {
		startTime = System.currentTimeMillis();
	}

	public void gotCoin() {
		if ( coins < 99 ) {
			coins++;
		} else {
			coins = 0;
			// add 1up
		}

	}

	public void addPoints(int amount) {
		score += amount;
	}

	public void setName(String newName) {
		name = " " + newName;
	}

	private enum State {
		NORMAL, PAUSED
	}
}
