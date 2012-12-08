package edu.neu.madcourse.adamgressen.alienevasion;

import android.content.Context;
import android.media.MediaPlayer;

public class Sounds {
	private static MediaPlayer music = new MediaPlayer();
	private static MediaPlayer sound = new MediaPlayer();

	/** Stop old sound and start new one */
	public static void playSound(Context context, int resource) {
		stop(context, sound);

		// Play sound only if not disabled in preferences
		if (Settings.getSound(context)) {
			sound = MediaPlayer.create(context, resource);
			sound.setLooping(false);
			sound.start();
		}
	}

	public static void playMusic(Context context, int resource) {
		stop(context, music);

		// Start music only if not disabled in preferences
		if (Settings.getMusic(context)) {
			music = MediaPlayer.create(context, resource);
			music.setLooping(true);
			music.start();
		}
	}

	/** Stop the given media player */
	public static void stop(Context context, MediaPlayer m) { 
		if (m != null) {
			m.release();
			m.reset();
		}
	}

	/** Stop both music and sound */
	public static void stop(Context context) {
		Sounds.stop(context, music);
		Sounds.stop(context, sound);
	}
}