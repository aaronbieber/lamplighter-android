package com.aaronbieber.apps.lamplighter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

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
                Log.d("WifiReceiver", "We are apparently home! Start heartbeat.");
                NetworkCheckService.setHeartbeatAlarm(context);
            }
        } else {
            Log.d("WifiReceiver", "WiFi connection lost. Disable heartbeat.");
            NetworkCheckService.stopHeartbeatAlarm(context);
        }
    }
}
