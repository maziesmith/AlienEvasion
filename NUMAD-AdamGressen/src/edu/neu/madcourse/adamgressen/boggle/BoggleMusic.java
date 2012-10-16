/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband3 for more book information.
***/
package edu.neu.madcourse.adamgressen.boggle;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;

public class BoggleMusic {
   private static List<MediaPlayer> mp = new LinkedList<MediaPlayer>();

   /** Stop old song and start new one */
   
   public static void play(Context context, int resource, boolean loop) {
	   // Start music only if not disabled in preferences
	   if (BogglePrefs.getMusic(context)) {
		   MediaPlayer newMp = MediaPlayer.create(context, resource); 
		   newMp.setLooping(loop);
		   mp.add(newMp);
		   newMp.start();
	  }
   }
   
   public static void play(Context context, int resource) {
	   stop(context);
	   
	   play(context, resource, true);
   }
   

   /** Stop the music */
   public static void stop(Context context) { 
      if (mp != null) {
    	  for (MediaPlayer m : mp) {
    		  m.stop();
    		  m.release();
    		  m = null;
    	  }
      }
      mp.clear();
   }
}
