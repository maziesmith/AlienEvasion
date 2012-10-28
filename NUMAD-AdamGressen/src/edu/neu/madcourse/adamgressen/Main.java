package edu.neu.madcourse.adamgressen;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import edu.neu.madcourse.adamgressen.boggle.Boggle;
import edu.neu.madcourse.adamgressen.persistentboggle.PersistentBoggle;
import edu.neu.madcourse.adamgressen.sudoku.Sudoku;
import edu.neu.mobileClass.*;

public class Main extends Activity implements OnClickListener {

	private static final String USER_PREFS = "persistent_user_prefs";
	private static final String USER_ID = "id";
	Account[] accounts;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		setTitle("Adam Gressen");

		// Get Google Account
		AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
		accounts = accountManager.getAccountsByType("com.google");
		if (accounts.length > 0) {
			Account account = accounts[0];
			Log.d("google: ", "Google: "+account.name);
			getSharedPreferences(USER_PREFS, MODE_PRIVATE).edit().putString(USER_ID, account.name).commit();
		}

		// Set up click listeners for buttons
		View teamButton = findViewById(R.id.team_button);
		teamButton.setOnClickListener(this);
		View sudokuButton = findViewById(R.id.sudoku_button);
		sudokuButton.setOnClickListener(this);
		View boggleButton = findViewById(R.id.boggle_button);
		boggleButton.setOnClickListener(this);
		View persistentButton = findViewById(R.id.persistent_boggle_button);
		persistentButton.setOnClickListener(this);
		View errorButton = findViewById(R.id.error_button);
		errorButton.setOnClickListener(this);
		View exitButton = findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);

		PhoneCheckAPI.doAuthorization(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	// Handle click events for all buttons
	@SuppressWarnings("null")
	public void onClick(View v) {
		switch (v.getId()) {
		// Team button click event
		case R.id.team_button:
			String version = "";
			try {
				version = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				version = "";
			}

			TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
			String phoneId = TelephonyMgr.getDeviceId();

			new AlertDialog.Builder(this)
			.setMessage("Name: Adam Gressen\nEmail: gressen.a@husky.neu.edu\n"+
					"Name: Vaibhav Mahindroo\nEmail: mahindroo.v@husky.neu.edu\nVersion: "+
					version+"\nPhone ID: "+phoneId)
					.setTitle("Team Members")
					.setCancelable(true)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
						public void onClick(DialogInterface dialog, int id) {  
							dialog.dismiss();
						}
					}).show();
			break;
			// Sudoku button click event
		case R.id.sudoku_button:
			Intent l = new Intent(this, Sudoku.class);
			startActivity(l);
			break;
			// Boggle button click event
		case R.id.boggle_button:
			Intent j = new Intent(this, Boggle.class);
			startActivity(j);
			break;
			// Persistent Boggle button click event
		case R.id.persistent_boggle_button:
			if (accounts != null && accounts.length != 0) {
				Intent u = new Intent(this, PersistentBoggle.class);
				startActivity(u);
			}
			else {
				new AlertDialog.Builder(this)
				.setMessage(R.string.no_google_account)
				.setTitle("Google Account Required")
				.setCancelable(true)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
					public void onClick(DialogInterface dialog, int id) {  
						dialog.dismiss();
					}
				}).show();
			}
			break;
			// Error button click event
		case R.id.error_button:
			String crash = null;
			crash.charAt(0);
			break;
		case R.id.exit_button:
			finish();
			break;
		}
	}
}