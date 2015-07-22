package com.aaronbieber.apps.lamplighter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.aaronbieber.apps.lamplighter.service.NetworkCheckService;

public class StartNetworkCheckServiceAtBootReceiver extends BroadcastReceiver {
    public StartNetworkCheckServiceAtBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        context.startService(new Intent(context, NetworkCheckService.class));
    }
}
