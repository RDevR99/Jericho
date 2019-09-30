package com.example.mad_assignment;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.io.PipedInputStream;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // We pass the intent to the notification activity class
        // In a future iteration the notification activity could be a map activity showing the wayto the lecture.
        Intent notificationIntent = new Intent(context, NotificationActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(NotificationActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        // Need to find a way for the request code to be flexible. May be store the requstCode somewhere inthe intent exttras and access it here.
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(intent.getIntExtra("requestCode", 0), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Notification notification = builder.setContentTitle("MAD Lecture in 10 Minutes")
                .setContentText("Dr. Scott is Waiting")
                .setTicker("")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_golf_course_black_24dp)
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);


    }
}
/*
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent alarmIntent = new Intent(context, HomeFragment.class);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_settings_black_24dp)
                .setContentTitle("Notification Title")
                .setContentText("Notification Text")
                .setAutoCancel(true);

        if(intent.getAction().equals("MY_NOTIFICATION_MESSAGE"))
        {
            notificationManager.notify(0, builder.build());
        }



    }
}*/
