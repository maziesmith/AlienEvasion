package edu.neu.madcourse.adamgressen.alienevasion;

import edu.neu.madcourse.adamgressen.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class Evasions extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ListView evasionsList = new ListView(this);
		
		setContentView(evasionsList);
	}
	
}
