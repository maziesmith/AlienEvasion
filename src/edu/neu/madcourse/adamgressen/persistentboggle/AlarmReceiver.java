package edu.neu.madcourse.adamgressen.persistentboggle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("AlarmReceiver", "onReceive Started");
		
		context.startService(new Intent(context,BoggleService.class));
	}
}
