/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband3 for more book information.
 ***/
package edu.neu.madcourse.adamgressen.persistentboggle;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import edu.neu.madcourse.adamgressen.R;
import edu.neu.mobileclass.apis.KeyValueAPI;
import edu.neu.madcourse.adamgressen.persistentboggle.PersistentBoggle.BoggleFields;

public class PersistentBoggleGame extends Activity implements PersistentBoggleInterface {
	private static final String TAG = "Boggle";
	private static int ROWS = 5;

	private Timer timer = new Timer();
	private TimerTask task;
	int delay = 1000; // milliseconds
	private int time;

	public int getTime() {
		return this.time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	private boolean paused = false;
	private boolean gameOver = false;

	public boolean getGameOver() {
		return this.gameOver;
	}

	private static final String BOARD_KEY = "board";
	private static final String SCORE_KEY = "score";
	private static final String TIME_KEY = "time";
	private static final String USED_WORDS_KEY = "used-words";
	private static final String OPP_KEY = "opponent";
	private static final String WORLD_TIME_KEY = "world-time";
	public static String getWorldTimeKey() {
		return WORLD_TIME_KEY;
	}

	private static final String LOCAL_OPP_ONLINE_KEY = "opp-online";
	private static final String LOCAL_OPP_DONE_KEY = "opp-done";
	private static final String LEADERBOARD = "leaderboard";
	private static final String GAME_OVER = "game-over";

	private static final String USER_PREFS = "persistent_user_prefs";
	private static final String USER_ID_KEY = "id";
	private static String userID;

	public static String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		PersistentBoggleGame.userID = userID;
	}
	
	private static String SERVER_BOARD_KEY;
	private static String SERVER_SCORE_KEY;
	private static String SERVER_USED_WORDS_KEY;
	private static String SERVER_TIME_KEY;
	private static String SERVER_OPP_KEY;
	private static String SERVER_ONLINE_KEY;
	private static String SERVER_OPP_SCORE_KEY;
	private static String SERVER_OPP_USED_WORDS_KEY;
	private static String SERVER_WORLD_TIME_KEY;

	public static String getSERVER_WORLD_TIME_KEY() {
		return SERVER_WORLD_TIME_KEY;
	}

	private static String opponent;
	public static String OPP_BOARD_KEY;
	private static String OPP_SCORE_KEY;
	private static String OPP_TIME_KEY;
	public static String getOPP_TIME_KEY() {
		return OPP_TIME_KEY;
	}
	private static String OPP_ONLINE_KEY;
	private static String OPP_OPP_KEY;
	public static String getOPP_OPP_KEY() {
		return OPP_OPP_KEY;
	}
	private static String OPP_USED_WORDS_KEY;

	private static String OPP_OPP_SCORE_KEY;
	private static String OPP_OPP_USED_WORDS_KEY;
	public static String opponentScore = "0";

	private Long remoteTime = 0L;

	public Long getRemoteTime() {
		return this.remoteTime;
	}

	public void setRemoteTime(Long remoteTime) {
		this.remoteTime = remoteTime;
	}

	public void setOpponent(String opponent) {
		PersistentBoggleGame.opponent = opponent;
	}

	public void setOpponentScore(String s) {
		opponentScore = s;
	}

	public void setBoard(String board) {
		this.board = board;
	}

	public String retrieveOpponentScore() {
		return opponentScore;
	}

	private String board;
	private List<String> usedWords = new LinkedList<String>();
	private String usedWordString;

	public String getUsedWordString() {
		return this.usedWordString;
	}

	public void setUsedWordString(String usedWordString) {
		this.usedWordString = usedWordString;
	}
	InputStream is;
	BufferedReader br;

