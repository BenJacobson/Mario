package mario;

import mechanics.Pos;
import mechanics.Vector;
import stats.Stats;
import util.Images;
import window.GameCanvas;
import window.GameFrame;
import world.collision.CollisionResult;
import world.World;

import java.awt.*;
import java.awt.geom.Rectangle2D;

// contains the information and subclasses about mario on screen
public class Mario {

	private static Mario instance;

	public static Mario getInstance() {
		if ( instance == null ) {
			instance = new Mario();
		}
		return instance;
	}

	Pos originalPos = new Pos(25*GameFrame.pixelScale(), 180*GameFrame.pixelScale());
	Pos currentPos = originalPos.copy();
	Vector vector = new Vector();

	private boolean movingLeft = false;
	private boolean movingRight = false;
	private boolean running = false;
	private boolean lastDirectionForward = true;
	private boolean jump = false;
	private boolean canJumpAgain = false;
	private boolean jumpHeld = false;

	private int numberOfPasses = 0;
	private int jumpHeldState = 0;
	private int jumpHeldPasses = 0;
	private int deadState = 0;
	private int invincible = 0;

	private FrameState frameState = FrameState.STAND;
	private PowerState powerState = PowerState.SMALL;
	
	private final Image stand_frame = Images.stand_frame;
	private final Image jump_frame = Images.jump_frame;
	private final Image stand_frame_back = Images.stand_frame_back;
	private final Image jump_frame_back = Images.jump_frame_back;
	private final Image run_frame_13 = Images.run_frame_13;
	private final Image run_frame_13_back = Images.run_frame_13_back;
	private final Image run_frame_2 = Images.run_frame_2;
	private final Image run_frame_2_back = Images.run_frame_2_back;
	private final Image run_frame_4 = Images.run_frame_4;
	private final Image run_frame_4_back = Images.run_frame_4_back;
	private final Image dead_frame = Images.dead_frame;

	private final int LEFT = 37;
	private final int RIGHT = 39;
	private final int RUN = 88;
	private final int SPACE = 32;


