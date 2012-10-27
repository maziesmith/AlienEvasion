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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.neu.mobileclass.apis.KeyValueAPI;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import edu.neu.madcourse.adamgressen.R;
import edu.neu.madcourse.adamgressen.persistentboggle.PersistentBoggle.BoggleFields;

public class PersistentBoggleGame extends Activity implements PersistentBoggleInterface {
	private static final String TAG = "Boggle";
	private static int ROWS = 5;

	private Timer timer = new Timer();
	private TimerTask task;
	int delay = 1000; //milliseconds
	private int time;
	
	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int retrieveTime() { return this.time; }
	private boolean paused = false;
	private boolean gameOver = false;
	public boolean getGameOver() { return this.gameOver; }

	private static final String BOARD_KEY = "board";
	private static final String SCORE_KEY = "score";
	private static final String TIME_KEY = "time";
	private static final String USED_WORDS_KEY = "used-words";
	private static final String OPP_KEY = "opponent";
	private static final String WORLD_TIME_KEY = "world-time";

	private static final String USER_PREFS = "persistent_user_prefs";
	private static final String USER_ID_KEY = "id";
	private static String userID;

	public void setUserID(String userID) {
		PersistentBoggleGame.userID = userID;
	}
	private static String SERVER_BOARD_KEY;
	private static String SERVER_SCORE_KEY;
	private static String SERVER_USED_WORDS_KEY;
	private static String SERVER_TIME_KEY;
	private static String SERVER_OPP_KEY;
	private static String SERVER_ONLINE_KEY;
	private static String SERVER_WORLD_TIME_KEY;

	private static final String TEAM = "persistence";
	private static final String PASSWORD = "p3rs1st3nc3";
 
	private static String opponent;
	private static String OPP_BOARD_KEY;
	private static String OPP_SCORE_KEY;
	private static String OPP_USED_WORDS_KEY;
	private static String OPP_TIME_KEY;
	private static String OPP_ONLINE_KEY;
	private static String OPP_OPP_KEY;
	private Long remoteTime =0L;
	
	public Long getRemoteTime() {
		return remoteTime;
	}

	public void setRemoteTime(Long remoteTime) {
		this.remoteTime = remoteTime;
	}
	public static String opponentScore = "0"; 
	
	public void setOpponent(String opponent) {
		PersistentBoggleGame.opponent = opponent;
	}

	public static void setOpponentScore(String opponentScore) {
		PersistentBoggleGame.opponentScore = opponentScore;
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
		return usedWordString;
	}

	public void setUsedWordString(String usedWordString) {
		this.usedWordString = usedWordString;
	}
	InputStream is;
	BufferedReader br;

	private static final List<String> die1 = new LinkedList<String>(Arrays.asList("A", "A", "A", "F", "R", "S"));
	private static final List<String> die2 = new LinkedList<String>(Arrays.asList("A", "A", "E", "E", "E", "E"));
	private static final List<String> die3 = new LinkedList<String>(Arrays.asList("A", "A", "F", "I", "R", "S"));
	private static final List<String> die4 = new LinkedList<String>(Arrays.asList("A", "D", "E", "N", "N", "N"));
	private static final List<String> die5 = new LinkedList<String>(Arrays.asList("A", "E", "E", "E", "E", "M"));
	private static final List<String> die6 = new LinkedList<String>(Arrays.asList("A", "E", "E", "G", "M", "U"));
	private static final List<String> die7 = new LinkedList<String>(Arrays.asList("A", "E", "G", "M", "N", "N"));
	private static final List<String> die8 = new LinkedList<String>(Arrays.asList("A", "F", "I", "R", "S", "Y"));
	private static final List<String> die9 = new LinkedList<String>(Arrays.asList("B", "J", "K", "Q", "X", "Z"));
	private static final List<String> die10 = new LinkedList<String>(Arrays.asList("C", "C", "N", "S", "T", "W"));
	private static final List<String> die11 = new LinkedList<String>(Arrays.asList("C", "E", "I", "I", "L", "T"));
	private static final List<String> die12 = new LinkedList<String>(Arrays.asList("C", "E", "I", "L", "P", "T"));
	private static final List<String> die13 = new LinkedList<String>(Arrays.asList("C", "E", "I", "P", "S", "T"));
	private static final List<String> die14 = new LinkedList<String>(Arrays.asList("D", "D", "L", "N", "O", "R"));
	private static final List<String> die15 = new LinkedList<String>(Arrays.asList("D", "H", "H", "L", "O", "R"));
	private static final List<String> die16 = new LinkedList<String>(Arrays.asList("D", "H", "H", "N", "O", "T"));
	private static final List<String> die17 = new LinkedList<String>(Arrays.asList("D", "H", "L", "N", "O", "R"));
	private static final List<String> die18 = new LinkedList<String>(Arrays.asList("E", "I", "I", "I", "T", "T"));
	private static final List<String> die19 = new LinkedList<String>(Arrays.asList("E", "M", "O", "T", "T", "T"));
	private static final List<String> die20 = new LinkedList<String>(Arrays.asList("E", "N", "S", "S", "S", "U"));
	private static final List<String> die21 = new LinkedList<String>(Arrays.asList("F", "I", "P", "R", "S", "Y"));
	private static final List<String> die22 = new LinkedList<String>(Arrays.asList("G", "O", "R", "R", "V", "W"));
	private static final List<String> die23 = new LinkedList<String>(Arrays.asList("H", "I", "P", "R", "R", "Y"));
	private static final List<String> die24 = new LinkedList<String>(Arrays.asList("N", "O", "O", "T", "U", "W"));
	private static final List<String> die25 = new LinkedList<String>(Arrays.asList("O", "O", "O", "T", "T", "U"));

