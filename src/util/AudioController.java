package util;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import window.GameFrame;

import java.io.IOException;

public class AudioController {

	static {
		GameFrame.addPeriodicTask(AudioController::loopTheme);
	}

	private static AudioStream theme;

	public static AudioStream play(String file) {
		try {
			AudioStream audioStream = new AudioStream(AudioController.class.getResourceAsStream(file));
			AudioPlayer.player.start(audioStream);
			return audioStream;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void stop(AudioStream audioStream) {
		AudioPlayer.player.stop(audioStream);
	}

	public static void stopTheme() {
		stop(theme);
	}

	public static void startTheme() {
		theme = play("/sound/mario theme.wav");
	}

	private static void loopTheme() {
		try {
			if (theme != null && theme.available() < 3000) {
				AudioStream themeToStop = theme;
				startTheme();
				stop(themeToStop);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
