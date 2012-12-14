/***
 * The main activity for the game, Alien Evasion
 ***/
package edu.neu.madcourse.adamgressen.alienevasion;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import edu.neu.madcourse.adamgressen.R;
import edu.neu.mobileClass.PhoneCheckAPI;

public class Main extends Activity implements OnClickListener {

	View startButton;
	View evasionsButton;
	View achievementsButton;
	View howtoButton;
	View exitButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

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
		exitButton = findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);
		exitButton.getBackground().setAlpha(120);

		PhoneCheckAPI.doAuthorization(this);

		if (!EvadeLocationManager.isGPSAvailable(this)) {
			new AlertDialog.Builder(this)
			.setMessage("Please enable GPS to use this application.")
			.setTitle("GPS")
			.setCancelable(true)
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			})
			.setPositiveButton("GPS Settings", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int id) {
					startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
					dialog.dismiss();
				}
			}).show();
		}
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
			List<String> storedEvasions = Evasions.readStoredEvasions(this);
			String currentEvasion = Evasions.getSavedEvasionName(this);
			storedEvasions.remove(currentEvasion);

			// Alert the user if there are no previous evasions
			if (storedEvasions.isEmpty()) {
				new AlertDialog.Builder(this)
				.setTitle("No Evasions")
				.setMessage("You don't have any previous Evasions.")
				.setNegativeButton(R.string.start_label, new DialogInterface.OnClickListener() {  
					public void onClick(DialogInterface dialog, int id) {
						startGame();
						dialog.dismiss();
					}
				})
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				}).create().show();
			}
			else
				startActivity(new Intent(this, Evasions.class));
			break;
		case R.id.achievements_button:
			startActivity(new Intent(this, Achievements.class));
			break;
		case R.id.howto_button:
			startActivity(new Intent(this, HowTo.class));
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
		case R.id.ack:
			new AlertDialog.Builder(this)
			.setTitle(R.string.ack_label)
			.setMessage(R.string.acknowledgements)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			}).create().show();
			/*
			showDialog(this,
					R.string.ack_label,
					R.string.acknowledgements,
					R.drawable.alien_evasion_logo);
			*/
			return true;
		case R.id.about:
			new AlertDialog.Builder(this)
			.setTitle(R.string.alien_evasion_about_title)
			.setMessage(R.string.about_message)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			}).create().show();
			/*
			showDialog(this,
					R.string.alien_evasion_about_title,
					R.string.about_message,
					R.drawable.alien_evasion_logo);
			*/
			return true;
		}
		return false;
	}

	public static void showDialog(Context context, int title, int text, int imageID) {
		// Custom dialog
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.alien_evasion_dialog);
		dialog.setTitle(title);

		// Set text of custom dialog
		TextView textView = (TextView) dialog.findViewById(R.id.dialog_text);
		textView.setText(text);

		// Set image for custom dialog
		ImageView image = (ImageView) dialog.findViewById(R.id.dialog_image);
		image.setImageResource(imageID);

		// Set button for custom dialog
		Button dialogButton = (Button) dialog.findViewById(R.id.dialog_button);
		dialogButton.setText(android.R.string.ok);
		dialogButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/** Start a new game */
	public void startGame() {
		Sounds.stop(this);
		Intent evadeIntent = new Intent(this, Evade.class);
		startActivity(evadeIntent);
	}
}