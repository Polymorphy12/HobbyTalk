package com.example.sumin.myapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Sumin on 2017-10-21.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

    private static final String TAG = "FirebaseMsgService";

    String roomName;

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //메시지를 받았을 때, 노티피케이션을 띄운다.

        //추가한것
        sendNotification(remoteMessage.getData().get("message"));
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Activity를 실행시키는 pendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        //알람 울리는 것에 사용된다.
        String[] array;
        array = messageBody.split(":::");

        Log.d("내 메시지", messageBody);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(array[1])
                .setContentText(array[0])
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


    private void setRoomName(String room)
    {
        roomName = room;
    }

}