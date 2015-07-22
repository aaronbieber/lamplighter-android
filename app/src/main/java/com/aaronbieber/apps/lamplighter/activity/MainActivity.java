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

public class MainActivity extends Activity {

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
                boolean isAlarmSet = NetworkCheckService.isHeartbeatAlarmSet(activity.getApplicationContext());

                if (isChecked) {
                    Log.d("Lamplighter", "Asked to start service!");
                    if (!isAlarmSet) {
                        Log.d("Lamplighter", "It's not set, so I'm doing it.");
                        NetworkCheckService.setHeartbeatAlarm(activity.getApplicationContext());
                    } else {
                        Log.d("Lamplighter", "It's already set, doing nothing.");
                    }
                } else {
                    Log.d("Lamplighter", "Asked to stop service!");
                    if (isAlarmSet) {
                        Log.d("Lamplighter", "It's set, so I'm killing it.");
                        NetworkCheckService.stopHeartbeatAlarm(activity.getApplicationContext());
                    } else {
                        Log.d("Lamplighter", "It's not set, so I'm doing nothing.");
                    }
                }

                updateOnOffButton();
            }
        });

        Button btnCheck = (Button) findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        boolean isAlarmSet = NetworkCheckService.isHeartbeatAlarmSet(this.getApplicationContext());

        Log.i("Lamplighter", "Heartbeat service alarm is " + (isAlarmSet ? "on." : "off."));

        ToggleButton t = (ToggleButton) findViewById(R.id.toggleOnOff);
        t.setChecked(isAlarmSet);
    }
}
