/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband3 for more book information.
 ***/

package edu.neu.madcourse.adamgressen.boggle;

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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import edu.neu.madcourse.adamgressen.R;

public class BogglePuzzleView extends View {

	private static final String TAG = "Boggle";

	private static final String SELX = "selX"; 
	private static final String SELY = "selY";
	private static final String VIEW_STATE = "viewState";
	private static final int ID = 42;

	private int boardWidth = 0;									// width of game board
	private int boardHeight = 0;									// height of game board
	private float width;    										// width of one tile
	private float height;   										// height of one tile
	private ArrayList<Integer> selX = new ArrayList<Integer>(); // X index of selection
	private ArrayList<Integer> selY = new ArrayList<Integer>(); // Y index of selection
	private final List<Rect> selRect = new LinkedList<Rect>(Arrays.asList(new Rect()));

	private RelativeLayout relativeLayout;
	private Button pauseBut;
	private int buttonMargin = 0;
	private int buttonWidth = 0;
	private int buttonHeight = 0;

	private final BoggleGame boggleGame;
	private int rows;

	/** Constructors */
	public BogglePuzzleView(Context context) {
		super(context);

		this.boggleGame = (BoggleGame) context;

		this.rows = this.boggleGame.getRows();
		setFocusable(true);
		setFocusableInTouchMode(true);
		
		relativeLayout = new RelativeLayout(boggleGame);
		pauseBut = new Button(boggleGame);
		pauseBut.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.d("pauseBut: ","clicked");
				if (boggleGame.getState()) {
					boggleGame.resumeGame();
					pauseBut.setText("Pause");
				}
				else {
					boggleGame.pauseGame();
					pauseBut.setText("Resume");
				}
			}
		});
		relativeLayout.addView(pauseBut);

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
		buttonMargin = (int)(diff*0.1);
		buttonWidth = (int)(diff*0.8);
		buttonHeight = (int)(h*0.3);
		//pauseBut.measure(buttonWidth, buttonHeight);
		
		LayoutParams params = new LayoutParams(0, 0);
		relativeLayout.setLayoutParams(params);
		//relativeLayout.layout(h+buttonMargin, buttonMargin, 0, 0);
		pauseBut.setHeight(buttonHeight);
		pauseBut.setWidth(buttonWidth);
		//pauseBut.layout(h+buttonMargin, buttonMargin, 0, 0);
			//h+buttonMargin+buttonWidth, buttonMargin+buttonHeight);

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
		// Draw the background...
		Paint background = new Paint();
		background.setColor(getResources().getColor(
				R.color.puzzle_background));
		canvas.drawRect(0, 0, boardWidth, boardHeight, background);

		// Draw the board...

		// Define colors for the grid lines
		Paint dark = new Paint();
		dark.setColor(getResources().getColor(R.color.puzzle_dark));

		Paint hilite = new Paint();
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

		if (!this.boggleGame.getState()) {
			// Draw the letters...
			// Define color and style for numbers
			Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
			foreground.setColor(getResources().getColor(
					R.color.puzzle_foreground));
			foreground.setStyle(Style.FILL);
			foreground.setTextSize(height * 0.75f);
			foreground.setTextScaleX(width / height);
			foreground.setTextAlign(Paint.Align.CENTER);

			// Draw the number in the center of the tile
			FontMetrics fm = foreground.getFontMetrics();
			// Centering in X: use alignment (and X at midpoint)
			float x = width / 2;
			// Centering in Y: measure ascent/descent first
			float y = height / 2 - (fm.ascent + fm.descent) / 2;
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < rows; j++) {
					canvas.drawText(this.boggleGame.getTileString(i, j), i
							* width + x, j * height + y, foreground);
				}
			}

			// Draw the selection...
			Log.d(TAG, "selRect=" + selRect);
			Paint selected = new Paint();
			selected.setColor(getResources().getColor(
					R.color.puzzle_selected));
			// Draw all selected rectangles
			for (Rect r : selRect) {
				canvas.drawRect(r, selected);
			}
		}
		
		relativeLayout.draw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);

		float eventX = event.getX();
		float eventY = event.getY();

		if ((eventX < boardWidth) && (eventY < boardHeight)) {
			select((int) (eventX / width),
					(int) (eventY / height));
		}

		Log.d(TAG, "onTouchEvent: x " + selX + ", y " + selY);
		return true;
	}

	private void select(int x, int y) {
		Vibrator vb = (Vibrator) boggleGame.getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(100);
		int event = this.boggleGame.selectTile(x + (y * rows));
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
		}
		else if (event == 2) {
			selX.clear();
			selY.clear();
			for (Rect r : selRect) {
				invalidate(r);
			}
			selRect.clear();
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
	/*
   private void getRects(ArrayList<Integer> xs, ArrayList<Integer> ys, List<Rect> rects) {
	   if (xs != null && ys != null) {
		   for (int i=0; i<xs.size(); i++) {
			   rects.set(i, new Rect());
			   getRect(xs.get(i),ys.get(i),rects.get(i));
		   }
	   }
   }*/
}