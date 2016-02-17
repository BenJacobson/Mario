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

	int score = 0;
	int coins = 0;
	long startTime = System.currentTimeMillis();
	long pauseTime;
	State state = State.NORMAL;
	Font font = new Font(Font.MONOSPACED, Font.BOLD, 10*GameFrame.pixelScale());

	private Stats() {}

	public void draw(Graphics2D g2) {
		g2.setColor(Color.WHITE);
		g2.setFont(font);
		FontMetrics metrics = g2.getFontMetrics();
		int y = GameFrame.blockDimension();
		int line2 = metrics.getHeight()/2 + GameFrame.pixelScale();

		g2.drawString("MARIO", GameFrame.gameWidth()/16, y);
		g2.drawString(String.format("%06d", score), GameFrame.gameWidth()/16, y + line2);

		g2.drawImage(getCoinImage(), GameFrame.gameWidth()*5/16 + GameFrame.pixelScale()*2,
				y + line2 - GameFrame.blockDimension()/2 + GameFrame.pixelScale(), null);
		g2.drawString(String.format("×%02d",coins), GameFrame.gameWidth()*11/32, y + line2);

		g2.drawString("WORLD", GameFrame.gameWidth()*9/16, y);
		g2.drawString(" 1-1", GameFrame.gameWidth()*9/16, y + line2);

		g2.drawString("TIME", GameFrame.gameWidth()*13/16, y);
		g2.drawString(String.format(" %03d",getTime()), GameFrame.gameWidth()*13/16, y + line2);
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
		coins++;
	}

	public void addPoints(int amount) {
		score += amount;
	}

	private enum State {
		NORMAL, PAUSED
	}
}
