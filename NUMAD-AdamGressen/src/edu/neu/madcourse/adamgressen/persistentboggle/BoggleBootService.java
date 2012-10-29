package edu.neu.madcourse.adamgressen.persistentboggle;

import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BoggleBootService extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		AlarmManager alarmmanager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, new Intent(context,BoggleService.class),0);

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 1);
		
		alarmmanager.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(),PersistentBoggle.POLL_INTERVAL, pIntent);
	}
}
