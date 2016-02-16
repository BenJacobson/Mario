package util;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.IOException;

public class AudioController {

	private static AudioStream theme;

	public static AudioStream play(String file) {
		try {
			AudioStream audioStream = new AudioStream(AudioController.class.getResourceAsStream(file));
			AudioPlayer.player.start(audioStream);
			return audioStream;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}

	public static void stop(AudioStream audioStream) {
		AudioPlayer.player.stop(audioStream);
	}

	public static void stopTheme() {
		stop(theme);
	}

	public static void startTheme() {
		theme = play("/sound/wav/mario theme.wav");
	}

	public static void loopTheme() {
		try {
			if (theme != null && theme.available() < 3000) {
				stopTheme();
				startTheme();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
