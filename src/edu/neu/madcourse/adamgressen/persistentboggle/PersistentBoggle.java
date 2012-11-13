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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import edu.neu.madcourse.adamgressen.R;
import edu.neu.mobileclass.apis.KeyValueAPI;

public class PersistentBoggle extends Activity implements OnClickListener,PersistentBoggleInterface {
	private static final String BOARD_PREFS = "persistent_board-prefs";
	public static final String BOARD_KEY = "board";
	private static final String SCORE_KEY = "score";
	private static final String USED_WORDS_KEY = "used-words";
	private static final String TIME_KEY = "time";
	private static final String OPP_KEY = "opponent";
	private static final String WORLD_TIME_KEY = "world-time";
	private static final String LOCAL_OPP_ONLINE_KEY = "opp-online";
	private static final String LOCAL_OPP_DONE_KEY = "opp-done";
	private static final String LEADERBOARD = "leaderboard";

	private static final String USER_PREFS = "persistent_user_prefs";
	private static final String USER_ID_KEY = "id";

	public void setUserID(String userID) {
		PersistentBoggle.userID = userID;
	}

	public void setOpponent(String opponent) {
		PersistentBoggle.opponent = opponent;
	}
	
	public void setOpponentScore(String score) {}

	private static String userID;
	private static boolean boardExists;
	private static final String GAME_OVER = "game-over";

	public static String SERVER_BOARD_KEY;
	private static String SERVER_SCORE_KEY;
	private static String SERVER_USED_WORDS_KEY;
	private static String SERVER_TIME_KEY;
	private static String SERVER_OPP_KEY;
	private static String SERVER_OPP_SCORE_KEY;
	private static String SERVER_OPP_USED_WORDS_KEY;
	private static String SERVER_ONLINE_KEY;
	public static String SERVER_WORLD_TIME_KEY;
	private static final String SERVER_LEADERBOARD = "leaderboard";

	private static final String TEAM = "persistence";
	private static final String PASSWORD = "p3rs1st3nc3";

	private static String opponent;
	public static String OPP_BOARD_KEY;
	private static String OPP_SCORE_KEY;
	private static String OPP_USED_WORDS_KEY;
	private static String OPP_TIME_KEY;
	private static String OPP_ONLINE_KEY;
	private static String OPP_OPP_KEY;

	private static String OPP_OPP_SCORE_KEY;
	private static String OPP_OPP_USED_WORDS_KEY;
	public static String OPP_WORLD_TIME_KEY;
	
	public static long POLL_INTERVAL = 300000L;

	public static enum BoggleFields{
		OPPONENT,USERID,BOARD,SCORE,REMOTETIME,SERVERTIME,USEDWORDS,OPPSCORE
	}

	GsonBuilder gsonb = new GsonBuilder();
	Gson gson = gsonb.create();

	View continueButton;
	View newButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		userID = getSharedPreferences(USER_PREFS, MODE_PRIVATE).getString(USER_ID_KEY, "");
		setKeys(this, "");

		setContentView(R.layout.persistent_boggle_main);

		// Set up click listeners for all the buttons
		continueButton = findViewById(R.id.continue_button);
		continueButton.setOnClickListener(this);
		newButton = findViewById(R.id.new_button);
		newButton.setOnClickListener(this);

		if (!boardExists)
			// Set visibility to GONE
			continueButton.setVisibility(8);

