package com.aaronbieber.apps.lamplighter.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import com.aaronbieber.apps.lamplighter.R;
import com.aaronbieber.apps.lamplighter.activity.MainActivity;

public class Notify {
    // Arbitrary number.
    public static final int HEARTBEAT_NOTIFICATION_ID = 191817;

    public static void sendNotification(Context context, int id, String title, String message) {
        // Build intent for notification content
        Intent viewIntent = new Intent(context, MainActivity.class);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(context, 0, viewIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                        .setSmallIcon(R.drawable.ic_stat_lamplighter_notification)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setContentIntent(viewPendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(id, notificationBuilder.build());
    }

    public static void cancelNotification(Context context, int id) {
        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        // Build the notification and issues it with notification manager.
        notificationManager.cancel(id);
    }
}
