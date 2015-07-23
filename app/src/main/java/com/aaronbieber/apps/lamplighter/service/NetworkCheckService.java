package com.aaronbieber.apps.lamplighter.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import com.aaronbieber.apps.lamplighter.util.Heartbeat;

public class NetworkCheckService extends Service {
    private static final String DEBUG_TAG = "NetworkCheckService";

    // Code used to create unique intents.
    public static final int SERVICE_CODE = 928574635;

    public NetworkCheckService() {
        // Noop.
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Do the actual work.
        Log.d(DEBUG_TAG, "The NetworkCheckService HAS AWOKEN!!");

        if (onHomeNetwork(this)) {
            // Do the real work.
            Log.d(DEBUG_TAG, "Holy shit I'm home");
            try {
                Heartbeat.sendHeartbeat();
            } catch (Exception e) {
                Log.d(DEBUG_TAG, "Caught exception: " + e.getClass());
                // I don't know what to do, so do nothing.
            }
        }

        // We're done, end.
        stopSelf();

        // If our process is killed, let the service stop until the next intent is delivered.
        return START_NOT_STICKY;
    }

    public static boolean onHomeNetwork(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            String ssid = wifiInfo.getSSID().replaceAll("\"", "");
            Log.d(DEBUG_TAG, "SSID is " + ssid);

            return ssid.startsWith("TwilightZone");
        } else {
            Log.d(DEBUG_TAG, "Not on wi-fi, so fuck off.");
        }

        return false;
    }
}