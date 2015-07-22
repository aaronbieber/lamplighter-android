package com.aaronbieber.apps.lamplighter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.aaronbieber.apps.lamplighter.R;
import com.aaronbieber.apps.lamplighter.service.NetworkCheckService;
import com.aaronbieber.apps.lamplighter.util.Heartbeat;

public class MainActivity extends Activity {
    private static final String DEBUG_TAG = "Lamplighter";
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;

        updateOnOffButton();

        // Events
        ToggleButton switchServiceToggle = (ToggleButton) findViewById(R.id.toggleOnOff);
        switchServiceToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean isAlarmSet = Heartbeat.isHeartbeatAlarmSet(activity.getApplicationContext());

                if (isChecked) {
                    Log.d(DEBUG_TAG, "Asked to start service!");
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
    }

    private void updateOnOffButton() {
        // Update the textStatus label based on NetworkCheckService.isHeartbeatAlarmSet()
        boolean isAlarmSet = Heartbeat.isHeartbeatAlarmSet(this.getApplicationContext());

        Log.i("Lamplighter", "Heartbeat service alarm is " + (isAlarmSet ? "on." : "off."));

        ToggleButton t = (ToggleButton) findViewById(R.id.toggleOnOff);
        t.setChecked(isAlarmSet);
    }
}
