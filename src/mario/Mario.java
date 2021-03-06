package mario;

import util.input.GameController;
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
	private boolean lastPowerChangeDown = false;
	private boolean powerChange = false;
	private boolean hitEnemyLastFrame = false;

	private int numberOfPasses = 0;
	private int jumpHeldState = 0;
	private int jumpHeldPasses = 0;
	private int deadState = 0;
	private int invincible = 0;
	private int powerChangeState = 0;

	private final int waitShootShort = 5;
	private final int waitShootLong = 40;
	private int oneShotAgo = waitShootShort;
	private int twoShotsAgo = waitShootLong;

	private FrameState frameState = FrameState.STAND;
	private PowerState powerState = PowerState.SMALL;

	private final MarioFrames marioFrames = new MarioFrames();
	private List<Fireball> fireballs = new ArrayList<>();

	private Mario() {
		GameFrame.addPeriodicTask(this::move);
		addControllerEvents();
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
		if ( invincible > 0 && invincible%2 == 0 ) { return; }

		PowerState imagePowerState = powerChange ? getPowerChangePowerState() : powerState;
		Image currentFrame = marioFrames.getFrame(imagePowerState, frameState, lastDirectionForward, shoot);

		int extraX = 0, extraY = 0;
		if (frameState == FrameState.JUMP || frameState == FrameState.RUN4) {
			extraX += 2 * GameFrame.pixelScale();
		} else if (frameState == FrameState.RUN1 || frameState == FrameState.RUN3) {
			extraY += GameFrame.pixelScale();
		}
		if (imagePowerState != PowerState.SMALL) {
			extraY -= GameFrame.blockDimension();
		}

		g2.drawImage(currentFrame, currentPos.getX() - extraX, currentPos.getY() + extraY, null);
	}

	private PowerState getPowerChangePowerState() {
		if (lastPowerChangeDown) {
			return powerChangeState%2 == 0 ? PowerState.BIG : PowerState.SMALL;
		} else if ( powerState == PowerState.BIG ) {
			return powerChangeState%2 == 0 ? PowerState.BIG : PowerState.SMALL;
		} else {
			return powerChangeState%2 == 0 ? PowerState.BIG : PowerState.FIRE;
		}
	}

	private void drawFireballs(Graphics2D g2) {
		fireballs.forEach( fireball -> fireball.draw(g2, World.getInstance().getOffest()));
		fireballs = fireballs.stream().filter(fireball -> !fireball.isDone()).collect(Collectors.toList());
	}

	private void move() {

		if ( powerChange ) { // everything pauses and mario flashes as he grows big or shrinks small
			if ( --powerChangeState == 0 ) {
				powerChange = false;
				Stats.getInstance().resume();
				if (lastPowerChangeDown) {
					invincible = 100;
				}
			}
		} else if (frameState != FrameState.DEAD) { // normal mario movements and collisions handled

			handleJump();

			handleShoot();

			handleEnemies();

			handleItems();

			updateVector();

			currentPos.move(vector);

			handleCollisions();

			checkDead();

		} else { // mario is dead, allow time for the death sequence before respawn or game over

			if (deadState++ > 200) {
				reset();
			} else if (deadState > 50) {
				vector.gravity();
				currentPos.move(vector);
			}

		}
	}

	/*
	this function shoots Mario's fireballs when he is big
	it
	 */
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
		if (powerState != PowerState.FIRE) {
			if (powerState == PowerState.SMALL) {
				powerState = PowerState.BIG;
			} else if (powerState == PowerState.BIG) {
				powerState = PowerState.FIRE;
			}
			powerChange = true;
			powerChangeState = 50;
			lastPowerChangeDown = false;
			Stats.getInstance().pause();
			AudioController.play("/sound/powerup_eaten.wav");
		}
	}

	public boolean isBig() {
		return powerState != PowerState.SMALL;
	}

	private void hit() {
		if (invincible <= 0) { // if not invincible
			if (powerState == PowerState.SMALL) { // if you get hit when small,you die
				dead();
			} else { // if you get hit when big or fire, you become small
				powerState = PowerState.SMALL;
				powerChange = true;
				powerChangeState = 50;
				lastPowerChangeDown = true;
				Stats.getInstance().pause();
				AudioController.play("/sound/pipe-powerdown.wav");
			}
		}
	}

	private void handleEnemies() {
		if (invincible>0)
			return;

		Boolean[] hits = World.getInstance().findMarioEnemyCollisions(getRect(), vector.getDy() > 0);
		boolean enemyHitMario = hits[0];
		boolean marioJumpsOnEnemy = hits[1];

		if (!hitEnemyLastFrame && enemyHitMario) {
			hit();
			hitEnemyLastFrame = true;
		} else {
			hitEnemyLastFrame = false;
		}
		if (marioJumpsOnEnemy) {
			double bounceHeight = 0.6;
			vector.jump(bounceHeight);
		}
	}

	private void checkDead() {
		if ( currentPos.getY() > 260 * GameFrame.pixelScale() || Stats.getInstance().getTime() < 1 ) {
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

	public boolean isPaused() {
		return frameState == FrameState.DEAD || powerChange;
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

		CollisionResult collisionResult = World.getInstance().blockCollisions(getRect(), vector, true);

		currentPos.moveDown(collisionResult.getDy());
		// Prevent the collision from shifting mario if he is hitting the bottom of things with his head
		// It looks funny if mario hits the bottom of a row of blocks and gets shifted around
		if ( !collisionResult.isBottomHit() ) {
			currentPos.moveRight(collisionResult.getDx());
		}


		canJumpAgain = collisionResult.isTopHit();

		if (collisionResult.isTopHit() && !movingLeft && !movingRight && !isPaused()) {
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
		if ( (collisionResult.isLeftHit() && vector.getDx() > 0) || (collisionResult.isRightHit() && vector.getDx() < 0) ) {
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
		int y = powerState == PowerState.SMALL ? currentPos.getY() : currentPos.getY() - GameFrame.blockDimension();
		int width = (powerState == PowerState.SMALL ? 12 : 16) * GameFrame.pixelScale();
		int height = (powerState == PowerState.SMALL ? 1 : 2) * GameFrame.blockDimension();
		rect.setRect(currentPos.getX(), y, width, height);
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

	/////////////////////////////////////////////////////////
	// Controller Events
	/////////////////////////////////////////////////////////
	private void addControllerEvents() {
		addAButtonListener();
		addBButtonListener();
		addLeftButtonListener();
		addRightButtonListener();
	}

	private void addRightButtonListener() {
		GameController.getInstance().addRightButtonListener(new GameController.ControllerEvent() {
			@Override
			public void set() {
				if ( !isPaused() ) {
					movingRight = true;
					movingLeft = false;
					if (canJumpAgain) lastDirectionForward = true;
				}
			}

			@Override
			public void unset() {
				movingRight = false;
			}
		});
	}

	private void addLeftButtonListener() {
		GameController.getInstance().addLeftButtonListener(new GameController.ControllerEvent() {
			@Override
			public void set() {
				if ( !isPaused() ) {
					movingLeft = true;
					movingRight = false;
					if (canJumpAgain) lastDirectionForward = false;
				}
			}

			@Override
			public void unset() {
				movingLeft = false;
			}
		});
	}

	private void addBButtonListener() {
		GameController.getInstance().addBButtonListener(new GameController.ControllerEvent() {
			@Override
			public void set() {
				if ( isPaused() ) {
					return;
				}
				if (canJumpAgain) {
					running = true;
				}
				if ( powerState == PowerState.FIRE && canShootAgain ) {
					canShootAgain = false;
					shoot = true;
				}
			}

			@Override
			public void unset() {
				canShootAgain = true;
				running = false;
			}
		});
	}

	private void addAButtonListener() {
		GameController.getInstance().addAButtonListener(new GameController.ControllerEvent() {
			@Override
			public void set() {
				if ( !isPaused() && vector.getDy() == 0 && canJumpAgain && !jumpHeld) {
					jump = true;
					jumpHeld = true;
					jumpHeldState = 0;
				}
			}

			@Override
			public void unset() {
				jumpHeld = false;
			}
		});
	}
	/////////////////////////////////////////////////////////////////////

	enum FrameState {
		STAND, JUMP, RUN1, RUN2, RUN3, RUN4, TURN, DEAD
	}

	enum PowerState {
		SMALL, BIG, FIRE
	}

}