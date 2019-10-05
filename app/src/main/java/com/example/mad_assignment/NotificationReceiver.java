package com.example.mad_assignment;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
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

        long[] vibrationPattern = {500,500};
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = builder.setContentTitle("MAD Lecture in 10 Minutes")
                .setContentText("Dr. Scott is Waiting")
                .setLights(Color.RED, 500, 50)
                .setVibrate(vibrationPattern)
                .setSound(alarmSound)
                .setTicker("")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_golf_course_black_24dp)
                .setContentIntent(pendingIntent).build();


       // builder.setDefaults(Notification.DEFAULT_VIBRATE);
       // builder.setDefaults(Notification.DEFAULT_SOUND);
       // builder.setDefaults(Notification.DEFAULT_LIGHTS);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);


    }
}
