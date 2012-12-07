package edu.neu.madcourse.adamgressen.persistentboggle;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import edu.neu.madcourse.adamgressen.R;

public class BoggleService extends IntentService {

	public BoggleService(){
		super("Boggle Service");
	}
	
	public BoggleService(String name) {
		super("Boggle Service");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String opponent_opp = PersistentBoggle.getKeyValuewait(PersistentBoggleGame.getOPP_OPP_KEY(),"");
		String UserID = PersistentBoggleGame.userID;
		String time = PersistentBoggle.getKeyValuewait(PersistentBoggleGame.getOPP_TIME_KEY(), "0");

		long user_world_time = PersistentBoggle.getPref(getApplicationContext(), PersistentBoggleGame.WORLD_TIME_KEY, (long)0);
		String opp_world_time = PersistentBoggle.getKeyValuewait(PersistentBoggle.OPP_WORLD_TIME_KEY, "0");

		String user_board = PersistentBoggle.getPref(getApplicationContext(), PersistentBoggle.BOARD_KEY, "");

		if(!opp_world_time.equals("0")){
			if(opponent_opp.equals(UserID)){
				if(user_world_time>Long.valueOf(opp_world_time))
					PersistentBoggle.setKeyValuewait(PersistentBoggle.OPP_BOARD_KEY,user_board );

				if (Long.valueOf(time)==0L){
					sendnotification(this,0);
				}
				else if(Long.valueOf(time)==120L){
					sendnotification(this,120);
				}
			}
		}
	}
	
	private void sendnotification(Context context,int time){
		
		NotificationManager notificationmanager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		Intent notificationIntent = new Intent(context,PersistentBoggle.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		
		String content ="";
		
		if(time==0){
			content = getString(R.string.Notification_Game_Ended);
		}
		else{
			content = getString(R.string.Notification_Game_Started);
		}
		
		Notification notification = new Notification(R.drawable.ic_launcher,
				content,
				System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		String contentTitle ="Persitent Boggle";
		
		notification.setLatestEventInfo(context, contentTitle, content, pIntent);
		notificationmanager.notify(0,notification);
	}
}