	private static final LinkedList<String> die1 = new LinkedList<String>(
			Arrays.asList("A", "A", "A", "F", "R", "S"));
	private static final LinkedList<String> die2 = new LinkedList<String>(
			Arrays.asList("A", "A", "E", "E", "E", "E"));
	private static final LinkedList<String> die3 = new LinkedList<String>(
			Arrays.asList("A", "A", "F", "I", "R", "S"));
	private static final LinkedList<String> die4 = new LinkedList<String>(
			Arrays.asList("A", "D", "E", "N", "N", "N"));
	private static final LinkedList<String> die5 = new LinkedList<String>(
			Arrays.asList("A", "E", "E", "E", "E", "M"));
	private static final LinkedList<String> die6 = new LinkedList<String>(
			Arrays.asList("A", "E", "E", "G", "M", "U"));
	private static final LinkedList<String> die7 = new LinkedList<String>(
			Arrays.asList("A", "E", "G", "M", "N", "N"));
	private static final LinkedList<String> die8 = new LinkedList<String>(
			Arrays.asList("A", "F", "I", "R", "S", "Y"));
	private static final LinkedList<String> die9 = new LinkedList<String>(
			Arrays.asList("B", "J", "K", "Q", "X", "Z"));
	private static final LinkedList<String> die10 = new LinkedList<String>(
			Arrays.asList("C", "C", "N", "S", "T", "W"));
	private static final LinkedList<String> die11 = new LinkedList<String>(
			Arrays.asList("C", "E", "I", "I", "L", "T"));
	private static final LinkedList<String> die12 = new LinkedList<String>(
			Arrays.asList("C", "E", "I", "L", "P", "T"));
	private static final LinkedList<String> die13 = new LinkedList<String>(
			Arrays.asList("C", "E", "I", "P", "S", "T"));
	private static final LinkedList<String> die14 = new LinkedList<String>(
			Arrays.asList("D", "D", "L", "N", "O", "R"));
	private static final LinkedList<String> die15 = new LinkedList<String>(
			Arrays.asList("D", "H", "H", "L", "O", "R"));
	private static final LinkedList<String> die16 = new LinkedList<String>(
			Arrays.asList("D", "H", "H", "N", "O", "T"));
	private static final LinkedList<String> die17 = new LinkedList<String>(
			Arrays.asList("D", "H", "L", "N", "O", "R"));
	private static final LinkedList<String> die18 = new LinkedList<String>(
			Arrays.asList("E", "I", "I", "I", "T", "T"));
	private static final LinkedList<String> die19 = new LinkedList<String>(
			Arrays.asList("E", "M", "O", "T", "T", "T"));
	private static final LinkedList<String> die20 = new LinkedList<String>(
			Arrays.asList("E", "N", "S", "S", "S", "U"));
	private static final LinkedList<String> die21 = new LinkedList<String>(
			Arrays.asList("F", "I", "P", "R", "S", "Y"));
	private static final LinkedList<String> die22 = new LinkedList<String>(
			Arrays.asList("G", "O", "R", "R", "V", "W"));
	private static final LinkedList<String> die23 = new LinkedList<String>(
			Arrays.asList("H", "I", "P", "R", "R", "Y"));
	private static final LinkedList<String> die24 = new LinkedList<String>(
			Arrays.asList("N", "O", "O", "T", "U", "W"));
	private static final LinkedList<String> die25 = new LinkedList<String>(
			Arrays.asList("O", "O", "O", "T", "T", "U"));

	@SuppressWarnings("unchecked")
	private static final LinkedList<LinkedList<String>> THE_DIE = new LinkedList<LinkedList<String>>(
			Arrays.asList(die1, die2, die3, die4, die5, die6, die7, die8, die9,
					die10, die11, die12, die13, die14, die15, die16, die17,
					die18, die19, die20, die21, die22, die23, die24, die25));

	private int score;

