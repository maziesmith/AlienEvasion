/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband3 for more book information.
 ***/

package edu.neu.madcourse.adamgressen.persistentboggle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import edu.neu.madcourse.adamgressen.R;

public class PersistentBogglePuzzleView extends View {

	private static final String TAG = "Persistent Boggle";

	private static final String SELX = "selX"; 
	private static final String SELY = "selY";
	private static final String VIEW_STATE = "viewState";
	private static final int ID = 42;

	private int boardWidth = 0;									    // width of game board
	private int boardHeight = 0;									// height of game board
	private float width;    										// width of one tile
	private float height;   										// height of one tile
	private ArrayList<Integer> selX = new ArrayList<Integer>(); // X index of selection
	private ArrayList<Integer> selY = new ArrayList<Integer>(); // Y index of selection
	private final List<Rect> selRect = new LinkedList<Rect>(Arrays.asList(new Rect()));

	private String time = "Time: 120";
	public void setTime(int t) {
		this.time = "Time: "+String.valueOf(t);
		invalidate(timeRect);
	}
	private String score = "Score: 0";
	public void setScore(int s) {
		this.score = "Score: "+String.valueOf(s);
		invalidate(scoreRect);
	}
	private String opponentScore = "Opponent Score: 0";
	public void setOpponentScore(String s) {
		this.opponentScore = "Opponent Score: "+s;
		invalidate(oppScoreRect);
	}
	
	Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint background = new Paint();
	Paint dark = new Paint();
	Paint hilite = new Paint();
	Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint selected = new Paint();
	
	private Rect pauseBut;
	private int pauseTextX;
	private int pauseTextY;
	private String pauseText;
	private Rect quitBut;
	private int quitTextX;
	private int quitTextY;
	private String quitText = "Exit";
	private int buttonMargin = 0;
	private int buttonWidth = 0;
	private int buttonHeight = 0;
	private int scoreX;
	private int scoreY;
	private Rect scoreRect = new Rect();
	private int oppScoreX;
	private int oppScoreY;
	private Rect oppScoreRect = new Rect();
	private int timeX;
	private int timeY;
	private Rect timeRect = new Rect();

	private final PersistentBoggleGame persistentBoggleGame;
	private int rows;
	
	private boolean triggeredGameOver = false;

	/** Constructors */
	public PersistentBogglePuzzleView(Context context) {
		super(context);

		this.persistentBoggleGame = (PersistentBoggleGame) context;
		
		if (this.persistentBoggleGame.getGameOver())
			pauseText = "View Words";
		else
			pauseText = "Pause";
		
		this.rows = this.persistentBoggleGame.getRows();
		setFocusable(true);
		setFocusableInTouchMode(true);

		setId(ID);
	}
	
