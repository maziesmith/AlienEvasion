/***
 * The main activity for the game, Alien Evasion
 ***/
package edu.neu.madcourse.adamgressen.alienevasion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import edu.neu.madcourse.adamgressen.R;
import edu.neu.mobileClass.PhoneCheckAPI;

public class Main extends Activity implements OnClickListener {

	View startButton;
	View evasionsButton;
	View achievementsButton;
	View howtoButton;
	View ackButton;
	View exitButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set layout
		setContentView(R.layout.alien_evasion_main);

		// Set up click listeners for all the buttons
		startButton = findViewById(R.id.start_button);
		startButton.setOnClickListener(this);
		startButton.getBackground().setAlpha(120);
		evasionsButton = findViewById(R.id.evasions_button);
		evasionsButton.setOnClickListener(this);
		evasionsButton.getBackground().setAlpha(120);
		achievementsButton = findViewById(R.id.achievements_button);
		achievementsButton.setOnClickListener(this);
		achievementsButton.getBackground().setAlpha(120);
		howtoButton = findViewById(R.id.howto_button);
		howtoButton.setOnClickListener(this);
		howtoButton.getBackground().setAlpha(120);
		ackButton = findViewById(R.id.acknowledgements_button);
		ackButton.setOnClickListener(this);
		ackButton.getBackground().setAlpha(120);
		exitButton = findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);
		exitButton.getBackground().setAlpha(120);
		
		PhoneCheckAPI.doAuthorization(this);
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

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start_button:
			startGame();
			break;
		case R.id.evasions_button:
			startActivity(new Intent(this, Evasions.class));
			break;
		case R.id.achievements_button:
			startActivity(new Intent(this, Achievements.class));
			break;
		case R.id.howto_button:
			startActivity(new Intent(this, HowTo.class));
			break;
		case R.id.acknowledgements_button:
			new AlertDialog.Builder(this)
			.setMessage(R.string.acknowledgements)
			.setTitle(R.string.ack_label)
			.setCancelable(true)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int id) {  
					dialog.dismiss();
				}
			}).show();
			break;
		case R.id.exit_button:
			finish();
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.alien_evasion_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent(this, Settings.class));
			return true;
		case R.id.about:
			new AlertDialog.Builder(this)
			.setMessage(R.string.about_message)
			.setTitle(R.string.alien_evasion_about_title)
			.setCancelable(true)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {  
					dialog.dismiss();
				}
			}).show();
			return true;
		}
		return false;
	}

	/** Start a new game */
	private void startGame() {
		Sounds.stop(this);
		startActivity(new Intent(this, Evade.class));
	}
}