package enemy;


import mario.Mario;
import mechanics.Pos;
import mechanics.Vector;
import window.GameCanvas;
import world.World;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Goomba implements Enemy {

	private Pos pos;
	private Vector vector = new Vector();

	private Image imageLeft;
	private Image imageRight;

	boolean useLeftImage;
	private int passesBetweenFrames = 5;
	private int numberOfPasses = 0;

	public Goomba(Pos pos) {
		this.pos = pos;
		imageLeft = GameCanvas.initFrame(GameCanvas.imageFolder + "enemy_goomba_left.png");
		imageRight = GameCanvas.initFrame(GameCanvas.imageFolder + "enemy_goomba_right.png");
		vector.moveLeft();
		vector.moveLeft();
	}


	@Override
	public void draw(Graphics2D g2, int offset) {
		update();
		Image image = useLeftImage ? imageLeft : imageRight;
		g2.drawImage(image, pos.getX()-offset, pos.getY(), null);
	}

	@Override
	public int getX(int offset) {
		return pos.getX() - offset;
	}

	@Override
	public Rectangle2D getRect(int offset) {
		return new Rectangle2D.Double(pos.getX()-offset, pos.getY(), imageLeft.getWidth(null), imageLeft.getHeight(null));
	}

	private void update() {
		updateFrame();
		vector.gravity();
		pos.move(vector);
		World.getInstance().collision(pos, imageLeft.getWidth(null), imageLeft.getHeight(null), vector);
		// Mario.getInstance().
	}

	private void updateFrame() {
		numberOfPasses++;

		if ( numberOfPasses > passesBetweenFrames ) {
			numberOfPasses = 0;
			useLeftImage = !useLeftImage;
		}
	}
}
