package com.example.luis.series.actividades;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.luis.series.R;
import com.example.luis.series.utilidades.LoadPhoneNumbersFromContacts;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {
    List<String> contactosTelefono;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        contactosTelefono=new ArrayList<>();
        loadContactFromTlf();
    }

    private void loadContactFromTlf() {
        ContentResolver contentResolver=this.getContentResolver();
        // String [] projeccion=new String[]{ContactsContract.Data._ID,ContactsContract.Data.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone.TYPE};
        String [] projeccion=new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        String selectionClause=ContactsContract.Data.MIMETYPE + "='" +
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND " +
                ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL";
        //String sortOrder = ContactsContract.Data.DISPLAY_NAME + " ASC";
        Cursor cursor=this.getContentResolver().query(ContactsContract.Data.CONTENT_URI,projeccion,selectionClause,null,null);
        while(cursor.moveToNext()){
            String phoneNumber=cursor.getString(0);

            if(phoneNumber.length()>=9){
                phoneNumber=phoneNumber.replaceAll("\\s","");
                if(phoneNumber.substring(0,3).equals("+34")){
                    phoneNumber=phoneNumber.substring(3,phoneNumber.length());
                    //Log.i("contactosTlf","numSin+34 -> " + phoneNumber);

                }
                contactosTelefono.add(phoneNumber);
            }

            //Log.i("contactos","Identificador: " + cursor.getString(0));
            //Log.i("contactos","Nombre: " + cursor.getString(1));
            //Log.i("contactos","Numero: " + phoneNumber);

            //Log.i("contactos","Tipo: " + cursor.getString(3));
        }
        cursor.close();
        LoadPhoneNumbersFromContacts.setListadoTelefonos(contactosTelefono);
        Intent intent = new Intent(this, TabActivity.class);
        startActivity(intent);
        Log.i("SPLASH","Termina de cagar los telefonos");
        finish();
        //aqui iremos a tabactivity y tabactivity recuperara la agenda filtrada a traves de LoadPhoneNumbersFromContacts.getListadoTelefonos()
    }
}
