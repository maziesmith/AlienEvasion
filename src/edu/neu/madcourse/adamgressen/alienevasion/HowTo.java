/***
 * The instructions activity for the game, Alien Evasion
 ***/
package edu.neu.madcourse.adamgressen.alienevasion;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import edu.neu.madcourse.adamgressen.R;

public class HowTo extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Set layout
		setContentView(R.layout.alien_evasion_howto);
	}

	@Override
	protected void onResume() {
		super.onResume();

		Sounds.playMusic(this, R.raw.alien_evasion_main);
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		Sounds.stop(this);
	}
}