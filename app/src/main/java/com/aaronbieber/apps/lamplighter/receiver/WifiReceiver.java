package com.aaronbieber.apps.lamplighter.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.aaronbieber.apps.lamplighter.R;
import com.aaronbieber.apps.lamplighter.activity.MainActivity;
import com.aaronbieber.apps.lamplighter.service.NetworkCheckService;

/**
 * Created by airborne on 7/21/15.
 */
public class WifiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            Log.d("WifiReceiver", "WiFi connection is now active.");
            if (NetworkCheckService.onHomeNetwork(context)) {
                Log.d("WifiReceiver", "We are apparently home!");

                if(!NetworkCheckService.isHeartbeatAlarmSet(context)) {
                    Log.d("WifiReceiver", "Heartbeat is not active, starting.");
                    NetworkCheckService.setHeartbeatAlarm(context);
                    sendNotification(context, 002, "Heartbeat Enabled", "Lamplighter heartbeat has been enabled.");
                } else {
                    Log.d("WifiReceiver", "Heartbeat is already active, resting.");
                }
            }
        } else {
            Log.d("WifiReceiver", "WiFi connection lost.");
            if (NetworkCheckService.isHeartbeatAlarmSet(context)) {
                Log.d("WifiReceiver", "Heartbeat is active, deactivate it.");
                NetworkCheckService.stopHeartbeatAlarm(context);
                sendNotification(context, 001, "Heartbeat Disabled", "Lamplighter heartbeat has been disabled.");
            } else {
                Log.d("WifiReceiver", "Heartbeat is not active, resting.");
            }
        }
    }

    private void sendNotification(Context context, int id, String title, String message) {
        // Build intent for notification content
        Intent viewIntent = new Intent(context, MainActivity.class);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(context, 0, viewIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setContentIntent(viewPendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(id, notificationBuilder.build());
    }
}