	@Override
	protected Parcelable onSaveInstanceState() { 
		Parcelable p = super.onSaveInstanceState();
		Log.d(TAG, "onSaveInstanceState");
		Bundle bundle = new Bundle();
		bundle.putIntegerArrayList(SELX, selX);
		bundle.putIntegerArrayList(SELY, selY);
		bundle.putParcelable(VIEW_STATE, p);
		return bundle;
	}
	@Override
	protected void onRestoreInstanceState(Parcelable state) { 
		Log.d(TAG, "onRestoreInstanceState");
		Bundle bundle = (Bundle) state;
		selectMultiple(selX, selY);
		super.onRestoreInstanceState(bundle.getParcelable(VIEW_STATE));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// Make the width and height equal to the height
		//   because of landscape mode
		int newSize = h;
		boardWidth = newSize;
		boardHeight = newSize;

		// Generate size of buttons
		int diff = w-h;
		buttonMargin = (int)(diff*0.06);
		buttonWidth = (int)(diff*0.8);
		buttonHeight = (int)(h*0.11);
		
		// Define color and style for numbers
		textPaint.setColor(getResources().getColor(
				R.color.puzzle_foreground));
		textPaint.setStyle(Style.FILL);
		textPaint.setTextSize(buttonHeight * 0.5f);
		textPaint.setTextScaleX(w / h);
		textPaint.setTextAlign(Paint.Align.CENTER);
		
		timeX = h + buttonMargin + (buttonWidth / 2);
		timeY = buttonMargin + (buttonHeight / 2);
		timeRect = new Rect(h+buttonMargin, buttonMargin,
				h+buttonMargin+buttonWidth, buttonMargin+buttonHeight);
		
		scoreX = h + buttonMargin + (buttonWidth / 2);
		scoreY = (buttonMargin*2) + (buttonHeight*3/2);
		scoreRect = new Rect(h+buttonMargin, (buttonMargin*2)+buttonHeight,
				h+buttonMargin+buttonWidth, (buttonMargin*2)+(buttonHeight*2));
		
		oppScoreX = h + buttonMargin + (buttonWidth / 2);
		oppScoreY = (buttonMargin*3) + (buttonHeight*5/2);
		oppScoreRect = new Rect(h+buttonMargin, (buttonMargin*3)+(buttonHeight*2),
				h+buttonMargin+buttonWidth, (buttonMargin*3)+(buttonHeight*3));
		
		pauseBut = new Rect(h+buttonMargin, (buttonMargin*4)+(buttonHeight*3),
				h+buttonMargin+buttonWidth, (buttonMargin*4)+(buttonHeight*4));
		pauseTextX = h + buttonMargin + (buttonWidth / 2);
		pauseTextY = (buttonMargin*4) + (buttonHeight*7/2);
		
		quitBut = new Rect(h+buttonMargin, (buttonMargin*5)+(buttonHeight*4),
				h+buttonMargin+buttonWidth, (buttonMargin*5)+(buttonHeight*5));
		quitTextX = h + buttonMargin + (buttonWidth / 2);
		quitTextY = (buttonMargin*5) + (buttonHeight*9/2);

		// Generate size of letters
		width = newSize / (float)rows;
		height = newSize / (float)rows;
		//getRects(selX, selY, selRect);
		Log.d(TAG, "onSizeChanged: width " + width + ", height "
				+ height);
		super.onSizeChanged(newSize, newSize, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		setTime(this.persistentBoggleGame.retrieveTime());
		setScore(this.persistentBoggleGame.retrieveScore());
		setOpponentScore(this.persistentBoggleGame.retrieveOpponentScore());
		
		if (!triggeredGameOver && this.persistentBoggleGame.getGameOver()) {
			pauseText = "View Words";
			invalidate(pauseBut);
			this.persistentBoggleGame.handleGameOver();
			triggeredGameOver = true;
		}
		
		// Draw the background...
		background.setColor(getResources().getColor(
				R.color.puzzle_background));
		canvas.drawRect(0, 0, boardWidth, boardHeight, background);

		// Draw the board...

		// Define colors for the grid lines
		dark.setColor(getResources().getColor(R.color.puzzle_dark));

		hilite.setColor(getResources().getColor(R.color.puzzle_hilite));

		// Draw the grid lines
		for (int i = 0; i < rows; i++) {
			canvas.drawLine(0, i * height, boardWidth, i * height,
					dark);
			canvas.drawLine(0, i * height + 1, boardWidth, i * height
					+ 1, hilite);
			canvas.drawLine(i * width, 0, i * width, boardHeight,
					dark);
			canvas.drawLine(i * width + 1, 0, i * width + 1,
					boardHeight, hilite);
		}
		
		// Define color and style for numbers
		foreground.setColor(getResources().getColor(
				R.color.puzzle_foreground));
		foreground.setStyle(Style.FILL);
		foreground.setTextSize(height * 0.75f);
		foreground.setTextScaleX(width / height);
		foreground.setTextAlign(Paint.Align.CENTER);

		// Draw the number in the center of the tile
		FontMetrics fm = foreground.getFontMetrics();
		
		// Draw time and score
		canvas.drawText(this.time, timeX, timeY, textPaint);
		canvas.drawText(this.score, scoreX, scoreY, textPaint);
		canvas.drawText(this.opponentScore, oppScoreX, oppScoreY, textPaint);

		if (!this.persistentBoggleGame.getState()) {
			// Draw the letters...
			// Centering in X: use alignment (and X at midpoint)
			float x = width / 2;
			// Centering in Y: measure ascent/descent first
			float y = height / 2 - (fm.ascent + fm.descent) / 2;
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < rows; j++) {
					canvas.drawText(this.persistentBoggleGame.getTileString(i, j), i
							* width + x, j * height + y, foreground);
				}
			}
			
			// Draw the selection...
			selected.setColor(getResources().getColor(
					R.color.puzzle_selected));
			// Draw all selected rectangles
			for (Rect r : selRect) {
				canvas.drawRect(r, selected);
			}
		}
		
