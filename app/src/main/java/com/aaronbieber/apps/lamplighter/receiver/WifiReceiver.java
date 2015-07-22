package com.aaronbieber.apps.lamplighter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.aaronbieber.apps.lamplighter.service.NetworkCheckService;
import com.aaronbieber.apps.lamplighter.util.Heartbeat;
import com.aaronbieber.apps.lamplighter.util.Notify;

public class WifiReceiver extends BroadcastReceiver {
    private static final String DEBUG_TAG = "WifiReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            Log.d(DEBUG_TAG, "WiFi connection is now active.");
            if (NetworkCheckService.onHomeNetwork(context)) {
                Log.d(DEBUG_TAG, "We are apparently home!");

                // Send a heartbeat immediately!
                Heartbeat.sendHeartbeat();

                if(!Heartbeat.isHeartbeatAlarmSet(context)) {
                    Log.d(DEBUG_TAG, "Heartbeat is not active, starting.");
                    Heartbeat.setHeartbeatAlarm(context);
                    Notify.sendNotification(context, Notify.HEARTBEAT_NOTIFICATION_ID, "Heartbeat Enabled", "Lamplighter heartbeat has been enabled.");
                } else {
                    Log.d(DEBUG_TAG, "Heartbeat is already active, resting.");
                }
            }
        } else {
            Log.d(DEBUG_TAG, "WiFi connection lost.");
            if (Heartbeat.isHeartbeatAlarmSet(context)) {
                Log.d(DEBUG_TAG, "Heartbeat is active, deactivate it.");
                Heartbeat.stopHeartbeatAlarm(context);
                Notify.sendNotification(context, Notify.HEARTBEAT_NOTIFICATION_ID, "Heartbeat Disabled", "Lamplighter heartbeat has been disabled.");
            } else {
                Log.d(DEBUG_TAG, "Heartbeat is not active, resting.");
            }
        }
    }
}
