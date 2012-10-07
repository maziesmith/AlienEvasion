/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband3 for more book information.
***/
package edu.neu.madcourse.adamgressen.boggle;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import edu.neu.madcourse.adamgressen.R;

public class BoggleGame extends Activity {
   private static final String TAG = "Boggle";
   private static int rows = 4;

   private Timer timer = new Timer();
   
   private static final String STORED_BOARD = "board";

   private String board;
   
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
   
   private List<List<String>> theDie = 
		   new LinkedList<List<String>>(Arrays.asList(
				   die1,die2,die3,die4,die5,die6,
				   die7,die8,die9,die10,die11,
				   die12,die13,die14,die15,die16));
   
   private List<List<String>> remainingDie;
   
   private int score;

   private BogglePuzzleView bogglePuzzleView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Log.d(TAG, "onCreate");

      board = getBoard();
      
      score = 0;

      bogglePuzzleView = new BogglePuzzleView(this);
      setContentView(bogglePuzzleView);
      bogglePuzzleView.requestFocus();
   }

   @Override
   protected void onResume() {
      super.onResume();
      BoggleMusic.play(this, R.raw.game);
   }

   @Override
   protected void onPause() {
      super.onPause();
      Log.d(TAG, "onPause");
      BoggleMusic.stop(this);
      // Save the current puzzle
      getPreferences(MODE_PRIVATE).edit().putString(STORED_BOARD, board).commit();
   }
   
   /** Given a difficulty level, come up with a new puzzle */
   private String getBoard() {
      String newBoard = getPreferences(MODE_PRIVATE).getString(STORED_BOARD, createBoard());
      return newBoard;
   }
   
   /** Generate a boggle board */
   private String createBoard() {
	   // The board to generate
	   board = "";
	   // The selected die index
	   int dieIndex;
	   // Reset the remainingDie list
	   remainingDie = theDie;
	   Random rand = new Random();
	   for (int i = 0; i < (rows * rows); i++) {
		   int size = remainingDie.size()-1;
		   if (size > 0) {
			   // The selected die index
			   dieIndex = rand.nextInt(size);
		   }
		   else
			   dieIndex = 0;
		   // Choose letter
		   char chosenChar = chooseLetter(dieIndex);
		   // Add char to board
		   board += chosenChar;
	   }
	   Log.d(TAG, "board generated: " + board);
	   return board;
   }
   
   /** Roll the die and choose the letter */
   private char chooseLetter(int dieIndex) {
	   Random letterRand = new Random();
	   // Get chosen die
	   List<String> chosenDie = remainingDie.get(dieIndex);
	   // Get a char from the chosen die list
	   char chosenChar = chosenDie.get(letterRand.nextInt(5)).toCharArray()[0];
	   // Remove die from remainingDie list 
	   remainingDie.remove(dieIndex);
	   // Return the chosen character
	   return chosenChar;
   }
   
   /** Get number of rows */
   public int getRows() {
	   return rows;
   }

   /** Convert an array into a board string */
   static private String toBoardString(char[] boardList) {
      StringBuilder buf = new StringBuilder();
      for (char element : boardList) {
         buf.append(element);
      }
      return buf.toString();
   }

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
      return fromBoardString(board)[y * rows + x];
   }

   /** Return a string for the tile at the given coordinates */
   protected String getTileString(int x, int y) {
      char v = getTile(x, y);
      return String.valueOf(v);
   }
   
   /** List of indices of selected tiles */
   private List<Integer> selected;
   
   /** Select a tile */
   private void selectTile(int index) {
	   if (isSelectable(index))
		   selected.add(index);
   }
   
   /** Returns true if a word is valid */
   private boolean isValidWord(String word) {
	   return true;
   }
   
   /** Calculate point value of word */
   private int calculateScore(String word) {
	   int wordValue = word.length() - 2;
	   if (wordValue < 0)
		   wordValue = 0;
	   return wordValue;
   }
   
   /** Submit word */
   private void submitWord(String word) {
	   if (isValidWord(word)) {
		   // calculate points and add to score
		   score += calculateScore(word);
	   }
   }
   
   /** Return whether a tile is in range of a previously selected tile */
   private boolean inRange(int selIndex) {
	   boolean inrange = false;
	   for (int tile : selected) {
		   if (((tile - 1) == selIndex) || ((tile + 1) == selIndex)
				   || ((tile - rows) == selIndex) || ((tile + rows) == selIndex)
				   || ((tile - rows - 1) == selIndex) || ((tile - rows + 1) == selIndex)
				   || ((tile + rows - 1) == selIndex) || ((tile + rows + 1) == selIndex))
			   inrange = true;
	   }
	   return inrange;
   }
   
   /** Return whether a tile is selectable */
   private boolean isSelectable(int selIndex) {
	   return selected.size() == 0 || inRange(selIndex);
   }
}