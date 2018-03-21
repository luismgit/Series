package com.example.luis.series;

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
import com.example.luis.series.Objetos.Series;
import com.example.luis.series.actividades.AutentificacionActivity;
import com.example.luis.series.actividades.TabActivity;
import com.example.luis.series.utilidades.Common;
import com.example.luis.series.utilidades.ComunicarContactosPhoneNumber;
import com.example.luis.series.utilidades.ListaNumerosAgendaTelefonos;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MiFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i("prueba","hola -> " );
        if(remoteMessage.getData().size()>0){
            Map<String,String> data = remoteMessage.getData();
            String telefono=data.get("telefono");
            String serie=data.get("serie");
            String contactoAgenda=loadContactFromTlf(telefono);
            Log.i("prueba","contactoAgenda -> " + contactoAgenda);
            Bitmap bitmap=null;
            try {
                 bitmap= Glide
                        .with(this)
                        .load(data.get("avatar"))
                        .asBitmap()
                        .into(100,100)
                        .get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(this, AutentificacionActivity.class);
            intent.putExtra(Common.NOTIFICACION,Common.NOTIFICACION);
            intent.putExtra(Common.NOMBRE_SERIE_COMENTARIOS,serie);
            PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
            Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.avatar3)
                    .setColor(Color.CYAN)
                    .setLargeIcon(bitmap)
                    .setContentTitle("Series")
                    .setContentText(contactoAgenda + " le ha dado a 'me gusta' a un comentario")
                    .setAutoCancel(true)
                    .setSound(sonido)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0,builder.build());
            SharedPreferences sharedPref = getSharedPreferences(Common.NOTIFICACION,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("notify",true);
            editor.commit();

            SharedPreferences sharedPreferences = getSharedPreferences("serieNotificacion",Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("serie",serie);
            edit.commit();
        }



    }

    public String loadContactFromTlf(String telefono) {
        ContentResolver contentResolver=this.getContentResolver();
        String [] projeccion=new String[]{ContactsContract.Data.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER};
        //String [] projeccion=new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.Contacts.DISPLAY_NAME};
        String selectionClause=ContactsContract.Data.MIMETYPE + "='" +
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND " +
                ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL";
        //String sortOrder = ContactsContract.Data.DISPLAY_NAME + " ASC";
        Cursor cursor=this.getContentResolver().query(ContactsContract.Data.CONTENT_URI,projeccion,selectionClause,null,null);
        while(cursor.moveToNext()){
            String name=cursor.getString(0);
            String phoneNumber=cursor.getString(1);
            if(phoneNumber.length()>=9){
                phoneNumber=phoneNumber.replaceAll("\\s","");
                if(phoneNumber.substring(0,3).equals("+34")){
                    phoneNumber=phoneNumber.substring(3,phoneNumber.length());
                    //Log.i("contactosTlf","numSin+34 -> " + phoneNumber);

                }
                //nombreContactosTelefono.add(name);
                // contactosTelefono.add(phoneNumber);
                Log.i("contactos","Nombre: " + name);
                Log.i("contactos","Numero: " + phoneNumber);
                if(phoneNumber.equals(telefono)){
                    return name;
                }
            }

            //Log.i("contactos","Identificador: " + cursor.getString(0));


            //Log.i("contactos","Tipo: " + cursor.getString(3));
        }
        cursor.close();
        return "";
    }
}
