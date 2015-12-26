package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;

public class Mario {

	private static Mario instance;

	public static Mario getInstance() {
		if ( instance == null ) {
			instance = new Mario();
		}
		return instance;
	}

	Pos currentPos = new Pos(100, 100);
	Vector vector = new Vector();

	private boolean movingLeft = false;
	private boolean movingRight = false;
	private boolean lastDirectionForward = true;
	private boolean jump = false;
	private boolean canJumpAgain = false;

	private int passesBetweenFrames = 5;
	private int numberOfPasses = 0;

	private FrameState frameState = FrameState.STAND;
	private final Image stand_frame = initFrame("mario_stand.png");
	private final Image jump_frame = initFrame("mario_jump.png");
	private final Image stand_frame_back = initFrame("mario_stand_back.png");
	private final Image jump_frame_back = initFrame("mario_jump_back.png");
	private final Image run_frame_13 = initFrame("mario_run_13.png");
	private final Image run_frame_13_back = initFrame("mario_run_13_back.png");
	private final Image run_frame_2 = initFrame("mario_run_2.png");
	private final Image run_frame_2_back = initFrame("mario_run_2_back.png");
	private final Image run_frame_4 = initFrame("mario_run_4.png");
	private final Image run_frame_4_back = initFrame("mario_run_4_back.png");

	public static int LEFT = 37;
	public static int RIGHT = 39;
	public static int SPACE = 32;


	private Mario() {}

	private Image initFrame(String fileName) {
		final String filePath = "lib" + File.separator + "pic" + File.separator + fileName;
		try {
			Image frame = ImageIO.read(new File(filePath));
			return frame.getScaledInstance(frame.getWidth(null)*MarioNes.PIXEL_SCALE, frame.getHeight(null)*MarioNes.PIXEL_SCALE, 0);
		} catch (IOException e) {
			System.out.println("Could not load " + filePath);
			System.exit(0);
			return null;
		}
	}

	private Image getCurrentFrame() {

		switch (frameState) {
			case JUMP:
				if ( lastDirectionForward ) {
					return jump_frame;
				} else {
					return jump_frame_back;
				}
			case STAND:
				if ( lastDirectionForward ) {
					return stand_frame;
				} else {
					return stand_frame_back;
				}
			case RUN1:
				if ( lastDirectionForward ) {
					return run_frame_13;
				} else {
					return run_frame_13_back;
				}
			case RUN2:
				if ( lastDirectionForward ) {
					return run_frame_2;
				} else {
					return run_frame_2_back;
				}
			case RUN3:
				if ( lastDirectionForward ) {
					return run_frame_13;
				} else {
					return run_frame_13_back;
				}
			case RUN4:
				if ( lastDirectionForward ) {
					return run_frame_4;
				} else {
					return run_frame_4_back;
				}
		}

		return null;
	}

	private void setRunFrame() {
		if ( frameState == FrameState.JUMP ) {
			// do nothing because you can't run in the air
			return;
		}

		if ( frameState != FrameState.RUN1 && frameState != FrameState.RUN2 &&
				frameState != FrameState.RUN3 && frameState != FrameState.RUN4 ) {
			// start running frames
			frameState = FrameState.RUN1;
		}
	}

	private void updateRunFrame() {
		 if ( frameState == FrameState.RUN1 || frameState == FrameState.RUN2 ||
				 frameState == FrameState.RUN3 || frameState == FrameState.RUN4 ) {

			numberOfPasses++;

			if ( numberOfPasses > passesBetweenFrames ) {
				numberOfPasses = 0;
			} else {
				// don't change the frame yet
				return;
			}


			// advance to next running frame
			if (frameState == FrameState.RUN1) {
				frameState = FrameState.RUN2;
			} else if (frameState == FrameState.RUN2) {
				frameState = FrameState.RUN3;
			} else if (frameState == FrameState.RUN3) {
				frameState = FrameState.RUN4;
			} else {
				frameState = FrameState.RUN1;
			}
		}
	}

	public void draw(Graphics2D g2) {
		Image currentFrame = getCurrentFrame();

		int extraX = 0, extraY = 0;
		if ( currentFrame == jump_frame || currentFrame == jump_frame_back ||
				currentFrame == run_frame_4 || currentFrame == run_frame_4_back ) {
			extraX = 2*MarioNes.PIXEL_SCALE;
		} else if ( currentFrame == run_frame_13 || currentFrame == run_frame_13_back ) {
			extraY = MarioNes.PIXEL_SCALE;
		}

		g2.drawImage(currentFrame, currentPos.getX()-extraX, currentPos.getY()-extraY,
				currentFrame.getWidth(null), currentFrame.getHeight(null), null);
	}

	public void setDirection(int action) {
		if ( action == LEFT ) {
			movingLeft = true;
			movingRight = false;
			lastDirectionForward = canJumpAgain ? false : lastDirectionForward;
		} else if ( action == RIGHT ) {
			movingRight = true;
			movingLeft = false;
			lastDirectionForward = canJumpAgain ? true : lastDirectionForward;
		} else if ( action == SPACE ) {
			if ( vector.getDy() == 0 && canJumpAgain) {
				jump = true;
			}
		}
	}

	public void unsetDirection(int action) {
		if ( action == LEFT || action == RIGHT ) {
			movingLeft = false;
			movingRight = false;
		}
	}

	public void move() {

		handleJump();

		updateVector();

		currentPos.move(vector);

		handleCollisions();

		checkDead();
	}

	private void checkDead() {
		if ( currentPos.getY() > 250*MarioNes.PIXEL_SCALE ) {
			dead();
		}
	}

	private void dead() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException ex) {
			// move on
		}
		currentPos.set(100,100);
		World.getInstance().resetOffset();
		frameState = FrameState.STAND;
	}

	private void handleCollisions() {

		boolean isCollision = World.getInstance().collision(currentPos, stand_frame.getWidth(null), stand_frame.getHeight(null), vector);
		canJumpAgain |= isCollision;
		if ( isCollision && !isInRunState() ) {
			frameState = FrameState.STAND;
		}

		if ( currentPos.getX() < 2*MarioNes.PIXEL_SCALE ) {
			currentPos.setX( 5 );
		} else if ( currentPos.getX() > 256*MarioNes.PIXEL_SCALE/2 ) {
			World.getInstance().addOffset( currentPos.getX() - 256*MarioNes.PIXEL_SCALE/2 );
			currentPos.setX( 256*MarioNes.PIXEL_SCALE/2 );
		}
	}

	private boolean isInRunState() {
		return frameState == FrameState.RUN1 || frameState == FrameState.RUN2 ||
				frameState == FrameState.RUN3 || frameState == FrameState.RUN4;
	}

	private void updateVector() {
		if ( movingLeft ) {
			vector.moveLeft();
			setRunFrame();
			updateRunFrame();
		} else if ( movingRight ) {
			vector.moveRight();
			setRunFrame();
			updateRunFrame();
		} else {
			vector.reduceSpeed();
			updateRunFrame();
		}
		vector.gravity();

		if ( vector.getDx() == 0 && canJumpAgain ) {
			frameState = FrameState.STAND;
		}
	}

	private void handleJump() {
		if ( jump ) {
			vector.jump();
			jump = false;
			canJumpAgain = false;
			frameState = FrameState.JUMP;
		}
	}

	private enum FrameState {
		STAND, JUMP, RUN1, RUN2, RUN3, RUN4, DEAD
	}
}
