package edu.neu.madcourse.adamgressen.persistentboggle;

import edu.neu.madcourse.adamgressen.R;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

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
		String UserID = PersistentBoggle.getKeyValuewait(PersistentBoggleGame.getUserID(), "");
		String time = PersistentBoggle.getKeyValuewait(PersistentBoggleGame.getOPP_TIME_KEY(), "0");

		if(opponent_opp.equals(UserID)){
			if (Integer.parseInt(time)==0){
				sendnotification(this,0);
			}
			else if(Integer.parseInt(time)==120)
				sendnotification(this,120);
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
