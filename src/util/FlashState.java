package util;

public class FlashState {

	private static int state = 0;
	private static State curState = State.ONE;

	public static State getFlashState() {
		return curState;
	}

	public static void advanceState() {
		if ( state++ > 5 ) {
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
				default:
					curState = State.ONE;
			}
		}
	}

	public enum State {
		ONE, TWO, THREE, FOUR;
	}

}
