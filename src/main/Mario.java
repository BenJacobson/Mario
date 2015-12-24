package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Mario {

	private static Mario instance;

	public static Mario getInstance() {
		if ( instance == null ) {
			instance = new Mario();
		}
		return instance;
	}

	public static int LEFT = 37;
	public static int RIGHT = 39;
	public static int SPACE = 32;

	private boolean movingLeft = false;
	private boolean movingRight = false;
	private boolean jump = false;
	private  boolean jumpAgain = false;

	private final Image frame = initFrames();

	Pos currentPos = new Pos(100, 100);
	Vector vector = new Vector();

	private Mario() {}

	private Image initFrames() {
		final String filePath = "lib" + File.separator + "pic" + File.separator + "mario_stand.png";
		try {
			Image frame = ImageIO.read(new File(filePath));
			return frame.getScaledInstance(frame.getWidth(null)*MarioNes.PIXEL_SCALE, frame.getHeight(null)*MarioNes.PIXEL_SCALE, 0);
		} catch (IOException e) {
			System.out.println("Could not load " + filePath);
			System.exit(0);
			return null;
		}
	}

	public void draw(Graphics2D g2) {
		g2.drawImage(frame, currentPos.getX(), currentPos.getY(), frame.getWidth(null), frame.getHeight(null), null);
	}

	public void setDirection(int action) {
		if ( action == LEFT ) {
			movingLeft = true;
			movingRight = false;
		} else if ( action == RIGHT ) {
			movingRight = true;
			movingLeft = false;
		} else if ( action == SPACE ) {
			if ( vector.getDy() == 0 && jumpAgain ) {
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
	}

	private void handleCollisions() {
		jumpAgain |= World.getInstance().collision(currentPos, frame.getWidth(null), frame.getHeight(null), vector);

		if ( currentPos.getX() < 2*MarioNes.PIXEL_SCALE ) {
			currentPos.setX( 5 );
		} else if ( currentPos.getX() > 256*MarioNes.PIXEL_SCALE/2 ) {
			World.getInstance().addOffset( currentPos.getX() - 256*MarioNes.PIXEL_SCALE/2 );
			currentPos.setX( 256*MarioNes.PIXEL_SCALE/2 );
		}
	}

	private void updateVector() {
		if ( movingLeft ) {
			vector.moveLeft();
		} else if ( movingRight ) {
			vector.moveRight();
		} else {
			vector.reduceSpeed();
		}
		vector.gravity();
	}

	private void handleJump() {
		if ( jump ) {
			vector.jump();
			jump = false;
			jumpAgain = false;
		}
	}
}
