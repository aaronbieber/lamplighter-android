package com.aaronbieber.apps.lamplighter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.app.AlarmManager;
import java.util.Calendar;
import android.util.Log;

public class HeartbeatReceiver extends BroadcastReceiver {
    private static final String DEBUG_TAG = "HeartbeatReceiver";

    public HeartbeatReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(DEBUG_TAG, "Received heartbeat message.");
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");

        //Intent networkCheck = new Intent(context, NetworkCheckService.class);
        //context.startService(networkCheck);

        Intent alarmIntent = new Intent(context, HeartbeatReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 10000, pendingIntent);
    }
}
