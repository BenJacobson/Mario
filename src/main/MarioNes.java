package main;

import window.GameFrame;

import java.awt.*;

// starts the main frame of game
public class MarioNes {

	public static int PIXEL_SCALE = 4;

	public static void main(String args[]) {
		EventQueue.invokeLater( () -> new GameFrame() );
	}
}
