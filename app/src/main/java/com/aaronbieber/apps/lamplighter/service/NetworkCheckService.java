package com.aaronbieber.apps.lamplighter.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkCheckService extends Service {
    private static final String DEBUG_TAG = "NetworkCheckService";

    // Code used to create unique intents.
    public static final int SERVICE_CODE = 1234;

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
                new GetHeartbeatClass().execute("http://lights.skynet.net/heartbeat/android");
            } catch (Exception e) {
                Log.d(DEBUG_TAG, "Caught exception: " + e.getClass());
                // I don't know what to do, so do nothing.
            }
        }

        //NetworkCheckService.setHeartbeatAlarm(this);

        // Don't you dare keep running.
        stopSelf();
        return START_STICKY;
    }

    public static boolean onHomeNetwork(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        String ssid = wifiInfo.getSSID().replaceAll("\"", "");
        Log.d(DEBUG_TAG, "SSID is " + ssid);

        return ssid.startsWith("TwilightZone");
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    private String readIt(InputStream stream, int len) throws IOException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer).split("\n")[0];
    }

    private class GetHeartbeatClass extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d(DEBUG_TAG, result);
        }
    }

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
        PendingIntent pendingIntent = NetworkCheckService.getHeartbeatPendingIntent(context);

        // Make sure that it's canceled.
        //NetworkCheckService.stopHeartbeatAlarm(context);

        // Repeat every SECONDS seconds.
        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,    // Wake up for the event (might be bad)
                System.currentTimeMillis(), // Start now
                1000,                       // Every 5 seconds or so
                pendingIntent               // Do this
        );

        return true;
    }

    public static boolean isHeartbeatAlarmSet(Context context) {
        Intent alarmIntent = new Intent(context, NetworkCheckService.class);

        //return (PendingIntent.getService(context, 0, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null);
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
        PendingIntent pendingIntent = NetworkCheckService.getHeartbeatPendingIntent(context);

        // Make sure that shit isn't already scheduled.
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

        return true;
    }
}
