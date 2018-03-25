package com.maniac.luis.series;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.maniac.luis.series.actividades.TabActivity;
import com.maniac.luis.series.utilidades.Common;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MiFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String mensaje="";
        if(remoteMessage.getData().size()>0){
            Map<String,String> data = remoteMessage.getData();
            String telefono=data.get(Common.TELEFONO);
            String comentario=data.get(Common.COMENTARIO);
            if(comentario.length()>=19){
                comentario=comentario.substring(0,20)+"..., ";
            }
            String telefono_usuario_final=data.get(Common.TELEFONO_USUARIO_FINAL);
            String serie=data.get(Common.SERIE);
            String contactoAgenda=loadContactFromTlf(telefono);
            if(contactoAgenda.equals("")){
                mensaje="Un usuario con tu número en su agenda le ha gustado tu comentario:" + comentario  + " en " + serie;
            }else{
               mensaje="A " + contactoAgenda + " " + "le ha gustado tu comentario:" + comentario + "en" + " " + serie;
            }
            Bitmap bitmap=null;
            try {
                 bitmap= Glide
                        .with(this)
                        .load(data.get(Common.AVATAR))
                        .asBitmap()
                        .into(100,100)
                        .get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(this, TabActivity.class);
            intent.putExtra(Common.NOTIFICACION,Common.NOTIFICACION);
            intent.putExtra(Common.NOMBRE_SERIE_COMENTARIOS,serie);
            intent.putExtra(Common.TELEFONO,telefono_usuario_final);
            PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
            Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher)
                    .setColor(Color.RED)
                    .setLargeIcon(bitmap)
                    .setContentTitle(Common.LIKE)
                    .setContentText(contactoAgenda)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje))
                    .setSound(sonido)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
            notificationManager.notify(m,builder.build());
            SharedPreferences sharedPref = getSharedPreferences(Common.NOTIFICACION,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(Common.NOTIFY,true);
            editor.commit();


        }



    }

    public String loadContactFromTlf(String telefono) {
        ContentResolver contentResolver=this.getContentResolver();
        String [] projeccion=new String[]{ContactsContract.Data.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER};
        String selectionClause=ContactsContract.Data.MIMETYPE + "='" +
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND " +
                ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL";
        Cursor cursor=this.getContentResolver().query(ContactsContract.Data.CONTENT_URI,projeccion,selectionClause,null,null);
        while(cursor.moveToNext()){
            String name=cursor.getString(0);
            String phoneNumber=cursor.getString(1);
            if(phoneNumber.length()>=9){
                phoneNumber=phoneNumber.replaceAll("\\s","");
                if(phoneNumber.substring(0,3).equals("+34")){
                    phoneNumber=phoneNumber.substring(3,phoneNumber.length());
                }

                if(phoneNumber.equals(telefono)){
                    return name;
                }
            }
        }
        cursor.close();
        return "";
    }
}
