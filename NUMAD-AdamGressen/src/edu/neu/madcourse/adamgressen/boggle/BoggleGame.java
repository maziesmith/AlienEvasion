/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband3 for more book information.
***/
package edu.neu.madcourse.adamgressen.boggle;

//import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
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

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import edu.neu.madcourse.adamgressen.R;

public class BoggleGame extends Activity {
   private static final String TAG = "Boggle";
   private static int ROWS = 4;

   private Timer timer = new Timer();
   private TimerTask task;
   int delay = 1000; //milliseconds
   private int time;
   public int retrieveTime() { return this.time; }
   private boolean paused = false;
   private boolean gameOver = false;
   public boolean getGameOver() { return this.gameOver; }
   
   private static final String STORED_BOARD = "board";
   private static final String BOARD_PREFS = "board-prefs";
   private static final String SCORE_KEY = "score";
   private static final String TIME_KEY = "time";
   private static final String USED_WORDS_KEY = "used-words";

   private String board;
   private List<String> usedWords = new LinkedList<String>();
   InputStream is;
   BufferedReader br;
   
   private static final List<String> die1 = new LinkedList<String>(Arrays.asList("A", "A", "E", "E", "G", "N"));
   private static final List<String> die2 = new LinkedList<String>(Arrays.asList("E", "L", "R", "T", "T", "Y"));
   private static final List<String> die3 = new LinkedList<String>(Arrays.asList("A", "O", "O", "T", "T", "W"));
   private static final List<String> die4 = new LinkedList<String>(Arrays.asList("A", "B", "B", "J", "O", "O"));
   private static final List<String> die5 = new LinkedList<String>(Arrays.asList("E", "H", "R", "T", "V", "W"));
   private static final List<String> die6 = new LinkedList<String>(Arrays.asList("C", "I", "M", "O", "T", "U"));
   private static final List<String> die7 = new LinkedList<String>(Arrays.asList("D", "I", "S", "T", "T", "Y"));
   private static final List<String> die8 = new LinkedList<String>(Arrays.asList("E", "I", "O", "S", "S", "T"));
   private static final List<String> die9 = new LinkedList<String>(Arrays.asList("D", "E", "L", "R", "V", "Y"));
   private static final List<String> die10 = new LinkedList<String>(Arrays.asList("A", "C", "H", "O", "P", "S"));
   private static final List<String> die11 = new LinkedList<String>(Arrays.asList("H", "I", "M", "N", "Qu", "U"));
   private static final List<String> die12 = new LinkedList<String>(Arrays.asList("E", "E", "I", "N", "S", "U"));
   private static final List<String> die13 = new LinkedList<String>(Arrays.asList("E", "E", "G", "H", "N", "W"));
   private static final List<String> die14 = new LinkedList<String>(Arrays.asList("A", "F", "F", "K", "P", "S"));
   private static final List<String> die15 = new LinkedList<String>(Arrays.asList("H", "L", "N", "N", "R", "Z"));
   private static final List<String> die16 = new LinkedList<String>(Arrays.asList("D", "E", "I", "L", "R", "X"));
   
   private static final List<List<String>> THE_DIE = 
		   new LinkedList<List<String>>(Arrays.asList(
				   die1,die2,die3,die4,die5,die6,
				   die7,die8,die9,die10,die11,
				   die12,die13,die14,die15,die16));
   
   private int score;
   public int retrieveScore() { return this.score; }

   private BogglePuzzleView bogglePuzzleView;
   private SharedPreferences prefs;
   GsonBuilder gsonb = new GsonBuilder();
   Gson gson = gsonb.create();

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Log.d(TAG, "onCreate");

      this.prefs = getSharedPreferences(BOARD_PREFS, MODE_PRIVATE);
      
      this.board = getBoard();
      this.score = getScore();
      this.usedWords = getUsedWords();
      this.time = getTime();
      
      bogglePuzzleView = new BogglePuzzleView(this);
      setContentView(bogglePuzzleView);
      bogglePuzzleView.requestFocus();
      
      this.task = new TimerTask() {
          public void run() {
        	  if (!paused) {
        		  time--;
        		  Log.d("time left: ", String.valueOf(time));
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

   /** Return game state (paused) */
   public boolean getState() {
	   return paused;
   }
   
   /** Resume game */
   public void resumeGame() {
	   paused = false;
	   BoggleMusic.play(this, R.raw.game);
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
	   BoggleMusic.stop(this);
   }
   
   @Override
   protected void onPause() {
      super.onPause();
      Log.d(TAG, "onPause");
      pauseGame();
   }
   
   /** Return the board or trigger createBoard */
   private String getBoard() {
	   String b = prefs.getString(STORED_BOARD, "");
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
		   bogglePuzzleView.setScore(this.score);
		   getSharedPreferences(BOARD_PREFS, MODE_PRIVATE).edit().putInt(SCORE_KEY, this.score).commit();
		   
		   usedWords.add(word.trim().toLowerCase());
		   String value = gson.toJson(usedWords);
		   getSharedPreferences(BOARD_PREFS, MODE_PRIVATE).edit().putString(USED_WORDS_KEY, value).commit();
		   BoggleMusic.play(this, R.raw.reward, false);
	   }
	   else
		   BoggleMusic.play(this, R.raw.fail, false);
	   Log.d("Score: ", String.valueOf(this.score));
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