	private static final List<List<String>> THE_DIE = 
			new LinkedList<List<String>>(Arrays.asList(
					die1,die2,die3,die4,die5,die6,
					die7,die8,die9,die10,die11,
					die12,die13,die14,die15,die16,
					die17,die18,die19,die20,die21,
					die22,die23,die24,die25));

	private int score;
	public void setScore(int score) {
		this.score = score;
	}

	public int retrieveScore() { return this.score; }

	private PersistentBogglePuzzleView persistentBogglePuzzleView;
	GsonBuilder gsonb = new GsonBuilder();
	Gson gson = gsonb.create();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		setKeys(this);

		boolean useServer = false;
		String remoteTimeString = PersistentBoggle.getKeyValuewait(SERVER_WORLD_TIME_KEY, "0");
		remoteTime = Long.valueOf(remoteTimeString);
		Log.d(TAG,"Setting the remote time to: "+ remoteTime);
		Long localTime = PersistentBoggle.getPref(this, WORLD_TIME_KEY, (long) 0);
		
		if (remoteTime == 0 && localTime != 0)
			useServer = false;
		else if (localTime == 0 && remoteTime != 0)
			useServer = true;
		else if (remoteTime == 0 && localTime == 0)
			useServer = false;
		else {
			useServer = remoteTime > localTime;
		}
		Log.d(TAG,"UseServer:" + useServer);

		this.board = getBoard(useServer);
		this.score = getScore(useServer);
		this.usedWords = getUsedWords(useServer);
		this.time = getTime(useServer);
		
		// Send board to prefs and server
		PersistentBoggle.setPref(this, BOARD_KEY, board);
		PersistentBoggle.setKeyValuewait(SERVER_BOARD_KEY, board);
		PersistentBoggle.setKeyValuewait(OPP_BOARD_KEY, board);
		Log.d(TAG, "server board key"+SERVER_BOARD_KEY);
		Log.d(TAG, "opp board key"+OPP_BOARD_KEY);
		
		PersistentBoggle.setPref(this, TIME_KEY, time);
		PersistentBoggle.setKeyValuewait(SERVER_TIME_KEY, String.valueOf(time));
		PersistentBoggle.setPref(this, SCORE_KEY, score);
		PersistentBoggle.setKeyValuewait(SERVER_SCORE_KEY, String.valueOf(score));
		PersistentBoggle.setPref(this, USED_WORDS_KEY, this.serializeWords());
		PersistentBoggle.setKeyValuewait(SERVER_USED_WORDS_KEY, this.serializeWords());
		
		persistentBogglePuzzleView = new PersistentBogglePuzzleView(this);
		setContentView(persistentBogglePuzzleView);
		persistentBogglePuzzleView.requestFocus();

		this.task = new TimerTask() {
			public void run() {
				if (!paused) {
					time--;
					if (time <= 0) {
						time = 0;
						gameOver = true;
						task.cancel();
					}
					
					//else if (time % 5 == 0)
						//PersistentBoggle.getKeyValue(getApplicationContext(),OPP_SCORE_KEY, "0",BoggleFields.OPPONENT);
				}
			}
		};
		timer.schedule(this.task, 0, delay);
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
			opponent = PersistentBoggle.getPref(context, OPP_KEY, "");		
		Log.d("Boggle", "opponent"+opponent);
		
		OPP_BOARD_KEY = opponent+"board";
		OPP_SCORE_KEY = opponent+"score";
		OPP_USED_WORDS_KEY = opponent+"used-words";
		OPP_TIME_KEY = opponent+"time";
		OPP_ONLINE_KEY = opponent+"online";
		OPP_OPP_KEY = opponent+"opponent";
		
		PersistentBoggle.getKeyValue(this,OPP_SCORE_KEY, "0",BoggleFields.OPPONENT);
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
		//Log.d(TAG, "onPause");
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
		if (b == "")
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
		if (useServer) {
			String serverVal = "";
			serverVal = PersistentBoggle.getKeyValuewait(SERVER_USED_WORDS_KEY, "");
			if (serverVal != "" && serverVal != null) {
				String[] list = gson.fromJson(serverVal, String[].class);
				for (String s : list)
					value.add(s);
			}
		}
		else {
			String serverVal = PersistentBoggle.getPref(this, USED_WORDS_KEY, "");
			if (serverVal != "" && serverVal != null ) {
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
			int size = remainingDie.size()-1;
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
	/*static private String toBoardString(char[] boardList) {
      StringBuilder buf = new StringBuilder();
      for (char element : boardList) {
         buf.append(element);
      }
      return buf.toString();
   }*/

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
		else if (index == selected.get(selected.size()-1)) {
			String word = "";
			for (int i : selected) {
				word += getTileString(i);
			}
			submitWord(word);
			return 2;
		}
		else
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
				is = this.getAssets().open(length+"/"+firstLetter+".txt");
				br = new BufferedReader(new InputStreamReader(is));
				String line;

				while (!isValid && (line = br.readLine()) != null) {
					isValid = word.trim().equalsIgnoreCase(line.trim());
				}

				br.close();
				is.close();
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
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
		int tile = selected.get(selected.size()-1);
		return ((tile - 1) == selIndex) || ((tile + 1) == selIndex)
				|| ((tile - ROWS) == selIndex) || ((tile + ROWS) == selIndex)
				|| ((tile - ROWS - 1) == selIndex) || ((tile - ROWS + 1) == selIndex)
				|| ((tile + ROWS - 1) == selIndex) || ((tile + ROWS + 1) == selIndex);
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
		return selected.size() == 0 || (inRange(selIndex) && !isSelected(selIndex));
	}
}