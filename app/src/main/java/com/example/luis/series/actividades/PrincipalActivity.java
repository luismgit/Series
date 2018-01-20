package com.example.luis.series.actividades;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.luis.series.Adapters.Adapter;
import com.example.luis.series.R;
import com.example.luis.series.utilidades.ComunicarCurrentUser;
import com.example.luis.series.references.FirebaseReferences;
import com.example.luis.series.Objetos.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    RecyclerView rv;
    List<Usuario> usuarios;
    Adapter adapter;
    List<String> contactosTelefono;
    String phoneNumberUser;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal_activity);

        rv=findViewById(R.id.recycler);

        rv.setLayoutManager(new LinearLayoutManager(this));
        //Intent intent = new Intent(this,TabActivity.class);
        //startActivity(intent);
        contactosTelefono=new ArrayList<>();
        usuarios=new ArrayList<>();
        SharedPreferences sharedPref = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("registroCerrado",false);
        editor.commit();
        Log.i("REGISTRO","Como hemos llegado a la principal ponemos registroCerrado a false");

        user= ComunicarCurrentUser.getUser();
        phoneNumberUser = user.getPhoneNumber();
        phoneNumberUser.replaceAll("\\s","");
        if(phoneNumberUser.substring(0,3).equals("+34")){
            phoneNumberUser=phoneNumberUser.substring(3,phoneNumberUser.length());
        }
        loadContactFromTlf();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        adapter=new Adapter(usuarios);
        rv.setAdapter(adapter);
        database.getReference(FirebaseReferences.USUARIOS_REFERENCE).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuarios.removeAll(usuarios);
                for (DataSnapshot snapshot:
                       dataSnapshot.getChildren() ){
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    String phoneNumber = usuario.getTelefono();
                    if(contactosTelefono.contains(phoneNumber)){
                        if(!phoneNumber.equals(phoneNumberUser)){
                            //Log.i("CONTACTOSS","user -> " + "phoneNumberUser" + phoneNumberUser);
                            //Log.i("CONTACTOSS","finales -> " + phoneNumber);
                            usuarios.add(usuario);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private  void loadContactFromTlf(){
        ContentResolver contentResolver=getContentResolver();
       // String [] projeccion=new String[]{ContactsContract.Data._ID,ContactsContract.Data.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone.TYPE};
        String [] projeccion=new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        String selectionClause=ContactsContract.Data.MIMETYPE + "='" +
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND " +
                                ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL";
        //String sortOrder = ContactsContract.Data.DISPLAY_NAME + " ASC";
        Cursor cursor=getContentResolver().query(ContactsContract.Data.CONTENT_URI,projeccion,selectionClause,null,null);
        while(cursor.moveToNext()){
            String phoneNumber=cursor.getString(0);
            phoneNumber=phoneNumber.replaceAll("\\s","");
            if(phoneNumber.substring(0,3).equals("+34")){
                phoneNumber=phoneNumber.substring(3,phoneNumber.length());
                //Log.i("contactosTlf","numSin+34 -> " + phoneNumber);
            }
            //Log.i("contactos","Identificador: " + cursor.getString(0));
            //Log.i("contactos","Nombre: " + cursor.getString(1));
            //Log.i("contactos","Numero: " + phoneNumber);
            contactosTelefono.add(phoneNumber);
            //Log.i("contactos","Tipo: " + cursor.getString(3));
        }
        cursor.close();
    }


}
