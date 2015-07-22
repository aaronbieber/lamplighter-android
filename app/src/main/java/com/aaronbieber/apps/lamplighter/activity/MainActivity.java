package com.aaronbieber.apps.lamplighter.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.aaronbieber.apps.lamplighter.R;
import com.aaronbieber.apps.lamplighter.service.NetworkCheckService;

public class MainActivity extends Activity {

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;

        updateNetworkStatusLabel();

        // Events
        Switch switchServiceToggle = (Switch) findViewById(R.id.switchServiceToggle);
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

                updateNetworkStatusLabel();
            }
        });

        Button btnCheck = (Button) findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update the textStatus label based on NetworkCheckService.isHeartbeatAlarmSet()
                boolean isAlarmSet = NetworkCheckService.isHeartbeatAlarmSet(activity.getApplicationContext());
                Log.i("Lamplighter", "Got value for isAlarmSet " + (isAlarmSet ? "Yes" : "No"));
                TextView t = (TextView) findViewById(R.id.textStatus);
                if (isAlarmSet) {
                    t.setText("Running");
                } else {
                    t.setText("Stopped");
                }

                updateNetworkStatusLabel();
            }
        });
    }

    private void updateNetworkStatusLabel() {
        // Update the textStatus label based on NetworkCheckService.isHeartbeatAlarmSet()
        boolean isAlarmSet = NetworkCheckService.isHeartbeatAlarmSet(this.getApplicationContext());
        TextView t = (TextView) findViewById(R.id.textStatus);

        if (isAlarmSet) {
            t.setText("Running");
        } else {
            t.setText("Stopped");
        }
    }
}
