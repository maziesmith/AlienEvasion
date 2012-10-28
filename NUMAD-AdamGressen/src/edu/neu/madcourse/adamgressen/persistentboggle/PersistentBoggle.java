/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband3 for more book information.
 ***/
package edu.neu.madcourse.adamgressen.persistentboggle;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import edu.neu.madcourse.adamgressen.R;
import edu.neu.madcourse.adamgressen.persistentboggle.PersistentBoggle.BoggleFields;
import edu.neu.mobileclass.apis.KeyValueAPI;

public class PersistentBoggle extends Activity implements OnClickListener,PersistentBoggleInterface {
	private static final String BOARD_PREFS = "persistent_board-prefs";
	private static final String BOARD_KEY = "board";
	private static final String SCORE_KEY = "score";
	private static final String USED_WORDS_KEY = "used-words";
	private static final String TIME_KEY = "time";
	private static final String OPP_KEY = "opponent";
	private static final String WORLD_TIME_KEY = "world-time";

	private static final String USER_PREFS = "persistent_user_prefs";
	private static final String USER_ID_KEY = "id";
	
	public void setUserID(String userID) {
		PersistentBoggle.userID = userID;
	}

	public void setOpponent(String opponent) {
		PersistentBoggle.opponent = opponent;
	}

	private static String userID;

	private static String SERVER_BOARD_KEY;
	private static String SERVER_SCORE_KEY;
	private static String SERVER_USED_WORDS_KEY;
	private static String SERVER_TIME_KEY;
	private static String SERVER_OPP_KEY;
	private static String SERVER_ONLINE_KEY;
	public static String SERVER_WORLD_TIME_KEY;

	private static final String TEAM = "persistence";
	private static final String PASSWORD = "p3rs1st3nc3";

	private static String opponent;
	private static String OPP_BOARD_KEY;
	private static String OPP_SCORE_KEY;
	private static String OPP_USED_WORDS_KEY;
	private static String OPP_TIME_KEY;
	private static String OPP_ONLINE_KEY;
	private static String OPP_OPP_KEY;

	private static String OPP_WORLD_TIME_KEY;
	
	public static enum BoggleFields{
		OPPONENT,USERID,BOARD,SCORE,REMOTETIME,SERVERTIME,USEDWORDS
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.persistent_boggle_main);

		userID = getSharedPreferences(USER_PREFS, MODE_PRIVATE).getString(USER_ID_KEY, "");
		
		
		setKeys(this);