	public int retrieveScore() {
		return this.score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	private PersistentBogglePuzzleView persistentBogglePuzzleView;
	GsonBuilder gsonb = new GsonBuilder();
	Gson gson = gsonb.create();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		setKeys(this);

		// Determine whether to use the remote or local values
		boolean useServer = false;
		Long localTime = PersistentBoggle.getPref(this, WORLD_TIME_KEY, (long) 0);
		Long remoteTime = Long.valueOf(PersistentBoggle.getKeyValuewait(SERVER_WORLD_TIME_KEY, "0")).longValue();

		String remoteTimeString = PersistentBoggle.getKeyValuewait(SERVER_WORLD_TIME_KEY, "0");
		remoteTime = Long.valueOf(remoteTimeString);
		Log.d(TAG,"Setting the remote time to: "+ remoteTime);

		if (remoteTime == 0 && localTime != 0)
			useServer = false;
		else if (localTime == 0 && remoteTime != 0)
			useServer = true;
		else if (remoteTime == 0 && localTime == 0)
			useServer = false;
		else {
			useServer = remoteTime > localTime;
		}
		Log.d(TAG,"UseServer: " + useServer);

		this.board = getBoard(useServer);
		this.score = getScore(useServer);
		this.usedWords = getUsedWords(useServer);
		this.time = getTime(useServer);

		// Send board to prefs and server
		PersistentBoggle.setPref(this, BOARD_KEY, board);
		PersistentBoggle.setKeyValuewait(SERVER_BOARD_KEY, board);
		Log.d(TAG, "server board key"+SERVER_BOARD_KEY);
		Log.d(TAG, "opp board key"+OPP_BOARD_KEY);

		// Send time to prefs and server
		PersistentBoggle.setPref(this, TIME_KEY, time);
		PersistentBoggle.setKeyValuewait(SERVER_TIME_KEY, String.valueOf(time));

		// Send score to prefs and server
		PersistentBoggle.setPref(this, SCORE_KEY, score);
		PersistentBoggle.setKeyValuewait(SERVER_SCORE_KEY, String.valueOf(score));

		// Send used words to prefs and server
		PersistentBoggle.setPref(this, USED_WORDS_KEY, this.serializeWords());
		PersistentBoggle.setKeyValuewait(SERVER_USED_WORDS_KEY, this.serializeWords());

		// Set this player's opponent score to the opponent's score from the
		// server ---------------------check
		PersistentBoggle.setKeyValue(this, SERVER_OPP_SCORE_KEY,
				PersistentBoggle.getKeyValuewait(OPP_SCORE_KEY, "0"));
		// Set this player's opponent used words list to the opponent's used
		// words list from the server----------------------------check
		PersistentBoggle.setKeyValue(this, SERVER_OPP_USED_WORDS_KEY,
				PersistentBoggle.getKeyValuewait(OPP_USED_WORDS_KEY, ""));

		persistentBogglePuzzleView = new PersistentBogglePuzzleView(this);
		setContentView(persistentBogglePuzzleView);
		persistentBogglePuzzleView.requestFocus();

		this.task = new TimerTask() {
			public void run() {
				if (!paused) {
					time--;
					// If time is less than 0 the game is over
					if (time <= 0) {
						time = 0;
						gameOver = true;
					}

					if (time % 5 == 0) {
						Context ctx = getApplicationContext();

						// Get opponent score from this player's stored copy
						opponentScore = PersistentBoggle.getKeyValuewait(SERVER_OPP_SCORE_KEY, "0");

						// Determine if your opponent is online
						boolean serverOnline = Boolean.valueOf(
								PersistentBoggle.getKeyValuewait(OPP_ONLINE_KEY, "false"));
						boolean localOnline = PersistentBoggle.getPref(ctx, LOCAL_OPP_ONLINE_KEY, false);
						if (localOnline && !serverOnline) {
							showToast(opponent + " has gone offline.");
							PersistentBoggle.setPref(ctx, LOCAL_OPP_ONLINE_KEY, false);
						}
						else if (!localOnline && serverOnline) {
							showToast(opponent + " has come online.");
							PersistentBoggle.setPref(ctx, LOCAL_OPP_ONLINE_KEY, true);
						}

						// Determine if your opponent has finished the game
						boolean serverDone = !PersistentBoggle.getKeyValuewait(OPP_OPP_KEY, "").equals(userID) ||
								(Integer.valueOf(PersistentBoggle.getKeyValuewait(OPP_TIME_KEY, "120")) == 0);
						boolean localDone = PersistentBoggle.getPref(ctx, LOCAL_OPP_DONE_KEY, false);
						if (localDone && !serverDone)
							PersistentBoggle.setPref(ctx, LOCAL_OPP_DONE_KEY, false);
						else if (!localDone && serverDone) {
							showToast(opponent + " has finished the game.");
							PersistentBoggle.setPref(ctx, LOCAL_OPP_DONE_KEY, true);
						}
					}
				}
			}
		};
		timer.schedule(this.task, 0, delay);
		
		startAlarmReceiver();
	}
	
