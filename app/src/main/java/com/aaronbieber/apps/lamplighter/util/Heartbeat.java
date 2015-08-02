package com.aaronbieber.apps.lamplighter.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.aaronbieber.apps.lamplighter.service.NetworkCheckService;

public class Heartbeat {
    private static final String DEBUG_TAG = "Heartbeat";

    private static PendingIntent getHeartbeatPendingIntent(Context context) {
        Intent alarmIntent = new Intent(context, NetworkCheckService.class);
        PendingIntent pendingIntent = PendingIntent.getService(
                context,
                NetworkCheckService.SERVICE_CODE,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        return pendingIntent;
    }

    public static boolean setHeartbeatAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = getHeartbeatPendingIntent(context);

        // Repeat every SECONDS seconds.
        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,    // Wake up for the event (might be bad)
                System.currentTimeMillis(), // Start now
                15 * 60 * 1000,             // Every 15 minutes or so
                pendingIntent               // Do this
        );

        return true;
    }

    public static boolean isHeartbeatAlarmSet(Context context) {
        Intent alarmIntent = new Intent(context, NetworkCheckService.class);

        return (PendingIntent.getService(
                context,
                NetworkCheckService.SERVICE_CODE,
                alarmIntent,
                PendingIntent.FLAG_NO_CREATE
        ) != null);
    }

    public static boolean stopHeartbeatAlarm(Context context) {
        Log.d(DEBUG_TAG, "Attempting to stop service...");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = getHeartbeatPendingIntent(context);

        // Make sure that shit isn't already scheduled.
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

        return true;
    }

    public static void sendHeartbeat() {
        new GetHeartbeatAsync().execute("http://lamplighter.skynet.net/heartbeat/set");
    }
}