		// Set up click listeners for all the buttons
		View continueButton = findViewById(R.id.continue_button);
		continueButton.setOnClickListener(this);
		View newButton = findViewById(R.id.new_button);
		newButton.setOnClickListener(this);
		View rulesButton = findViewById(R.id.rules_button);
		rulesButton.setOnClickListener(this);
		View aboutButton = findViewById(R.id.about_button);
		aboutButton.setOnClickListener(this);
		View acknowledgementsButton = findViewById(R.id.acknowledgements_button);
		acknowledgementsButton.setOnClickListener(this);
		View exitButton = findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);
	}

	private void setKeys(Context context) {
		userID = context.getSharedPreferences(USER_PREFS, MODE_PRIVATE).getString(USER_ID_KEY, "");
		Log.d("Persistent Boggle", "user id"+userID);

		SERVER_BOARD_KEY = userID+"board";
		SERVER_SCORE_KEY = userID+"score";
		SERVER_USED_WORDS_KEY = userID+"used-words";
		SERVER_TIME_KEY = userID+"time";
		SERVER_OPP_KEY = userID+"opponent";
		SERVER_ONLINE_KEY = userID+"online";
		SERVER_WORLD_TIME_KEY = userID+"world-time";

		opponent = KeyValueAPI.get(TEAM, PASSWORD, SERVER_OPP_KEY);
		if (opponent == "")
			opponent = getPref(context, OPP_KEY, "");		
		Log.d("Boggle", "opponent"+opponent);
		
		OPP_BOARD_KEY = opponent+"board";
		OPP_SCORE_KEY = opponent+"score";
		OPP_USED_WORDS_KEY = opponent+"used-words";
		OPP_TIME_KEY = opponent+"time";
		OPP_ONLINE_KEY = opponent+"online";
		OPP_OPP_KEY = opponent+"opponent";
		OPP_WORLD_TIME_KEY = opponent+"world-time";
	}

	@Override
	protected void onResume() {
		super.onResume();
		PersistentBoggleMusic.playMusic(this, R.raw.main);
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		PersistentBoggleMusic.stop(this);
		
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.continue_button:
			startGame();
			break;
		case R.id.about_button:
			new AlertDialog.Builder(this)
			.setMessage(R.string.persistent_about_text)
			.setTitle(R.string.persistent_about_title)
			.setCancelable(true)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int id) {  
					dialog.dismiss();
				}
			}).show();
			break;
		case R.id.acknowledgements_button:
			new AlertDialog.Builder(this)
			.setMessage(R.string.persistent_acknowledgements)
			.setTitle(R.string.acknowledgements_label)
			.setCancelable(true)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int id) {  
					dialog.dismiss();
				}
			}).show();
			break;
		case R.id.rules_button:
			new AlertDialog.Builder(this)
			.setMessage(R.string.persistent_rules)
			.setTitle(R.string.rules_label)
			.setCancelable(true)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int id) {  
					dialog.dismiss();
				}
			}).show();
			break;
		case R.id.new_button:
			openNewGameDialog();
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
		inflater.inflate(R.menu.persistent_boggle_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent(this, PersistentBogglePrefs.class));
			return true;
		}
		return false;
	}

	/** Ask the user what difficulty level they want */
	private void openNewGameDialog() {
		new AlertDialog.Builder(this)
		.setTitle(R.string.new_game_title)
		.setMessage(R.string.new_game_message)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				addOpponent();
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		})
		.show();
	}

	// Clear all preferences, set a new opponent and start the game
	public void addOpponent() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		dialog.setView(input);
		dialog.setTitle("Opponent");
		dialog.setMessage("Enter opponent's Google ID");
		dialog.setPositiveButton("Create Game",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				String value = input.getText().toString().trim();
				resetGamePrefs(value);
				startGame();
			}
		});
		dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		dialog.show();
	}

	/** Start a new game */
	private void startGame() {
		Intent intent = new Intent(this, PersistentBoggleGame.class);
		startActivity(intent);
	}

	/***************************************************************
	 * Methods for modifying preferences locally and on the server 
	 * **************************************************************/

	// Clear game preferences for new game creation
	public void resetGamePrefs(String value) {
		// Clear all local board prefs
		getSharedPreferences(BOARD_PREFS, MODE_PRIVATE).edit().clear().commit();

		// Clear necessary remote board prefs
		clearKeyValue(SERVER_BOARD_KEY);
		clearKeyValue(SERVER_SCORE_KEY);
		clearKeyValue(SERVER_USED_WORDS_KEY);
		clearKeyValue(SERVER_TIME_KEY);
		clearKeyValue(SERVER_ONLINE_KEY);
		clearKeyValue(SERVER_OPP_KEY);
		
		clearKeyValue(OPP_BOARD_KEY);
		clearKeyValue(OPP_SCORE_KEY);
		clearKeyValue(OPP_USED_WORDS_KEY);
		clearKeyValue(OPP_TIME_KEY);
		clearKeyValue(OPP_ONLINE_KEY);
		clearKeyValue(OPP_OPP_KEY);
		
		Long time = new Date().getTime();
		
		setPref(getApplicationContext(), SCORE_KEY, 0);
		setPref(getApplicationContext(), USED_WORDS_KEY, "");
		setPref(getApplicationContext(), TIME_KEY, 120);
		setPref(getApplicationContext(), OPP_KEY, value);
		setPref(getApplicationContext(), WORLD_TIME_KEY, time);
		
		setKeyValue(this,SERVER_OPP_KEY, value);
		setKeyValue(this,SERVER_SCORE_KEY, "0");
		setKeyValue(this,SERVER_USED_WORDS_KEY, "");
		setKeyValue(this,SERVER_TIME_KEY, "120");
		setKeyValue(this,SERVER_WORLD_TIME_KEY, String.valueOf(time));
		setKeyValue(this,SERVER_ONLINE_KEY, String.valueOf(true));
		
		setKeyValue(this,OPP_SCORE_KEY, "0");
		setKeyValue(this,OPP_USED_WORDS_KEY, "");
		setKeyValue(this,OPP_TIME_KEY, "120");
		setKeyValue(this,OPP_WORLD_TIME_KEY, String.valueOf(time));
		setKeyValue(this,OPP_OPP_KEY, userID);
	}

	/** Set preferences */
	public static void setPref(Context context, String key, String val) {
		context.getSharedPreferences(BOARD_PREFS, MODE_PRIVATE).edit().putString(key, val).commit();
		context.getSharedPreferences(BOARD_PREFS, MODE_PRIVATE).edit().putLong(WORLD_TIME_KEY, new Date().getTime()).commit();
	}
	public static void setPref(Context context, String key, int val) {
		context.getSharedPreferences(BOARD_PREFS, MODE_PRIVATE).edit().putInt(key, val).commit();
		context.getSharedPreferences(BOARD_PREFS, MODE_PRIVATE).edit().putLong(WORLD_TIME_KEY, new Date().getTime()).commit();
	}
	public static void setPref(Context context, String key, boolean val) {
		context.getSharedPreferences(BOARD_PREFS, MODE_PRIVATE).edit().putBoolean(key, val).commit();
		context.getSharedPreferences(BOARD_PREFS, MODE_PRIVATE).edit().putLong(WORLD_TIME_KEY, new Date().getTime()).commit();
	}
	public static void setPref(Context context, String key, Long val) {
		context.getSharedPreferences(BOARD_PREFS, MODE_PRIVATE).edit().putLong(key, val).commit();
		context.getSharedPreferences(BOARD_PREFS, MODE_PRIVATE).edit().putLong(WORLD_TIME_KEY, new Date().getTime()).commit();
	}
	public static void setKeyValue(PersistentBoggleInterface callable, String key, String val) {
		KeyValueThread kvthreadobject = new KeyValueThread();
		PersistentBoggleState state = new PersistentBoggleState();
		
		state.setMode(callable,key,val);
		kvthreadobject.execute(state);
		
	}

	/** Get preferences */
	public static String getPref(Context context, String key, String defVal) {
		return context.getSharedPreferences(BOARD_PREFS, MODE_PRIVATE).getString(key, defVal);
	}
	public static int getPref(Context context, String key, int defVal) {
		return context.getSharedPreferences(BOARD_PREFS, MODE_PRIVATE).getInt(key, defVal);
	}
	public static boolean getPref(Context context, String key, boolean defVal) {
		return context.getSharedPreferences(BOARD_PREFS, MODE_PRIVATE).getBoolean(key, defVal);
	}
	public static Long getPref(Context context, String key, Long defVal) {
		return context.getSharedPreferences(BOARD_PREFS, MODE_PRIVATE).getLong(key, defVal);
	}
	
	public static void getKeyValue(PersistentBoggleInterface callable,String key, String defVal, BoggleFields field) {
		KeyValueThread kvthreadobject = new KeyValueThread();
		PersistentBoggleState state = new PersistentBoggleState();
		
		state.getMode(callable,key,defVal,field);
		kvthreadobject.execute(state);
		
	}
	
	public static void getKeyValue(PersistentBoggleInterface callable,String key, String defVal, BoggleFields field, boolean bool){
		KeyValueThread kvthreadobject = new KeyValueThread();
		PersistentBoggleState state = new PersistentBoggleState();
		
		state.getMode(callable,key,defVal,field);
		kvthreadobject.execute(state);
		
	}

	// Clear a remote preference
	public static void clearKeyValue(String key) {
		KeyValueThread kvthreadobject = new KeyValueThread();
		PersistentBoggleState state = new PersistentBoggleState();
		
		state.clearMode(key);
		kvthreadobject.execute(state);
	}

	
	
	public void setBoard(String board) {
		// TODO Auto-generated method stub
		
	}

	public void setScore(int score) {
		// TODO Auto-generated method stub
		
	}

	public void setRemoteTime(Long remotetime) {
		// TODO Auto-generated method stub
		
	}

	public void setTime(int time) {
		// TODO Auto-generated method stub
		
	}
	

	public void setUsedWordString(String usedwords) {
		// TODO Auto-generated method stub
		
	}

	public static String getKeyValuewait(String key, String defval) {
		// TODO Auto-generated method stub
		try {
			if (KeyValueAPI.isServerAvailable()) {
				String val = KeyValueAPI.get(TEAM, PASSWORD, key);
				if (val == "" || val==null){
					
					return defval;
				}
				else{
					return val;
				}

			}
			else
				return defval;
		}
		catch (Exception e) {
			return defval;
		}

	}

	public static void setKeyValuewait(String key, String value) {
		// TODO Auto-generated method stub
		try{
			if (KeyValueAPI.isServerAvailable()) {
				KeyValueAPI.put(TEAM, PASSWORD, key, value);
				KeyValueAPI.put(TEAM, PASSWORD, PersistentBoggle.SERVER_WORLD_TIME_KEY, String.valueOf(new Date().getTime()));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}


}