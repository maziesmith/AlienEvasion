package edu.neu.madcourse.adamgressen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import edu.neu.madcourse.adamgressen.sudoku.Sudoku;
import edu.neu.mobileClass.*;

public class Main extends Activity implements OnClickListener {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setTitle("Adam Gressen");
        
        // Set up click listeners for buttons
        View teamButton = findViewById(R.id.team_button);
        teamButton.setOnClickListener(this);
        View sudokuButton = findViewById(R.id.sudoku_button);
        sudokuButton.setOnClickListener(this);
        View errorButton = findViewById(R.id.error_button);
        errorButton.setOnClickListener(this);
        View exitButton = findViewById(R.id.exit_button);
        exitButton.setOnClickListener(this);
        
        PhoneCheckAPI.doAuthorization(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    // Handle click events for all buttons
    @SuppressWarnings("null")
	public void onClick(View v) {
        switch (v.getId()) {
        // Team button click event
        case R.id.team_button:
        	String version = "";
        	try {
				version = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				version = "";
			}
			new AlertDialog.Builder(this)
			.setMessage("Name: Adam Gressen\nEmail: gressen.a@husky.neu.edu\nVersion: "+version)
			.setTitle("Team Members")
			.setCancelable(true)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int id) {  
					dialog.dismiss();
				}
			}).show();
        	break;
        // Sudoku button click event
        case R.id.sudoku_button:
           Intent j = new Intent(this, Sudoku.class);
           startActivity(j);
           break;
        // Error button click event
        case R.id.error_button:
           String crash = null;
           crash.charAt(0);
           break;
        case R.id.exit_button:
           finish();
           break;
        }
     }
}