	private Mario() {}

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
			case DEAD:
				return dead_frame;
		}

		return stand_frame;
	}

	private void setRunFrame() {
		if ( !canJumpAgain ) {
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

			if ( !canJumpAgain ) {
				// falling while running, switch frame to jump frame
				frameState = FrameState.JUMP;
			}

			numberOfPasses++;

			int passesToWait = vector.isFast() ? 3 : 5;
			if ( numberOfPasses <= passesToWait ) {
				// don't change the frame yet
				return;
			}

			numberOfPasses = 0;

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
		move();
		Image currentFrame = getCurrentFrame();

		int extraX = 0, extraY = 0;
		if ( currentFrame == jump_frame || currentFrame == jump_frame_back ||
				currentFrame == run_frame_4 || currentFrame == run_frame_4_back ) {
			extraX = 2 * GameFrame.pixelScale();
		} else if ( currentFrame == run_frame_13 || currentFrame == run_frame_13_back ) {
			extraY = GameFrame.pixelScale();
		}

		g2.drawImage(currentFrame, currentPos.getX()-extraX, currentPos.getY()+extraY,
				currentFrame.getWidth(null), currentFrame.getHeight(null), null);
	}

	public void setDirection(int action) {

		if ( action == LEFT ) {
			movingLeft = true;
			movingRight = false;
			if (canJumpAgain) lastDirectionForward = false;
		} else if ( action == RIGHT ) {
			movingRight = true;
			movingLeft = false;
			if (canJumpAgain) lastDirectionForward = true;
		} else if ( action == RUN  && canJumpAgain ) {
			running = true;
		} else if ( action == SPACE ) {
			if ( vector.getDy() == 0 && canJumpAgain && !jumpHeld ) {
				jump = true;
				jumpHeld = true;
				jumpHeldState = 0;
			}
		}
	}

	public void unsetDirection(int action) {
		if ( action == LEFT ) {
			movingLeft = false;
		} else if ( action == RIGHT ) {
			movingRight = false;
		} else if ( action == RUN ) {
			running = false;
		} else if ( action == SPACE ) {
			jumpHeld = false;
		}
	}

	private void move() {

		if ( frameState != FrameState.DEAD ) {

			handleJump();

			handleEnemies();

			handleItems();

			updateVector();

			currentPos.move(vector);

			handleCollisions();

			checkDead();

		} else {

			if ( deadState++ > 50 && deadState < 100 ) {
				vector.gravity();
				currentPos.move(vector);
			} else if ( deadState > 150 ) {
				reset();
			}

		}
	}

	private void handleItems() {
		if ( World.getInstance().findMarioItemCollisions(getRect()) ) {
			powerUp();
		}
	}

	private void powerUp() {
		if ( powerState == PowerState.SMALL ) {
			powerState = PowerState.BIG;
		} else if ( powerState == PowerState.BIG ) {
			powerState = PowerState.FIRE;
		}
	}

	public boolean isBig() {
		return !(powerState == PowerState.SMALL);
	}

	private void hit() {
		if ( invincible > 0 ) {
			
		} else if ( powerState == PowerState.SMALL ) {
			dead();
		} else {
			powerState = PowerState.SMALL;
			invincible = 25;
		}
	}

	private void handleEnemies() {
		Boolean[] hits = World.getInstance().findMarioEnemyCollisions(getRect());

		if ( hits[0] ) {
			hit();
		}
		if ( hits[1] ) {
			vector.bounce();
		}
	}

	private void checkDead() {
		if ( currentPos.getY() > 250*GameFrame.pixelScale() ) {
			dead();
		}
	}

	private void dead() {
		movingLeft = false;
		movingRight = false;
		canJumpAgain = false;
		frameState = FrameState.DEAD;
		vector.stop();
		vector.jump();
		Stats.getInstance().pause();
	}

	public boolean isNotDead() {
		return frameState != FrameState.DEAD;
	}

	private void reset() {
		deadState = 0;
		currentPos = originalPos.copy();
		World.getInstance().reset();
		frameState = FrameState.STAND;
		vector.stop();
		lastDirectionForward = true;
		canJumpAgain = true;
		jump = false;
		Stats.getInstance().resume();
   	}

	private void handleCollisions() {

		CollisionResult collisionResult = World.getInstance().blockCollisions(getRect(), vector);

		currentPos.moveDown( collisionResult.getDy() );
		currentPos.moveRight( collisionResult.getDx() );

		canJumpAgain = collisionResult.isTopHit();

		if ( collisionResult.isTopHit() && !isInRunState() && isNotDead() ) {
			frameState = FrameState.STAND;
		}

		// stop moving if you hit something
		if ( collisionResult.isTopHit() || collisionResult.isBottomHit() ) {
			vector.hitY();
		}
		if ( collisionResult.isLeftHit() || collisionResult.isRightHit() ) {
			vector.hitX();
		}

		// keep mario within the left half of the screen
		if ( currentPos.getX() < 2*GameFrame.pixelScale() ) {
			currentPos.setX( 2*GameFrame.pixelScale() );
		} else if ( currentPos.getX() > GameFrame.gameWidth()/2 ) {
			World.getInstance().addOffset( currentPos.getX() - GameFrame.gameWidth()/2 );
			currentPos.setX( GameFrame.gameWidth()/2 );
		}

		if ( invincible > 0 ) {
			invincible--;
		}
	}


	private Rectangle2D getRect() {
		return new Rectangle2D.Double(currentPos.getX(), currentPos.getY(), stand_frame.getWidth(null), stand_frame.getHeight(null));
	}

	private boolean isInRunState() {
		return frameState == FrameState.RUN1 || frameState == FrameState.RUN2 ||
				frameState == FrameState.RUN3 || frameState == FrameState.RUN4;
	}

	private void updateVector() {

		if ( movingLeft ) {
			vector.moveLeft(running);
			setRunFrame();
			updateRunFrame();
		} else if ( movingRight ) {
			vector.moveRight(running);
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
		int jumpHeldMax = 12;

		if ( jump ) {
			vector.jump();
			jump = false;
			canJumpAgain = false;
			frameState = FrameState.JUMP;
		} else if ( jumpHeld && jumpHeldState <= jumpHeldMax ) {

			int jumpPassesToWait = 0;
			if ( jumpHeldPasses <= jumpPassesToWait ) {
				jumpHeldPasses++;
				return;
			}

			jumpHeldPasses = 0;
			vector.jumpHold();
			jumpHeldState++;
		}
	}

	private enum FrameState {
		STAND, JUMP, RUN1, RUN2, RUN3, RUN4, DEAD
	}

	private enum PowerState {
		SMALL, BIG, FIRE
	}
}
