package com.aaronbieber.apps.lamplighter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.aaronbieber.apps.lamplighter.R;
import com.aaronbieber.apps.lamplighter.service.NetworkCheckService;
import com.aaronbieber.apps.lamplighter.util.Heartbeat;
import com.aaronbieber.apps.lamplighter.util.Notify;

public class MainActivity extends Activity {
    private static final String DEBUG_TAG = "Lamplighter";
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;

        // If the service should be started, start it.
        if (NetworkCheckService.onHomeNetwork(this.getApplicationContext())) {
            Log.d(DEBUG_TAG, "Heartbeat should be running... Start it.");
            Heartbeat.setHeartbeatAlarm(this.getApplicationContext());
        } else {
            Log.d(DEBUG_TAG, "Heartbeat should not be running... Stop it.");
            Heartbeat.stopHeartbeatAlarm(this.getApplicationContext());
        }

        updateOnOffButton();

        // Events
        ToggleButton switchServiceToggle = (ToggleButton) findViewById(R.id.toggleOnOff);
        switchServiceToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean isAlarmSet = Heartbeat.isHeartbeatAlarmSet(activity.getApplicationContext());

                if (isChecked) {
                    Log.d(DEBUG_TAG, "Asked to start service!");

                    if (!NetworkCheckService.onHomeNetwork(activity.getApplicationContext())) {
                        // Can't start the heartbeat service while away!
                        CharSequence text = "Can't start, no Wi-Fi!";
                        Toast.makeText(
                                activity.getApplicationContext(),
                                text,
                                Toast.LENGTH_SHORT
                        ).show();

                        // Un-check.
                        buttonView.setChecked(false);
                        return;
                    }

                    if (!isAlarmSet) {
                        Log.d(DEBUG_TAG, "It's not set, so I'm doing it.");
                        Heartbeat.setHeartbeatAlarm(activity.getApplicationContext());
                    } else {
                        Log.d(DEBUG_TAG, "It's already set, doing nothing.");
                    }
                } else {
                    Log.d(DEBUG_TAG, "Asked to stop service!");
                    if (isAlarmSet) {
                        Log.d(DEBUG_TAG, "It's set, so I'm killing it.");
                        Heartbeat.stopHeartbeatAlarm(activity.getApplicationContext());
                    } else {
                        Log.d(DEBUG_TAG, "It's not set, so I'm doing nothing.");
                    }
                }

                updateOnOffButton();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateOnOffButton();
        Notify.cancelNotification(this.getApplicationContext(), Notify.HEARTBEAT_NOTIFICATION_ID);

        Log.d(DEBUG_TAG, "Application has resumed. Notification canceled.");
    }

    private void updateOnOffButton() {
        // Update the textStatus label based on NetworkCheckService.isHeartbeatAlarmSet()
        boolean isAlarmSet = Heartbeat.isHeartbeatAlarmSet(this.getApplicationContext());

        Log.d(DEBUG_TAG, "Heartbeat service is " + (isAlarmSet ? "on." : "off."));

        ToggleButton t = (ToggleButton) findViewById(R.id.toggleOnOff);
        t.setChecked(isAlarmSet);
    }
}
