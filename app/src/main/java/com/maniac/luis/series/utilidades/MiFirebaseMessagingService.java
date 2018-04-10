package com.maniac.luis.series.utilidades;

import android.app.Notification;
import android.app.NotificationChannel;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maniac.luis.series.Objetos.Suscripcion;
import com.maniac.luis.series.R;
import com.maniac.luis.series.actividades.TabActivity;
import com.maniac.luis.series.references.FirebaseReferences;
import com.maniac.luis.series.utilidades.Common;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import me.leolin.shortcutbadger.ShortcutBadger;


public class MiFirebaseMessagingService extends FirebaseMessagingService {

    NotificationManager notificationManager;
    String telefono_usuario_final;
    String serie;
    int notificationId;
    NotificationCompat.Builder builder;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        String mensaje="";
        if(remoteMessage.getData().size()>0){
            Map<String,String> data = remoteMessage.getData();
            String telefono=data.get(Common.TELEFONO);
            String comentario=data.get(Common.COMENTARIO);
            if(comentario.length()>=20){
                comentario=comentario.substring(0,20)+"..., ";
            }
            telefono_usuario_final=data.get(Common.TELEFONO_USUARIO_FINAL);
            serie=data.get(Common.SERIE);
            String contactoAgenda=loadContactFromTlf(telefono);


            if(contactoAgenda.equals("")){
                mensaje="Un usuario con tu número en su agenda le ha gustado tu comentario:" + comentario  + " " + "en" + " " + serie + ".";
            }else{
               mensaje="A " + contactoAgenda + " " + "le ha gustado tu comentario:" + comentario + " " + "en" + " " + serie + ".";
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

            notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);;
            String channelId = "channel-01";
            String channelName = "Channel Name";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

             builder=new NotificationCompat.Builder(this,channelId)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setColor(Color.RED)
                    .setLargeIcon(bitmap)
                    .setContentTitle(Common.LIKE + " | " + contactoAgenda + " | " + serie)
                    .setContentText(contactoAgenda)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje))
                    .setSound(sonido)
                    .setContentIntent(pendingIntent);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.SUSCRIPCIONES);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot:
                            dataSnapshot.getChildren()){
                        Suscripcion suscripcion = snapshot.getValue(Suscripcion.class);
                        if(suscripcion.getTelefono().equals(telefono_usuario_final) &&
                                suscripcion.getSerie().equalsIgnoreCase(serie)){
                            mandarNotificacion();

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });







        }



    }

    private void mandarNotificacion() {
        notificationManager.notify(notificationId,builder.build());
        SharedPreferences sharedPref = getSharedPreferences(Common.NOTIFICACION,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(Common.NOTIFY,true);
        editor.commit();
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
