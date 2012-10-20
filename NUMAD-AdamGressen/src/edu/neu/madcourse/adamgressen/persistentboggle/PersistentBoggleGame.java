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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import edu.neu.madcourse.adamgressen.R;

public class PersistentBoggleGame extends Activity {
   private static final String TAG = "Boggle";
   private static int ROWS = 5;

   private Timer timer = new Timer();
   private TimerTask task;
   int delay = 1000; //milliseconds
   private int time;
   public int retrieveTime() { return this.time; }
   private boolean paused = false;
   private boolean gameOver = false;
   public boolean getGameOver() { return this.gameOver; }
   
   private static final String STORED_BOARD = "board";
   private static final String BOARD_PREFS = "persistent_board-prefs";
   private static final String SCORE_KEY = "score";
   private static final String TIME_KEY = "time";
   private static final String USED_WORDS_KEY = "used-words";
   
   private static final String USER_PREFS = "persistent_user_prefs";
	private static final String USER_ID = "id";
	private static String userID;
	
	private static String SERVER_BOARD_KEY;
	private static String SERVER_SCORE_KEY;
	private static String SERVER_USED_WORDS_KEY;
	private static String SERVER_TIME;
	private static String SERVER_OPP;
	private static String SERVER_ONLINE;
	private static String SERVER_GOOGLE_ID;
	
	private static final String TEAM = "persistence";
	private static final String PASSWORD = "p3rs1st3nc3";
	
	private static String opponent;
	private static String OPP_BOARD_KEY;

   private String board;
   private List<String> usedWords = new LinkedList<String>();
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
   public int retrieveScore() { return this.score; }

   private PersistentBogglePuzzleView persistentBogglePuzzleView;
   private SharedPreferences prefs;
   GsonBuilder gsonb = new GsonBuilder();
   Gson gson = gsonb.create();

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Log.d(TAG, "onCreate");

      setKeys();
      
      this.prefs = getSharedPreferences(BOARD_PREFS, MODE_PRIVATE);
      
      this.board = getBoard();
      this.score = getScore();
      this.usedWords = getUsedWords();
      this.time = getTime();
      
      persistentBogglePuzzleView = new PersistentBogglePuzzleView(this);
      setContentView(persistentBogglePuzzleView);
      persistentBogglePuzzleView.requestFocus();
      
      this.task = new TimerTask() {
          public void run() {
        	  if (!paused) {
        		  time--;
        		  //Log.d("time left: ", String.valueOf(time));
        		  if (time <= 0) {
        			  time = 0;
        			  gameOver = true;
        			  task.cancel();
        		  }
        	  }
          }
      };
      timer.schedule(this.task, 0, delay);
   }

   private void setKeys() {
	   userID = getSharedPreferences(USER_PREFS, MODE_PRIVATE).getString(USER_ID, "");
	   Log.d(TAG, "user id"+userID);
		
		SERVER_BOARD_KEY = userID+"board";
		SERVER_SCORE_KEY = userID+"score";
		SERVER_USED_WORDS_KEY = userID+"user-words";
		SERVER_TIME = userID+"time";
		SERVER_OPP = userID+"opponent";
		SERVER_ONLINE = userID+"online";
		SERVER_GOOGLE_ID = userID+"id";
		
		opponent = KeyValueAPI.get(TEAM, PASSWORD, SERVER_OPP);
		Log.d(TAG, "opponent"+opponent);
		OPP_BOARD_KEY = opponent+"board";
   }
   
   /** Return game state (paused) */
   public boolean getState() {
	   return paused;
   }
   
   /** Resume game */
   public void resumeGame() {
	   paused = false;
	   PersistentBoggleMusic.playMusic(this, R.raw.game);
   }
   
   @Override
   protected void onResume() {
      super.onResume();
      resumeGame();
   }

   /** Pause the game */
   public void pauseGame() {
	   paused = true;
	   prefs.edit().putInt(TIME_KEY, this.time).commit();
	   PersistentBoggleMusic.stop(this);
   }
   
   @Override
   protected void onPause() {
      super.onPause();
      //Log.d(TAG, "onPause");
      pauseGame();
   }
   
   /** Return the board or trigger createBoard */
   private String getBoard() {
	   String b = "";
	   try {
		   if (KeyValueAPI.isServerAvailable()) {// && (Integer.parseInt(KeyValueAPI.get(TEAM, PASSWORD, SERVER_TIME)) != 0)) {
			   b = KeyValueAPI.get(TEAM, PASSWORD, OPP_BOARD_KEY);
			   Log.d(TAG, "board getboard: "+b);
		   }
		   else {
			   b = prefs.getString(STORED_BOARD, "");
		   }
	   }
	   catch (Exception e) {}
	   if (b == "")
		   b = createBoard();
	   return b;
   }
   private int getScore() {
	   return prefs.getInt(SCORE_KEY, 0);
   }
   private int getTime() {
	   return prefs.getInt(TIME_KEY, 120);
   }
   private List<String> getUsedWords() {
	   String value = prefs.getString(USED_WORDS_KEY, null);

	   if (value == null)
		   return new LinkedList<String>();
	   else {
		   String[] list = gson.fromJson(value, String[].class);
		   List<String> returnList = new LinkedList<String>();
		   for (String s : list)
		   {
			   returnList.add(s);
		   }
		   return returnList;
	   }
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
	   getSharedPreferences(BOARD_PREFS, MODE_PRIVATE).edit().putString(STORED_BOARD, board).commit();
	   if (KeyValueAPI.isServerAvailable()) {
		   Log.d(TAG, "server board key"+SERVER_BOARD_KEY);
		   KeyValueAPI.put(TEAM, PASSWORD, SERVER_BOARD_KEY, board);
		   Log.d(TAG, "opp board key"+OPP_BOARD_KEY);
		   KeyValueAPI.put(TEAM, PASSWORD, OPP_BOARD_KEY, board);
	   }
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
		   getSharedPreferences(BOARD_PREFS, MODE_PRIVATE).edit().putInt(SCORE_KEY, this.score).commit();
		   
		   usedWords.add(word.trim().toLowerCase());
		   String value = gson.toJson(usedWords);
		   getSharedPreferences(BOARD_PREFS, MODE_PRIVATE).edit().putString(USED_WORDS_KEY, value).commit();
		   PersistentBoggleMusic.playSound(this, R.raw.reward);
	   }
	   else
		   PersistentBoggleMusic.playSound(this, R.raw.fail);
	   //Log.d("Score: ", String.valueOf(this.score));
	   selected.clear();
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