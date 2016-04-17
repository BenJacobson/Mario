package main;

import window.GameFrame;

import java.awt.*;

// starts the main frame of game
public class MarioNes {
	public static void main(String args[]) {
		EventQueue.invokeLater( GameFrame::new );
	}
}
