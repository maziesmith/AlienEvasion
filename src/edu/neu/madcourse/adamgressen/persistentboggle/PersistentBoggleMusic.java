/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband3 for more book information.
***/
package edu.neu.madcourse.adamgressen.persistentboggle;

import android.content.Context;
import android.media.MediaPlayer;

public class PersistentBoggleMusic {
   private static MediaPlayer music = new MediaPlayer();
   private static MediaPlayer sound = new MediaPlayer();

   /** Stop old sound and start new one */
   public static void playSound(Context context, int resource) {
	   stop(context, sound);
	   
	   // Play sound only if not disabled in preferences
	   if (PersistentBogglePrefs.getSound(context)) {
		   sound = MediaPlayer.create(context, resource);
		   sound.setLooping(false);
		   sound.start();
	   }
   }
   
   public static void playMusic(Context context, int resource) {
	   stop(context, music);
	   
	   // Start music only if not disabled in preferences
	   if (PersistentBogglePrefs.getMusic(context)) {
		   music = MediaPlayer.create(context, resource);
		   music.setLooping(true);
		   music.start();
	   }
   }

   /** Stop the given media player */
   public static void stop(Context context, MediaPlayer m) { 
      if (m != null) {
    	  m.release();
    	  m = null;
      }
   }
   
   /** Stop both music and sound */
   public static void stop(Context context) {
	   PersistentBoggleMusic.stop(context, music);
	   PersistentBoggleMusic.stop(context, sound);
   }
}