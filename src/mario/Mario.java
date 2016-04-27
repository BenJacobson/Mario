package mario;

import util.mechanics.Pos;
import util.mechanics.Vector;
import stats.Stats;
import util.AudioController;
import window.GameFrame;
import world.collision.CollisionResult;
import world.World;

import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.stream.Collectors;

// contains the information and subclasses about mario on screen
public class Mario {

	private static Mario instance;

	public static Mario getInstance() {
		if (instance == null) {
			instance = new Mario();
		}
		return instance;
	}

	private Pos originalPos = new Pos(25 * GameFrame.pixelScale(), 180 * GameFrame.pixelScale());
	private Pos currentPos = originalPos.copy();
	private Vector vector = new Vector();
	private Rectangle2D rect = new Rectangle2D.Double();

	private boolean movingLeft = false;
	private boolean movingRight = false;
	private boolean running = false;
	private boolean lastDirectionForward = true;
	private boolean jump = false;
	private boolean canJumpAgain = false;
	private boolean jumpHeld = false;
	private boolean canShootAgain = false;
	private boolean shoot = false;

	private int numberOfPasses = 0;
	private int jumpHeldState = 0;
	private int jumpHeldPasses = 0;
	private int deadState = 0;
	private int invincible = 0;

	private final int waitShootShort = 5;
	private final int waitShootLong = 40;
	private int oneShotAgo = waitShootShort;
	private int twoShotsAgo = waitShootLong;

	private FrameState frameState = FrameState.STAND;
	private PowerState powerState = PowerState.SMALL;

	private final MarioFrames marioFrames = new MarioFrames();
	private List<Fireball> fireballs = new ArrayList<>();

	private final int D_LEFT = 37;
	private final int D_RIGHT = 39;
	private final int BUTTON_A = 32;
	private final int BUTTON_B = 88;

	private Mario() {
		GameFrame.addPeriodicTask(this::move);
	}

	private void setRunFrame() {
		if (!canJumpAgain) {
			// do nothing because you can't run in the air
			return;
		}

		if (frameState != FrameState.RUN1 && frameState != FrameState.RUN2 &&
				frameState != FrameState.RUN3 && frameState != FrameState.RUN4) {
			// start running frames
			frameState = FrameState.RUN1;
		}
	}

	private void updateRunFrame() {

		if ( isInRunState() ) {

			if (!canJumpAgain) {
				// falling while running, stay on frame
				return;
			}

			int passesToWait = (int)(6 * (1-vector.getFast())) + 2;
			if (++numberOfPasses <= passesToWait) {
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
				if ( powerState == PowerState.SMALL ) {
					frameState = FrameState.RUN1;
				} else {
					frameState = FrameState.RUN2;
				}
			}
		}
	}

	public void draw(Graphics2D g2) {
		drawFireballs(g2);
		drawMario(g2);
	}

	private void drawMario(Graphics2D g2) {
		// Flash mario while he is invincible
		if ( invincible > 0 && (invincible/5)%2 == 0 ) { return; }

		Image currentFrame = marioFrames.getFrame(powerState, frameState, lastDirectionForward, shoot);

		int extraX = 0, extraY = 0;
		if (frameState == FrameState.JUMP || frameState == FrameState.RUN4) {
			extraX = 2 * GameFrame.pixelScale();
		} else if (frameState == FrameState.RUN1 || frameState == FrameState.RUN3) {
			extraY = GameFrame.pixelScale();
		}

		g2.drawImage(currentFrame, currentPos.getX() - extraX, currentPos.getY() + extraY, null);
	}

	private void drawFireballs(Graphics2D g2) {
		fireballs.forEach( fireball -> fireball.draw(g2, World.getInstance().getOffest()));
		fireballs = fireballs.stream().filter(fireball -> !fireball.isDone()).collect(Collectors.toList());
	}

	public void setKey(int action) {

		if (action == D_LEFT) {
			movingLeft = true;
			movingRight = false;
			if (canJumpAgain) lastDirectionForward = false;
		} else if (action == D_RIGHT) {
			movingRight = true;
			movingLeft = false;
			if (canJumpAgain) lastDirectionForward = true;
		} else if (action == BUTTON_B) {
			if (canJumpAgain) {
				running = true;				
			}
			if ( powerState == PowerState.FIRE && canShootAgain ) {
				canShootAgain = false;
				shoot = true;
			}
		} else if (action == BUTTON_A) {
			if (vector.getDy() == 0 && canJumpAgain && !jumpHeld) {
				jump = true;
				jumpHeld = true;
				jumpHeldState = 0;
			}
		}
	}

	public void unsetKey(int action) {
		if (action == D_LEFT) {
			movingLeft = false;
		} else if (action == D_RIGHT) {
			movingRight = false;
		} else if (action == BUTTON_B) {
			canShootAgain = true;
			running = false;
		} else if (action == BUTTON_A) {
			jumpHeld = false;
		}
	}

	private void move() {

		if (frameState != FrameState.DEAD) {

			handleJump();

			handleShoot();

			handleEnemies();

			handleItems();

			updateVector();

			currentPos.move(vector);

			handleCollisions();

			checkDead();

		} else {

			if (deadState++ > 200) {
				reset();
			} else if (deadState > 50) {
				vector.gravity();
				currentPos.move(vector);
			}

		}
	}

	private void handleShoot() {
		if ( shoot && oneShotAgo > waitShootShort && twoShotsAgo > waitShootLong ) {
			twoShotsAgo = oneShotAgo;
			oneShotAgo = 0;
			Pos firePos = currentPos.copy();
			firePos.moveRight(World.getInstance().getOffest());
			fireballs.add(new Fireball(firePos, lastDirectionForward));
			AudioController.play("/sound/fireball.wav");
		}
		shoot = false;
		oneShotAgo++;
		twoShotsAgo++;
	}

