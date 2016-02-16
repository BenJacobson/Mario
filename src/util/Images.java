package util;

import main.MarioNes;
import window.GameCanvas;
import window.GameFrame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Images {

	private static final String imageFolder = "/pic/";

	private static Image initFrame(String fileName) {

		try {
			Image frame = ImageIO.read(new BufferedInputStream(Images.class.getResourceAsStream(imageFolder + fileName)));
			return frame.getScaledInstance(frame.getWidth(null)*GameFrame.pixelScale(), frame.getHeight(null)*GameFrame.pixelScale(), 0);
		} catch (IOException e) {
			System.out.println("Could not load " + fileName);
 			System.exit(0);
			return null;
		}
	}

	// block images
	public static final Image ground = initFrame("block_ground.png");
	public static final Image brick = initFrame("block_brick.png");
	public static final Image invisible = initFrame("block_invisible.png");
	public static final Image pipeBottom = initFrame("pipe_bottom.png");
	public static final Image pipeTop = initFrame("pipe_top.png");
	public static final Image question_normal = initFrame("block_question_normal.png");
	public static final Image question_dark = initFrame("block_question_dark.png");
	public static final Image question_brown = initFrame("block_question_brown.png");
	public static final Image used = initFrame("block_used.png");
	public static final Image square = initFrame("block_square.png");

	// coin images
	public static final Image coin_normal = initFrame("coin_normal.png");
	public static final Image coin_dark = initFrame("coin_dark.png");
	public static final Image coin_brown = initFrame("coin_brown.png");

	// mario small images
	public static final Image stand_frame = initFrame("mario_stand.png");
	public static final Image jump_frame = initFrame("mario_jump.png");
	public static final Image stand_frame_back = initFrame("mario_stand_back.png");
	public static final Image jump_frame_back = initFrame("mario_jump_back.png");
	public static final Image run_frame_13 = initFrame("mario_run_13.png");
	public static final Image run_frame_13_back = initFrame("mario_run_13_back.png");
	public static final Image run_frame_2 = initFrame("mario_run_2.png");
	public static final Image run_frame_2_back = initFrame("mario_run_2_back.png");
	public static final Image run_frame_4 = initFrame("mario_run_4.png");
	public static final Image run_frame_4_back = initFrame("mario_run_4_back.png");
	public static final Image turn_frame = initFrame("mario_turning.png");
	public static final Image turn_frame_back = initFrame("mario_turning_back.png");
	public static final Image dead_frame = initFrame("mario_dead.png");

	// mario big images
	public static final Image stand_frame_big = initFrame("mario_stand_big.png");
	public static final Image stand_frame_back_big = initFrame("mario_stand_back_big.png");

	// goomba images
	public static final Image goombaLeft = initFrame("enemy_goomba_left.png");
	public static final Image goombaRight = initFrame("enemy_goomba_right.png");
	public static final Image goombaSquished = initFrame("enemy_goomba_squished.png");
	public static final Image goombaFlipped = initFrame("enemy_goomba_flipped.png");

	// item images
	public static final Image mushroom = initFrame("item_mushroom.png");
	public static final Image fireflower = initFrame("item_fireflower.png");

	// background images
	public static final Image hill_large = initFrame("hill_large.png");
	public static final Image hill_small = initFrame("hill_small.png");

	//flagpole
	public static final Image flagpole_pole = initFrame("flagpole_pole.png");
	public static final Image flagpole_ball = initFrame("flagpole_ball.png");
	public static final Image flagpole_flag = initFrame("flagpole_flag.png");
}