		View rulesButton = findViewById(R.id.rules_button);
		rulesButton.setOnClickListener(this);
		View aboutButton = findViewById(R.id.about_button);
		aboutButton.setOnClickListener(this);
		View acknowledgementsButton = findViewById(R.id.acknowledgements_button);
		acknowledgementsButton.setOnClickListener(this);
		View leaderboardButton = findViewById(R.id.leaderboard_button);
		leaderboardButton.setOnClickListener(this);
		View exitButton = findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);
	}

	private void setKeys(Context context, String opp) {
		userID = context.getSharedPreferences(USER_PREFS, MODE_PRIVATE).getString(USER_ID_KEY, "");
		Log.d("Persistent Boggle", "user id: "+userID);

		SERVER_BOARD_KEY = userID+"board";
		SERVER_SCORE_KEY = userID+"score";
		SERVER_USED_WORDS_KEY = userID+"used-words";
		SERVER_TIME_KEY = userID+"time";
		SERVER_OPP_KEY = userID+"opponent";
		SERVER_ONLINE_KEY = userID+"online";
		SERVER_OPP_SCORE_KEY = userID+"opp-score";
		SERVER_OPP_USED_WORDS_KEY = userID+"opp-used-words";
		SERVER_WORLD_TIME_KEY = userID+"world-time";

		if (opp.equals("")) {
			opponent = PersistentBoggle.getKeyValuewait(SERVER_OPP_KEY, "");
			if (opponent.equals(""))
				opponent = PersistentBoggle.getPref(context, OPP_KEY, "");	
			Log.d("Boggle", "opponent: "+opponent);
		}
		else
			opponent = opp;

		if (PersistentBoggle.getKeyValuewait(SERVER_BOARD_KEY, "").equals("") &&
				PersistentBoggle.getPref(context, BOARD_KEY, "").equals(""))
			boardExists = false;
		else
			boardExists = true;

		OPP_BOARD_KEY = opponent+"board";
		OPP_SCORE_KEY = opponent+"score";
		OPP_USED_WORDS_KEY = opponent+"used-words";
		OPP_TIME_KEY = opponent+"time";
		OPP_ONLINE_KEY = opponent+"online";
		OPP_OPP_KEY = opponent+"opponent";
		OPP_OPP_SCORE_KEY = opponent+"opp-score";
		OPP_OPP_USED_WORDS_KEY = opponent+"opp-used-words";
		OPP_WORLD_TIME_KEY = opponent+"world-time";
	}

	@Override
	protected void onResume() {
		super.onResume();

		setKeys(this, "");

		if (boardExists)
			// Set visibility to VISIBLE
			continueButton.setVisibility(0);

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
		case R.id.leaderboard_button:
			showLeaderboard();
			break;
		case R.id.exit_button:
			// For clearing all currently stored information
			//KeyValueAPI.clear(TEAM, PASSWORD);
			//this.getSharedPreferences(BOARD_PREFS, MODE_PRIVATE).edit().clear().commit();
			//this.getSharedPreferences(USER_PREFS, MODE_PRIVATE).edit().clear().commit();
			//Log.d("Persistent Boggle", "Cleared");
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
		// If the board exists then ask if you want to overwrite
		if (boardExists) {
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
		// Otherwise just show the add opponent dialog
		else
			addOpponent();
	}

	// Clear all preferences, set a new opponent and start the game
	public void addOpponent() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		dialog.setView(input);
		dialog.setTitle("Opponent");
		dialog.setMessage("Enter opponent's Google ID");
		dialog.setPositiveButton("Create Game",	new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				String value = input.getText().toString().trim();
				String testOppTimeKey = value+"time";
				int t = Integer.valueOf(PersistentBoggle.getKeyValuewait(testOppTimeKey, "120"));
				if (t > 0 && t != 120) {
					Toast.makeText(getApplicationContext(), 
							"This player is currently in another game. Try again later.",
							Toast.LENGTH_LONG).show();
				}
				else {
					setKeys(getApplicationContext(), value);
					resetGamePrefs(value);
					startGame();
				}
			}
		});
		dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		dialog.show();
	}

	/** Shows the leaderboard */
	private void showLeaderboard() {
		// If the server is available
		if (KeyValueAPI.isServerAvailable()) {
			// Get the server leaderboard
			Leaderboard leaderboard = gson.fromJson(PersistentBoggle.getKeyValuewait(SERVER_LEADERBOARD, ""), Leaderboard.class);
			if (leaderboard == null)
				leaderboard = new Leaderboard(new LinkedList<LeaderboardEntry>());
			// Get the local leaderboard
			Leaderboard localLeaderboard = gson.fromJson(PersistentBoggle.getPref(this, LEADERBOARD, ""), Leaderboard.class);
			if (localLeaderboard == null)
				localLeaderboard = new Leaderboard(new LinkedList<LeaderboardEntry>());
			// Combine the leaderboards
			leaderboard.combineLeaderboards(localLeaderboard);
			// Write the combined leaderboards to the server
			PersistentBoggle.setKeyValue(this, SERVER_LEADERBOARD, gson.toJson(leaderboard));
			// Clear the local leaderboard
			PersistentBoggle.setPref(this, LEADERBOARD, "");
			// List to be populated by leader info
			List<String> leaderStrings = new LinkedList<String>();
			// Iterate through the leaderboard entries
			for (LeaderboardEntry entry : leaderboard.entries) {
				leaderStrings.add(entry.info);
			}
			// Create charsequence to be displayed by alertdialog
			CharSequence[] leaders = leaderStrings.toArray(new CharSequence[leaderStrings.size()]);

			new AlertDialog.Builder(this)
			.setTitle(R.string.persistent_leaderboard_title)
			.setItems(leaders, null)
			.setCancelable(true)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int id) {  
					dialog.dismiss();
				}
			}).show();
		}
		// If the server is unavailable
		else {
			new AlertDialog.Builder(this)
			.setTitle("Server Unavailable")
			.setMessage("Viewing the leaderboard requires connection to the server. Please try again later.")
			.setCancelable(true)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).show();
		}
	}

	/** Start a new game */
	private void startGame() {
		Intent intent = new Intent(this, PersistentBoggleGame.class);
		startActivity(intent);
	}

	/***************************************************************
	 * Methods for modifying preferences locally and on the server 
	 * **************************************************************/

	// Reset game preferences for new game creation
	public void resetGamePrefs(String value) {
		// Clear all local board prefs
		getSharedPreferences(BOARD_PREFS, MODE_PRIVATE).edit().clear().commit();

		// Clear necessary remote board prefs
		clearKeyValue(SERVER_BOARD_KEY);
		//clearKeyValue(SERVER_SCORE_KEY);
		//clearKeyValue(SERVER_USED_WORDS_KEY);
		//clearKeyValue(SERVER_TIME_KEY);
		//clearKeyValue(SERVER_ONLINE_KEY);
		//clearKeyValue(SERVER_OPP_KEY);
		clearKeyValue(SERVER_OPP_SCORE_KEY);
		clearKeyValue(SERVER_OPP_USED_WORDS_KEY);

		clearKeyValue(OPP_BOARD_KEY);
		//clearKeyValue(OPP_SCORE_KEY);
		//clearKeyValue(OPP_USED_WORDS_KEY);
		//clearKeyValue(OPP_TIME_KEY);
		clearKeyValue(OPP_ONLINE_KEY);
		//clearKeyValue(OPP_OPP_KEY);
		//clearKeyValue(OPP_OPP_SCORE_KEY);
		//clearKeyValue(OPP_OPP_USED_WORDS_KEY);

		Long time = new Date().getTime();

		setPref(this, SCORE_KEY, 0);
		setPref(this, USED_WORDS_KEY, "");
		setPref(this, TIME_KEY, 120);
		setPref(this, OPP_KEY, value);
		setPref(this, WORLD_TIME_KEY, time);
		setPref(this, LOCAL_OPP_ONLINE_KEY, false);
		setPref(this, LOCAL_OPP_DONE_KEY, false);
		setPref(this, GAME_OVER, false);

		setKeyValuewait(SERVER_OPP_KEY, value);
		setKeyValuewait(SERVER_SCORE_KEY, "0");
		setKeyValuewait(SERVER_USED_WORDS_KEY, "");
		setKeyValuewait(SERVER_TIME_KEY, "120");
		setKeyValuewait(SERVER_WORLD_TIME_KEY, String.valueOf(time));
		setKeyValuewait(SERVER_ONLINE_KEY, String.valueOf(true));

		setKeyValuewait(OPP_SCORE_KEY, "0");
		setKeyValuewait(OPP_USED_WORDS_KEY, "");
		setKeyValuewait(OPP_OPP_SCORE_KEY, "0");
		setKeyValuewait(OPP_OPP_USED_WORDS_KEY, "");
		setKeyValuewait(OPP_TIME_KEY, "120");
		setKeyValuewait(OPP_WORLD_TIME_KEY, String.valueOf(time));
		setKeyValuewait(OPP_OPP_KEY, userID);
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

	public void setBoard(String board) {}

	public void setScore(int score) {}

	public void setRemoteTime(Long remotetime) {}

	public void setTime(int time) {}

	public void setUsedWordString(String usedwords) {}

	public static String getKeyValuewait(String key, String defVal) {
		try {
			if (KeyValueAPI.isServerAvailable()) {
				String val = KeyValueAPI.get(TEAM, PASSWORD, key);
				if (val.equals("") || val == null)
					return defVal;
				else
					return val;
			}
			else
				return defVal;
		}
		catch (Exception e) {
			return defVal;
		}
	}

	public static void setKeyValuewait(String key, String value) {
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