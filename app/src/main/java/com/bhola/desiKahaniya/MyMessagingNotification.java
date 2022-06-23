package com.bhola.desiKahaniya;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyMessagingNotification extends FirebaseMessagingService {
    private static int NOTIFY_ID = 1001;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());

        if (remoteMessage.getData() != null) {
            Log.d("TAGA", "onMessageReceived: "+remoteMessage.getData().get("KEY"));

        }
    }

    public void showNotification(String title, String message) {

        Intent notificationIntent = new Intent(getApplicationContext(), Notification_Story_Detail.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "myFirebaseChannel")
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.app_icon_round)
                .setAutoCancel(true)
                .setContentText(message)
                .setContentIntent(pendingIntent);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("myFirebaseChannel", "myFirebaseChannel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(101, builder.build());

    }
}