		// Draw buttons
		canvas.drawRect(pauseBut, background);
		canvas.drawText(pauseText, pauseTextX, pauseTextY, textPaint);
		canvas.drawRect(quitBut, background);
		canvas.drawText(quitText, quitTextX, quitTextY, textPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);

		float eventX = event.getX();
		float eventY = event.getY();

		// whether the game is paused or not
		boolean state = this.persistentBoggleGame.getState();
		// whether the game is over or not
		boolean gameOver = this.persistentBoggleGame.getGameOver();
		
		// If the game is over, remove selected rectangles
		if (gameOver && !selRect.isEmpty()) {
			for (Rect r : selRect)
				invalidate(r);
			selRect.clear();
		}
		
		if ((eventX < boardWidth) && (eventY < boardHeight) &&
				!state && !gameOver) {
			select((int) (eventX / width),
					(int) (eventY / height));
		}
		else if (eventX >= pauseBut.left && eventX <= pauseBut.right &&
				eventY >= pauseBut.top && eventY <= pauseBut.bottom) {
			if (state && !gameOver) {
				this.persistentBoggleGame.resumeGame();
				this.pauseText = "Pause";
			}
			else if (!gameOver) {
				this.persistentBoggleGame.pauseGame();
				this.pauseText = "Resume";
			}
			else {
				this.persistentBoggleGame.showUsedWords();
			}
			// Invalidate the whole screen so the board can be hidden on pause
			invalidate();
		}
		else if (eventX >= quitBut.left && eventX <= quitBut.right &&
				eventY >= quitBut.top && eventY <= quitBut.bottom) {
			this.persistentBoggleGame.finish();
		}
		
		//Log.d(TAG, "onTouchEvent: x " + selX + ", y " + selY);
		return true;
	}

	private void select(int x, int y) {
		Vibrator vb = (Vibrator) persistentBoggleGame.getSystemService(Context.VIBRATOR_SERVICE);
		int event = this.persistentBoggleGame.selectTile(x + (y * rows));
		if (event == 1) {
			//invalidate(selRect.get(selRect.size()-1));
			int newSelX = Math.min(Math.max(x, 0), 8);
			int newSelY = Math.min(Math.max(y, 0), 8);
			// Add selected indices to x and y lists
			selX.add(newSelX);
			selY.add(newSelY);
			// Get newly selected rectangle
			Rect rect = getRect(newSelX, newSelY, new Rect());
			// Add rectangle to selected list
			selRect.add(rect);
			invalidate(selRect.get(selRect.size()-1));
			vb.vibrate(10);
		}
		else if (event == 2) {
			selX.clear();
			selY.clear();
			for (Rect r : selRect)
				invalidate(r);
			selRect.clear();
		}
		else if (event == 3) {
			// Remove last selected
			selX.remove(selX.size()-1);
			selY.remove(selY.size()-1);
			invalidate(selRect.get(selRect.size()-1));
			selRect.remove(selRect.size()-1);
		}
	}

	private void selectMultiple(ArrayList<Integer> xs, ArrayList<Integer> ys) {
		for (int i = 0; i < xs.size(); i++) {
			select(xs.get(i),ys.get(i));
		}
	}

	private Rect getRect(int x, int y, Rect rect) {
		rect.set((int) (x * width), (int) (y * height), (int) (x
				* width + width), (int) (y * height + height));
		return rect;
	}
}