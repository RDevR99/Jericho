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

        // On clicking the notification the user will be lead to the main activity, in the Home Fragment through it.
        Intent notificationIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(NotificationActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        // Need to find a way for the request code to be flexible. May be store the requstCode somewhere inthe intent exttras and access it here.
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(intent.getIntExtra("requestCode", 0), PendingIntent.FLAG_UPDATE_CURRENT);

        String lectureName = intent.getStringExtra("Lecture Name");
        String NotificationText = intent.getStringExtra("Notification Text");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        long[] vibrationPattern = {500,500};
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = builder.setContentTitle(lectureName)
                .setContentText(NotificationText)
                .setLights(Color.RED, 500, 50)
                .setVibrate(vibrationPattern)
                .setSound(alarmSound)
                .setTicker("")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_golf_course_black_24dp)
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

    }
}
