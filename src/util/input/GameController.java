package util.input;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import window.GameFrame;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class GameController extends KeyAdapter {

	private static GameController instance;

	public static GameController getInstance() {
		if (instance == null) {
			instance = new GameController();
		}
		return instance;
	}

	private Controller controller;
	private Component[] components;

	private Button Button_A = Button.UNSET;
	private Button Button_B = Button.UNSET;
	private Button Button_X = Button.UNSET;
	private Button Button_Y = Button.UNSET;
	private Button Button_Start = Button.UNSET;
	private Button Button_Select = Button.UNSET;
	private Button Button_Up = Button.UNSET;
	private Button Button_Down = Button.UNSET;
	private Button Button_Left = Button.UNSET;
	private Button Button_Right = Button.UNSET;

	private final int Key_LEFT = 37;
	private final int Key_RIGHT = 39;
	private final int Key_A = 32;
	private final int Key_B = 88;
	
	private List<ControllerEvent> buttonAListeners = new ArrayList<>();
	private List<ControllerEvent> buttonBListeners = new ArrayList<>();
	private List<ControllerEvent> buttonXListeners = new ArrayList<>();
	private List<ControllerEvent> buttonYListeners = new ArrayList<>();
	private List<ControllerEvent> buttonStartListeners = new ArrayList<>();
	private List<ControllerEvent> buttonSelectListeners = new ArrayList<>();
	private List<ControllerEvent> buttonUpListeners = new ArrayList<>();
	private List<ControllerEvent> buttonDownListeners = new ArrayList<>();
	private List<ControllerEvent> buttonLeftListeners = new ArrayList<>();
	private List<ControllerEvent> buttonRightListeners = new ArrayList<>();

	private GameController() {
		if ( hasGamepad() ) {
			useGamepad();
		}
	}
	
	public void addAButtonListener(ControllerEvent e) {
		buttonAListeners.add(e);
	}

	public void addBButtonListener(ControllerEvent e) {
		buttonBListeners.add(e);
	}

	public void addXButtonListener(ControllerEvent e) {
		buttonXListeners.add(e);
	}

	public void addYButtonListener(ControllerEvent e) {
		buttonYListeners.add(e);
	}

	public void addStartButtonListener(ControllerEvent e) {
		buttonStartListeners.add(e);
	}

	public void addSelectButtonListener(ControllerEvent e) {
		buttonSelectListeners.add(e);
	}

	public void addUpButtonListener(ControllerEvent e) {
		buttonUpListeners.add(e);
	}

	public void addDownButtonListener(ControllerEvent e) {
		buttonDownListeners.add(e);
	}

	public void addLeftButtonListener(ControllerEvent e) {
		buttonLeftListeners.add(e);
	}

	public void addRightButtonListener(ControllerEvent e) {
		buttonRightListeners.add(e);
	}

	private void handleA(Button pollState) {
		if ( Button_A == Button.SET && pollState == Button.UNSET ) {
			Button_A = Button.UNSET;
			buttonAListeners.forEach(ControllerEvent::unset);
		} else if ( Button_A == Button.UNSET && pollState == Button.SET ) {
			Button_A = Button.SET;
			buttonAListeners.forEach(ControllerEvent::set);
		}
	}

	private void handleB(Button pollState) {
		if ( Button_B == Button.SET && pollState == Button.UNSET ) {
			Button_B = Button.UNSET;
			buttonBListeners.forEach(ControllerEvent::unset);
		} else if ( Button_B == Button.UNSET && pollState == Button.SET ) {
			Button_B = Button.SET;
			buttonBListeners.forEach(ControllerEvent::set);
		}
	}

	private void handleX(Button pollState) {
		if ( Button_X == Button.SET && pollState == Button.UNSET ) {
			Button_X = Button.UNSET;
			buttonXListeners.forEach(ControllerEvent::unset);
		} else if ( Button_X == Button.UNSET && pollState == Button.SET ) {
			Button_X = Button.SET;
			buttonXListeners.forEach(ControllerEvent::set);
		}
	}

	private void handleY(Button pollState) {
		if ( Button_Y == Button.SET && pollState == Button.UNSET ) {
			Button_Y = Button.UNSET;
			buttonYListeners.forEach(ControllerEvent::unset);
		} else if ( Button_Y == Button.UNSET && pollState == Button.SET ) {
			Button_Y = Button.SET;
			buttonYListeners.forEach(ControllerEvent::set);
		}
	}

	private void handleStart(Button pollState) {
		if ( Button_Start == Button.SET && pollState == Button.UNSET ) {
			Button_Start = Button.UNSET;
			buttonStartListeners.forEach(ControllerEvent::unset);
		} else if ( Button_Start == Button.UNSET && pollState == Button.SET ) {
			Button_Start = Button.SET;
			buttonStartListeners.forEach(ControllerEvent::set);
		}
	}

	private void handleSelect(Button pollState) {
		if ( Button_Select == Button.SET && pollState == Button.UNSET ) {
			Button_Select = Button.UNSET;
			buttonSelectListeners.forEach(ControllerEvent::unset);
		} else if ( Button_Select == Button.UNSET && pollState == Button.SET ) {
			Button_Select = Button.SET;
			buttonSelectListeners.forEach(ControllerEvent::set);
		}
	}

	private void handleUp(Button pollState) {
		if ( Button_Up == Button.SET && pollState == Button.UNSET ) {
			Button_Up = Button.UNSET;
			buttonUpListeners.forEach(ControllerEvent::unset);
		} else if ( Button_Up == Button.UNSET && pollState == Button.SET ) {
			Button_Up = Button.SET;
			buttonUpListeners.forEach(ControllerEvent::set);
		}
	}

	private void handleDown(Button pollState) {
		if ( Button_Down == Button.SET && pollState == Button.UNSET ) {
			Button_Down = Button.UNSET;
			buttonDownListeners.forEach(ControllerEvent::unset);
		} else if ( Button_Down == Button.UNSET && pollState == Button.SET ) {
			Button_Down = Button.SET;
			buttonDownListeners.forEach(ControllerEvent::set);
		}
	}

	private void handleLeft(Button pollState) {
		if ( Button_Left == Button.SET && pollState == Button.UNSET ) {
			Button_Left = Button.UNSET;
			buttonLeftListeners.forEach(ControllerEvent::unset);
		} else if ( Button_Left == Button.UNSET && pollState == Button.SET ) {
			Button_Left = Button.SET;
			buttonLeftListeners.forEach(ControllerEvent::set);
		}
	}

	private void handleRight(Button pollState) {
		if ( Button_Right == Button.SET && pollState == Button.UNSET ) {
			Button_Right = Button.UNSET;
			buttonRightListeners.forEach(ControllerEvent::unset);
		} else if ( Button_Right == Button.UNSET && pollState == Button.SET ) {
			Button_Right = Button.SET;
			buttonRightListeners.forEach(ControllerEvent::set);
		}
	}

	private void pollAButton() {
		float A_val = components[2].getPollData();
		handleA( A_val == 1.0f ? Button.SET: Button.UNSET );
	}

	private void pollBButton() {
		float B_val = components[3].getPollData();
		handleB( B_val == 1.0f ? Button.SET: Button.UNSET );
	}

	private void pollXAxis() {
		float xAxis = components[1].getPollData();
		handleLeft( xAxis == -1.0f ? Button.SET : Button.UNSET );
		handleRight( xAxis == 1.0f ? Button.SET : Button.UNSET );
	}
	
	private boolean hasGamepad() {
		// Load libraries
		try {
			loadDLL("jinput-dx8_64.dll");
			loadDLL("jinput-raw_64.dll");
			System.setProperty( "java.library.path", "./controller/" );
			Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
			fieldSysPath.setAccessible( true );
			fieldSysPath.set( null, null );
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return false;
		}
		// Connect to controller
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		for (Controller c : controllers) {
			if (c.getName().toLowerCase().contains("gamepad")) {
				System.out.println("Found Gamepad");
				controller = c;
				components= c.getComponents();
				return true;
			}
		}
		return false;
	}

	private void loadDLL(String name) throws Exception {
		// If the file already exists, return
		File targetFile = new File("./controller/" + name);
		if ( targetFile.exists() ) {
			return;
		}
		// make the file
		targetFile.getParentFile().mkdirs();
		targetFile.createNewFile();

		// Read the file from the JAR to the destination file
		InputStream inStream = GameController.class.getResourceAsStream("/jinput/" + name);
		OutputStream outStream = new FileOutputStream(targetFile);
		int read = 0;
		byte[] bytes = new byte[1024];
		while ((read = inStream.read(bytes)) != -1) {
			outStream.write(bytes, 0, read);
		}
		inStream.close();
		outStream.close();
	}

	private void useGamepad() {
		GameFrame.addPeriodicTask(() -> {
			controller.poll();
			pollAButton();
			pollBButton();
			pollXAxis();
		});
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case Key_A:
				handleA(Button.SET);
				break;
			case Key_B:
				handleB(Button.SET);
				break;
			case Key_LEFT:
				handleLeft(Button.SET);
				break;
			case Key_RIGHT:
				handleRight(Button.SET);
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case Key_A:
				handleA(Button.UNSET);
				break;
			case Key_B:
				handleB(Button.UNSET);
				break;
			case Key_LEFT:
				handleLeft(Button.UNSET);
				break;
			case Key_RIGHT:
				handleRight(Button.UNSET);
				break;
		}
	}
	
	public interface ControllerEvent {
		void set();
		void unset();
	}

	private enum Button {
		SET, UNSET
	}
}
