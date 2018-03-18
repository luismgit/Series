package com.example.luis.series;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.luis.series.Objetos.Series;
import com.example.luis.series.actividades.TabActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MiFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification()!=null){
            Log.d("Notificacion", "Mensaje: " + remoteMessage.getNotification().getBody() + ", titulo " + remoteMessage.getNotification().getTitle());
            Log.d("Notificacion","De: " + remoteMessage.getFrom());
            Intent intent = new Intent(this, Series.class);
            PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
            Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.avatar3)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setAutoCancel(true)
                    .setSound(sonido)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0,builder.build());
        }

    }
}
