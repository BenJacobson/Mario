package main;

import java.awt.*;

public class MarioNes {

	public static int PIXEL_SCALE = 4;

	public static void main(String args[]) {
		EventQueue.invokeLater( () -> new GameFrame() );
	}
}
