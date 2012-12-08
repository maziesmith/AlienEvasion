package edu.neu.madcourse.adamgressen.alienevasion;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;

public class AchievementItem extends Activity{
	
	String display;
	boolean locked = true;
	public static List<AchievementItem> listAchievements= new LinkedList<AchievementItem>();
	//Shared Preferences
	public static final String TOTAL_DISTANCE = "Total_Distance";
	public static final String TOTAL_TIME = "Total_Time";
	public static final String TOTAL_ALIENS = "TOTAL_ALIENS";
	public static final String ACHIEVEMENT_PREFS = "Achievement_Prefs";
	static int evaded;
	static float distance;
	static long time;
	
	public AchievementItem(String display){
		this.display = display;
		locked = false;
		
	}
	
	public static void checkAchievement(Context context){
		
		Evade evade = (Evade)context;
		evaded = evade.getEvaded();
		distance = (float) evade.getDist();
		time = evade.getTime();
		
		updateTotalAliens(context, evaded);
		updateTotalDistance(context, (float)distance);
		updateTotalTime(context, time);
		
		if((getTotalAliens(context) > 1000)){
			updateList("Evade 1,000 Aliens");
		}
		if(getTotalDistance(context) > 1){
			updateList("Run Your First Mile");
		}
		if(getTotalDistance(context) > 10){
			updateList("Run Your First 10 Miles");
		}
		if(getTotalDistance(context) > 50){
			updateList("Run Your First 50 Miles");
			
		}
		if(getTotalTime(context) > 1){
			updateList("Completed the run for 1 Hour");
		}
		if(getTotalTime(context) > 5){
			updateList("Completed the run for 5 Hour");
		}
		if(getTotalTime(context) > 10){
			updateList("Completed the run for 10 Hour");
		}
			
		if(evaded > 1000){
			updateList("Evade 1000 aliens in a single run");
			
		}
		else if(getTotalAliens(context) > 1){
			updateList("Evade Your First Alien");
		}
	}
	
	
	
	public static void updateList(String display) {
		AchievementItem a = new AchievementItem(display);
		if( !listAchievements.contains(a))
		listAchievements.add(a);
	}

	public static float getTotalDistance(Context context){
		
		return context.getSharedPreferences(ACHIEVEMENT_PREFS, MODE_PRIVATE).getFloat(TOTAL_DISTANCE, 0f);
	}
	
	public static long getTotalTime(Context context){
		return context.getSharedPreferences(ACHIEVEMENT_PREFS, MODE_PRIVATE).getLong(TOTAL_TIME, 0L);
	}
	
	public static int getTotalAliens(Context context){
		return context.getSharedPreferences(TOTAL_ALIENS, MODE_PRIVATE).getInt(TOTAL_ALIENS, 0);
	}

	public static void updateTotalDistance(Context context,float distance){
		float prevDistance = getTotalDistance(context);
		context.getSharedPreferences(ACHIEVEMENT_PREFS, MODE_PRIVATE).edit().putFloat(TOTAL_DISTANCE, prevDistance + distance);
	}
	
	public static void updateTotalTime(Context context,long time){
		long prevTime = getTotalTime(context);
		context.getSharedPreferences(ACHIEVEMENT_PREFS, MODE_PRIVATE).edit().putLong(TOTAL_TIME, prevTime + time);
	}
	public static void updateTotalAliens(Context context,int aliens){
		int prevAliens = getTotalAliens(context);
		context.getSharedPreferences(ACHIEVEMENT_PREFS, MODE_PRIVATE).edit().putInt(TOTAL_ALIENS, prevAliens + aliens);
	}
	
	
}
