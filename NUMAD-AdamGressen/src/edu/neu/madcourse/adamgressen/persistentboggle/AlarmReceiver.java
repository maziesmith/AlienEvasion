package edu.neu.madcourse.adamgressen.persistentboggle;

import edu.neu.madcourse.adamgressen.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d("AlarmReceiver", "onReceive Started");
		
		Toast.makeText(context, "AlarmReciever Started", Toast.LENGTH_SHORT).show();
		context.startService(new Intent(context,BoggleService.class));
		
		NotificationManager notificationmanager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Intent notificationIntent = new Intent(context,PersistentBoggle.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		
		
		Notification notification = new Notification(R.drawable.ic_launcher,
				"GAME HAS ENDED",
				System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		String contentTitle ="Persitent Boggle";
		CharSequence contentText = "Opponent Game Ended";
		
		notification.setLatestEventInfo(context, contentTitle, contentText, pIntent);
		notificationmanager.notify(0,notification);
		
		

	}

}