	private void handleItems() {
		if (World.getInstance().findMarioItemCollisions(getRect())) {
			powerUp();
		}
	}

	private void powerUp() {
		if (powerState == PowerState.SMALL) {
			powerState = PowerState.BIG;
			currentPos.moveDown(-GameFrame.blockDimension());
		} else if (powerState == PowerState.BIG) {
			powerState = PowerState.FIRE;
		}
	}

	public boolean isBig() {
		return !(powerState == PowerState.SMALL);
	}

	private void hit() {
		if (invincible <= 0) { // not invincible
			if (powerState == PowerState.SMALL) {
				dead();
			} else {
				powerState = PowerState.SMALL;
				currentPos.moveDown(GameFrame.blockDimension());
				invincible = 100;
			}
		}
	}

	private void handleEnemies() {
		Boolean[] hits = World.getInstance().findMarioEnemyCollisions(getRect());

		if (hits[0]) {
			hit();
		}
		if (hits[1]) {
			vector.jump(0.6);
		}
	}

	private void checkDead() {
		if ( currentPos.getY() > 250 * GameFrame.pixelScale() || Stats.getInstance().getTime() < 1 ) {
			dead();
		}
	}

	private void dead() {
		movingLeft = false;
		movingRight = false;
		canJumpAgain = false;
		shoot = false;
		frameState = FrameState.DEAD;
		deadState = 0;
		vector.stop();
		vector.jump();
		Stats.getInstance().pause();
		AudioController.stopTheme();
		AudioController.play("/sound/mario_die.wav");
	}

	public boolean isNotDead() {
		return frameState != FrameState.DEAD;
	}

	private void reset() {
		currentPos = originalPos.copy();
		World.getInstance().reset();
		frameState = FrameState.STAND;
		powerState = PowerState.SMALL;
		vector.stop();
		lastDirectionForward = true;
		canJumpAgain = true;
		jump = false;
		Stats.getInstance().resume();
		Stats.getInstance().reset();
		AudioController.startTheme();
	}

	private void handleCollisions() {

		CollisionResult collisionResult = World.getInstance().blockCollisions(getRect(), vector);

		currentPos.moveDown(collisionResult.getDy());
		currentPos.moveRight(collisionResult.getDx());

		canJumpAgain = collisionResult.isTopHit();

		if (collisionResult.isTopHit() && !movingLeft && !movingRight && isNotDead()) {
			if ( vector.getDx() == 0 ) {
				frameState = FrameState.STAND;
			} else if ( !isInRunState() ) {
				frameState = FrameState.RUN1;
			}
		}

		// stop moving if you hit something
		if (collisionResult.isTopHit() || collisionResult.isBottomHit()) {
			vector.hitY();
		}
		if (collisionResult.isLeftHit() || collisionResult.isRightHit()) {
			vector.hitX();
		}

		// keep mario within the left half of the screen
		if (currentPos.getX() < 2 * GameFrame.pixelScale()) {
			currentPos.setX(2 * GameFrame.pixelScale());
		} else if (currentPos.getX() > GameFrame.gameWidth() / 2) {
			World.getInstance().addOffset(currentPos.getX() - GameFrame.gameWidth() / 2);
			currentPos.setX(GameFrame.gameWidth() / 2);
		}

		if (invincible > 0) {
			invincible--;
		}
	}

	private boolean isInRunState() {
		return frameState == FrameState.RUN1 || frameState == FrameState.RUN2 ||
				frameState == FrameState.RUN3 || frameState == FrameState.RUN4;
	}


	private Rectangle2D getRect() {
		int width = (powerState == PowerState.SMALL ? 12 : 16) * GameFrame.pixelScale();
		int height = (powerState == PowerState.SMALL ? 1 : 2) * GameFrame.blockDimension();
		rect.setRect(currentPos.getX(), currentPos.getY(), width, height);
		return rect;
	}

	private void updateVector() {

		if (movingLeft) {
			vector.moveLeft(running);
			if ( vector.getDx() > 0 && canJumpAgain ) {
				vector.reduceSpeed();
				frameState = FrameState.TURN;
				lastDirectionForward = false;
			} else {
				setRunFrame();
				updateRunFrame();
			}
		} else if (movingRight) {
			vector.moveRight(running);
			if ( vector.getDx() < 0 && canJumpAgain ) {
				vector.reduceSpeed();
				frameState = FrameState.TURN;
				lastDirectionForward = true;
			} else {
				setRunFrame();
				updateRunFrame();
			}
		} else if ( canJumpAgain ) {
			vector.reduceSpeed();
			updateRunFrame();
		}
		vector.gravity();

		if ( vector.getDx() == 0 && canJumpAgain && !movingLeft && !movingRight ) {
			frameState = FrameState.STAND;
		}
	}

	private void handleJump() {
		int jumpHeldMax = 12;

		if (jump) {
			vector.jump();
			jump = false;
			canJumpAgain = false;
			frameState = FrameState.JUMP;
			AudioController.play( powerState == PowerState.SMALL ? "/sound/jump_small.wav" : "/sound/jump_super.wav");
		} else if (jumpHeld && jumpHeldState <= jumpHeldMax) {

			int jumpPassesToWait = 0;
			if (jumpHeldPasses <= jumpPassesToWait) {
				jumpHeldPasses++;
				return;
			}

			jumpHeldPasses = 0;
			vector.jumpHold();
			jumpHeldState++;
		}
	}

	enum FrameState {
		STAND, JUMP, RUN1, RUN2, RUN3, RUN4, TURN, DEAD
	}

	enum PowerState {
		SMALL, BIG, FIRE
	}

}