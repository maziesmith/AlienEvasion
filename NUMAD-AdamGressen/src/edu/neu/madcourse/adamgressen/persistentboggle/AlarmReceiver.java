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
		
		context.startService(new Intent(context,BoggleService.class));
		
	}

}
