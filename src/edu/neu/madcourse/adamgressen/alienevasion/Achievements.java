package edu.neu.madcourse.adamgressen.alienevasion;

import edu.neu.madcourse.adamgressen.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Achievements extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.alien_evasion_achievements);
		
		String[] achievements = getResources().getStringArray(R.array.achievements_list);
		
		ListView listView = (ListView) findViewById(R.id.achievements_list_view);
		listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, achievements));
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}
	
}
