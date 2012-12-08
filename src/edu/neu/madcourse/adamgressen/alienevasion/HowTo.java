/***
 * The instructions activity for the game, Alien Evasion
 ***/
package edu.neu.madcourse.adamgressen.alienevasion;

import android.app.Activity;
import android.os.Bundle;
import edu.neu.madcourse.adamgressen.R;

public class HowTo extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set layout
		setContentView(R.layout.alien_evasion_howto);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}