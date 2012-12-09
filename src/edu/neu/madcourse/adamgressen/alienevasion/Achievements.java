package edu.neu.madcourse.adamgressen.alienevasion;

import java.util.LinkedList;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import edu.neu.madcourse.adamgressen.R;

public class Achievements extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.alien_evasion_achievements);

		List<String> unlockedach = new LinkedList<String>();

		//AchievementItem.updateList("Evade Your First Alien");
		for(AchievementItem a : AchievementItem.listAchievements){
			unlockedach.add(a.display);
		}


		String[] achievementarray = getResources().getStringArray(R.array.achievements_list);

		ListView listView = (ListView) findViewById(R.id.achievements_list_view);
		AchAdapter adapter = new AchAdapter(getApplicationContext(), android.R.layout.simple_list_item_checked, achievementarray);
		adapter.setUnlocledList(unlockedach, achievementarray);
		listView.setAdapter(adapter);

	}
	
	@Override
	protected void onResume() {
		super.onResume();

		Sounds.playMusic(this, R.raw.alien_evasion_main);
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		Sounds.stop(this);
	}


}


