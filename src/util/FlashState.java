package util;

import window.GameFrame;

public class FlashState {

	static {
		GameFrame.addPeriodicTask(FlashState::advanceState);
	}

	private static int state = 0;
	private static State curState = State.ONE;

	public static State getFlashState() {
		return curState;
	}

	private static void advanceState() {
		if ( state++ > 4 ) {
			state = 0;

			switch (curState) {
				case ONE:
					curState = State.TWO;
					break;
				case TWO:
					curState = State.THREE;
					break;
				case THREE:
					curState = State.FOUR;
					break;
				case FOUR:
					curState = State.FIVE;
					break;
				case FIVE:
					curState = State.SIX;
					break;
				default:
					curState = State.ONE;
			}
		}
	}

	public enum State {
		ONE, TWO, THREE, FOUR, FIVE, SIX
	}

}