	private void startAlarmReceiver() {		
		AlarmManager alarmmanager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this,AlarmReceiver.class);
		PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent,PendingIntent.FLAG_CANCEL_CURRENT );
		
		//Calendar calendar = Calendar.getInstance();
		//calendar.add(Calendar.MILLISECOND,1000);
		alarmmanager.setRepeating(AlarmManager.RTC_WAKEUP,
			System.currentTimeMillis()+3000,PersistentBoggle.POLL_INTERVAL, pIntent);
	}

	/**
	 * Displays a toast with information about the opponent's state These states
	 * include "online", "offline", "paused", "resumed"
	 * */
	private void showToast(final CharSequence state) {
		PersistentBoggleGame.this.runOnUiThread(new Runnable() {
			
			public void run() {
				Toast t = Toast.makeText(PersistentBoggleGame.this, state,
						Toast.LENGTH_LONG);
				t.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
				t.show();
			}
		});
	}

	private void setKeys(Context context) {
		userID = context.getSharedPreferences(USER_PREFS, MODE_PRIVATE)
				.getString(USER_ID_KEY, "");
		Log.d("Persistent Boggle", "user id: " + userID);

		SERVER_BOARD_KEY = userID + "board";
		SERVER_SCORE_KEY = userID + "score";
		SERVER_USED_WORDS_KEY = userID + "used-words";
		SERVER_TIME_KEY = userID + "time";
		SERVER_OPP_KEY = userID + "opponent";
		SERVER_ONLINE_KEY = userID + "online";
		SERVER_OPP_SCORE_KEY = userID + "opp-score";
		SERVER_OPP_USED_WORDS_KEY = userID + "opp-used-words";
		SERVER_WORLD_TIME_KEY = userID + "world-time";
		
		opponent = PersistentBoggle.getKeyValuewait(SERVER_OPP_KEY, "");
		if (opponent.equals(""))
			opponent = PersistentBoggle.getPref(context, OPP_KEY, "");	
		Log.d("Boggle", "opponent: "+opponent);

		OPP_BOARD_KEY = opponent + "board";
		OPP_SCORE_KEY = opponent + "score";
		OPP_TIME_KEY = opponent + "time";
		OPP_USED_WORDS_KEY = opponent + "used-words";
		OPP_ONLINE_KEY = opponent + "online";
		OPP_OPP_KEY = opponent + "opponent";
		OPP_OPP_SCORE_KEY = opponent + "opp-score";
		OPP_OPP_USED_WORDS_KEY = opponent + "opp-used-words";

		PersistentBoggle.getKeyValue(this, OPP_SCORE_KEY, "0", BoggleFields.OPPSCORE);
	}

	/** Return game state (paused) */
	public boolean getState() {
		return paused;
	}

	/** Resume game */
	public void resumeGame() {
		paused = false;
		// Set player to be online
		PersistentBoggle.setKeyValue(this,SERVER_ONLINE_KEY, String.valueOf(true));
		// Restart the music
		PersistentBoggleMusic.playMusic(this, R.raw.game);
	}

	@Override
	protected void onResume() {
		if(isActivityVisible()){

			super.onResume();
			resumeGame();
		}
	}

	/** Pause the game */
	public void pauseGame() {
		paused = true;

		// Store time value locally and remotely
		PersistentBoggle.setPref(this, TIME_KEY, this.time);
		PersistentBoggle.setKeyValue(this,SERVER_TIME_KEY, String.valueOf(this.time));
		PersistentBoggle.setKeyValue(this,SERVER_ONLINE_KEY, String.valueOf(false));

		PersistentBoggleMusic.stop(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		pauseGame();
	}

	/** Return the board or trigger createBoard */
	private String getBoard(boolean useServer) {
		String b = "";
		// Check server for value first
		if (useServer) {
			b = PersistentBoggle.getKeyValuewait(SERVER_BOARD_KEY, "");
			Log.d(TAG, "board getboard: "+b);
		}
		else
			b = PersistentBoggle.getPref(this, BOARD_KEY, "");
		if (b.equals(""))
			b = createBoard();
		return b;
	}

	private int getScore(boolean useServer) {
		int s = 0;
		if (useServer) {
			s = Integer.parseInt(PersistentBoggle.getKeyValuewait(SERVER_SCORE_KEY, "0"));
		}
		else
			s = PersistentBoggle.getPref(this, SCORE_KEY, 0);
		return s;
	}

	private int getTime(boolean useServer) {
		int t = 120;
		if (useServer)
			t = Integer.parseInt(PersistentBoggle.getKeyValuewait(SERVER_TIME_KEY, "120"));
		else
			t = PersistentBoggle.getPref(this, TIME_KEY, 120);
		return t;
	}

	private List<String> getUsedWords(boolean useServer) {
		LinkedList<String> value = new LinkedList<String>();
		String serverVal = "";
		if (useServer) {
			serverVal = PersistentBoggle.getKeyValuewait(SERVER_USED_WORDS_KEY, "");
			if (!serverVal.equals("") && serverVal != null) {
				String[] list = gson.fromJson(serverVal, String[].class);
				for (String s : list)
					value.add(s);
			}
		}
		else {
			serverVal = PersistentBoggle.getPref(this, USED_WORDS_KEY, "");
			if (!serverVal.equals("") && serverVal != null) {
				String[] list = gson.fromJson(serverVal, String[].class);
				for (String s : list)
					value.add(s);
			}
		}
		return value;
	}

	public String serializeWords() {
		return gson.toJson(this.usedWords);
	}

	/** Generate a boggle board */
	private String createBoard() {
		// The board to generate
		board = "";
		// The selected die index
		int dieIndex;
		// Reset the remainingDie list
		List<List<String>> remainingDie = new LinkedList<List<String>>();
		for (List<String> l : THE_DIE) {
			remainingDie.add(l);
		}
		Random rand = new Random();
		for (int i = 0; i < (ROWS * ROWS); i++) {
			int size = remainingDie.size() - 1;
			if (size > 0)
				// The selected die index
				dieIndex = rand.nextInt(size);
			else
				dieIndex = 0;
			// Choose letter
			char chosenChar = chooseLetter(dieIndex, remainingDie);
			// Remove die from remainingDie list
			remainingDie.remove(dieIndex);
			// Add char to board
			board += chosenChar;
		}
		Log.d(TAG, "board generated: " + board);
		Log.d(TAG, "opp board key: "+OPP_BOARD_KEY);
		PersistentBoggle.setKeyValuewait(OPP_BOARD_KEY, board);
		return board;
	}

	/** Roll the die and choose the letter */
	private char chooseLetter(int dieIndex, List<List<String>> remainingDie) {
		Random letterRand = new Random();
		// Get chosen die
		List<String> chosenDie = remainingDie.get(dieIndex);
		// Get a char from the chosen die list
		char chosenChar = chosenDie.get(letterRand.nextInt(5)).toCharArray()[0];
		// Return the chosen character
		return chosenChar;
	}

	/** Get number of rows */
	public int getRows() {
		return ROWS;
	}

	/** Convert an array into a board string */
	/*
	 * static private String toBoardString(char[] boardList) { StringBuilder buf
	 * = new StringBuilder(); for (char element : boardList) {
	 * buf.append(element); } return buf.toString(); }
	 */

	/** Convert a board string into an array */
	static protected char[] fromBoardString(String string) {
		char[] convertedBoard = new char[string.length()];
		for (int i = 0; i < convertedBoard.length; i++) {
			convertedBoard[i] = string.charAt(i);
		}
		return convertedBoard;
	}

	/** Return the tile at the given coordinates */
	private char getTile(int x, int y) {
		return fromBoardString(board)[y * ROWS + x];
	}

	private char getTile(int tile) {
		return fromBoardString(board)[tile];
	}

	/** Return a string for the tile at the given coordinates */
	protected String getTileString(int x, int y) {
		char v = getTile(x, y);
		return String.valueOf(v);
	}

	protected String getTileString(int tile) {
		char v = getTile(tile);
		return String.valueOf(v);
	}

	/** List of indices of selected tiles */
	private List<Integer> selected = new ArrayList<Integer>();

	/** Select a tile */
	public int selectTile(int index) {
		// If the tile is selectable
		if (isSelectable(index)) {
			selected.add(index);
			return 1;
		}
		// If given index is the same as the previously selected index
		else if (index == selected.get(selected.size() - 1)) {
			String word = "";
			for (int i : selected)
				word += getTileString(i);
			submitWord(word);
			return 2;
		}
		// If given index is same as tile selected before the last selection
		else if (isSelected(index)) { // && selected.size()>1) {
			// Simply remove the last selected letter
			int s = selected.size() - 1;
			selected.remove(s);
			return 3;
		} else
			return 0;
	}

	/** Returns true if a word is valid */
	private boolean isValidWord(String word) {
		boolean isValid = false;
		boolean isRepeat = false;
		String length = String.valueOf(word.length());
		String firstLetter = String.valueOf(word.charAt(0)).toLowerCase();
		for (String s : usedWords) {
			if (s.equalsIgnoreCase(word))
				isRepeat = true;
		}
		if (isRepeat)
			return false;
		else {
			try {
				is = this.getAssets().open(length + "/" + firstLetter + ".txt");
				br = new BufferedReader(new InputStreamReader(is));
				String line;

				while (!isValid && (line = br.readLine()) != null) {
					isValid = word.trim().equalsIgnoreCase(line.trim());
				}

				br.close();
				is.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return isValid;
		}
	}

	/** Calculate point value of word */
	private int calculateScore(String word) {
		int wordValue = 0;
		if (word.length() <= 4)
			wordValue = 2;
		else
			wordValue = word.length() - 2;
		return wordValue;
	}

	/** Submit word */
	private void submitWord(String word) {
		if (isValidWord(word)) {
			this.score += calculateScore(word);
			persistentBogglePuzzleView.setScore(this.score);
			PersistentBoggle.setKeyValue(this,SERVER_SCORE_KEY, String.valueOf(this.score));
			PersistentBoggle.setPref(this, SCORE_KEY, this.score);

			usedWords.add(word.trim().toLowerCase());
			String value = this.serializeWords();
			PersistentBoggle.setKeyValue(this,SERVER_USED_WORDS_KEY, value);
			PersistentBoggle.setPref(this, USED_WORDS_KEY, value);

			if (PersistentBoggle.getKeyValuewait(OPP_OPP_KEY, "").equals(userID)) {
				PersistentBoggle.setKeyValue(this, OPP_OPP_SCORE_KEY, String.valueOf(this.score));
				PersistentBoggle.setKeyValue(this, OPP_OPP_USED_WORDS_KEY, value);
			}

			PersistentBoggle.setKeyValue(this,SERVER_TIME_KEY, String.valueOf(this.time));
			PersistentBoggle.setPref(this, TIME_KEY, this.time);

			PersistentBoggleMusic.playSound(this, R.raw.reward);
		} 
		else
			PersistentBoggleMusic.playSound(this, R.raw.fail);
		selected.clear();
	}

	public boolean isActivityVisible() {
		KeyguardManager kgMgr = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
		return !(kgMgr.inKeyguardRestrictedInputMode());
	}  

	/** Return whether a tile is in range of the previously selected tile */
	private boolean inRange(int selIndex) {
		int tile = selected.get(selected.size() - 1);
		return ((tile - 1) == selIndex) || ((tile + 1) == selIndex)
				|| ((tile - ROWS) == selIndex) || ((tile + ROWS) == selIndex)
				|| ((tile - ROWS - 1) == selIndex)
				|| ((tile - ROWS + 1) == selIndex)
				|| ((tile + ROWS - 1) == selIndex)
				|| ((tile + ROWS + 1) == selIndex);
	}

	/** Return whether a tile has already been selected */
	private boolean isSelected(int index) {
		boolean sel = false;
		for (int i : selected) {
			if (index == i)
				sel = true;
		}
		return sel;
	}

	/** Return whether a tile is selectable */
	private boolean isSelectable(int selIndex) {
		return selected.isEmpty() || (inRange(selIndex) && !isSelected(selIndex));
	}

	/** Handle game over */
	public void handleGameOver() {
		PersistentBoggleMusic.stop(this);

		// Initialize message
		String message = "";

		// If your opponent has finished the game
		if ((PersistentBoggle.getKeyValuewait(OPP_OPP_KEY, "")).equals(userID) ||
				Integer.valueOf(PersistentBoggle.getKeyValuewait(OPP_TIME_KEY, "120")) == 0) {
			// List of opponent's used words
			List<String> oppUsedWords = new LinkedList<String>();
			String serverVal = PersistentBoggle.getKeyValuewait(SERVER_OPP_USED_WORDS_KEY, "");
			if (!serverVal.equals("") && serverVal != null) {
				String[] list = gson.fromJson(serverVal, String[].class);
				for (String s : list)
					oppUsedWords.add(s);
			}

			// If you lost
			if (Integer.valueOf(opponentScore) > this.score) {
				message = "You lost.\nYou scored " + String.valueOf(this.score)
						+ " points with "
						+ String.valueOf(this.usedWords.size())
						+ " words.\nYour opponent scored " + opponentScore
						+ " points with " + String.valueOf(oppUsedWords.size())
						+ " words.";
			}
			// If you tied
			else if (Integer.valueOf(opponentScore) == this.score) {
				message = "You tied!\nYou scored " + String.valueOf(this.score)
						+ " points with "
						+ String.valueOf(this.usedWords.size())
						+ " words!\nYour opponent also scored " + opponentScore
						+ " points with " + String.valueOf(oppUsedWords.size())
						+ " words.";
			}
			// If you won
			else {
				message = "You won!\nYou scored " + String.valueOf(this.score)
						+ " points with "
						+ String.valueOf(this.usedWords.size())
						+ " words!\nYour opponent scored " + opponentScore
						+ " points with " + String.valueOf(oppUsedWords.size())
						+ " words.";
			}
		}
		// If your opponent hasn't finished the game yet
		else {
			message = "You scored " + String.valueOf(this.score)
					+ " points with " + String.valueOf(this.usedWords.size())
					+ " words!\nYour opponent has not yet finished the game.";
		}

		new AlertDialog.Builder(this)
		.setTitle(R.string.persistent_game_over_title)
		.setMessage(message)
		.setCancelable(true)
		.setPositiveButton("Back to Game",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		})
		.setNegativeButton("View Words",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int which) {
				showUsedWords();
				dialog.dismiss();
			}
		}).show();

		if (!PersistentBoggle.getPref(this, GAME_OVER, true)) {
			// Create new leaderboard entry
			LeaderboardEntry entry = new LeaderboardEntry(userID, this.score);

			// If the server is available
			if (KeyValueAPI.isServerAvailable()) {
				String l = PersistentBoggle.getKeyValuewait(LEADERBOARD, "");
				Leaderboard leaderboard;
				if (l.equals(""))
					leaderboard = new Leaderboard(new LinkedList<LeaderboardEntry>());
				else {
					// Get the leaderboard from the server
					//JsonReader reader = new JsonReader(new StringReader(l));
					//reader.setLenient(true);
					leaderboard = gson.fromJson(l, Leaderboard.class);
				}
				// Handle a null leaderboard
				if (leaderboard == null)
					leaderboard = new Leaderboard(new LinkedList<LeaderboardEntry>());
				// Add the leaderboard entry
				leaderboard.addEntry(entry);
				// Serialize the leaderboard
				String serializedLeaderboard = gson.toJson(leaderboard);
				// Set the leaderboard on the server
				PersistentBoggle.setKeyValue(this, LEADERBOARD, serializedLeaderboard);
			}
			else {
				// Get the local leaderboard
				Leaderboard leaderboard = gson.fromJson(
						PersistentBoggle.getPref(this, LEADERBOARD, ""), Leaderboard.class);
				if (leaderboard == null)
					leaderboard = new Leaderboard(new LinkedList<LeaderboardEntry>());
				// Add the leaderboard entry
				leaderboard.addEntry(entry);
				// Serialize the leaderboard
				String serializedLeaderboard = gson.toJson(leaderboard);
				// Set the leaderboard locally
				PersistentBoggle.setPref(this, LEADERBOARD, serializedLeaderboard);
			}
			PersistentBoggle.setPref(this, GAME_OVER, true);
		}
	}

	/** Shows an alert dialog with the used words */
	public void showUsedWords() {
		final CharSequence[] words = this.usedWords
				.toArray(new CharSequence[this.usedWords.size()]);

		new AlertDialog.Builder(this).setTitle(R.string.persistent_words_title)
		.setItems(words, null).setCancelable(true)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		}).show();
	}
}