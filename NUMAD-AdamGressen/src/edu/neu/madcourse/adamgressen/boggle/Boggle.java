/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband3 for more book information.
***/
package edu.neu.madcourse.adamgressen.boggle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import edu.neu.madcourse.adamgressen.R;

public class Boggle extends Activity implements OnClickListener {
   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.boggle_main);

      // Set up click listeners for all the buttons
      View continueButton = findViewById(R.id.continue_button);
      continueButton.setOnClickListener(this);
      View newButton = findViewById(R.id.new_button);
      newButton.setOnClickListener(this);
      View aboutButton = findViewById(R.id.about_button);
      aboutButton.setOnClickListener(this);
      View exitButton = findViewById(R.id.exit_button);
      exitButton.setOnClickListener(this);
   }

   @Override
   protected void onResume() {
      super.onResume();
      BoggleMusic.play(this, R.raw.main);
   }

   @Override
   protected void onPause() {
      super.onPause();
      BoggleMusic.stop(this);
   }

   public void onClick(View v) {
      switch (v.getId()) {
      case R.id.continue_button:
         startGame();
         break;
         // ...
      case R.id.about_button:
         Intent i = new Intent(this, BoggleAbout.class);
         startActivity(i);
         break;
      // More buttons go here (if any) ...
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
      inflater.inflate(R.menu.boggle_menu, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      case R.id.settings:
         startActivity(new Intent(this, BogglePrefs.class));
         return true;
      // More items go here (if any) ...
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
	   				startGame();
	   			}
	   		})
	   		.setNegativeButton("No", new DialogInterface.OnClickListener() {
	   			public void onClick(DialogInterface arg0, int arg1) {
	   			}
	   		})
	   .show();
   }

   /** Start a new game */
   private void startGame() {
      Intent intent = new Intent(this, BoggleGame.class);
      startActivity(intent);
   }
}