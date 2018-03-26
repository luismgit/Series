package com.maniac.luis.series.actividades;

import android.content.ClipData;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.maniac.luis.series.Adapters.AdaptadorComentarios;
import com.maniac.luis.series.Objetos.Comentario;
import com.maniac.luis.series.R;
import com.maniac.luis.series.references.FirebaseReferences;
import com.maniac.luis.series.utilidades.Common;
import com.maniac.luis.series.utilidades.ComunicarAvatarUsuario;
import com.maniac.luis.series.utilidades.ComunicarContactosPhoneNumber;
import com.maniac.luis.series.utilidades.ComunicarCurrentUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maniac.luis.series.utilidades.ListaNumerosAgendaTelefonos;
import com.sun.mail.imap.protocol.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComentariosActivity extends AppCompatActivity {

    RecyclerView rv;
    List<Comentario> comentarios;
    List<String> contactosPhoneNumber;
    AdaptadorComentarios adaptadorComentarios;
    EditText nuevoComentario;
    String nombreSerie;
    List<String> contactos;
    TextView txtSinComentarios;
    ImageView imagenSerieComentarios;
    TextView textoSerieComentarios;
    Map<String,String> agenda;
    DatabaseReference referenceABorrar;
    ValueEventListener listener;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);
        FirebaseDatabase.getInstance().goOnline();
        contactos=new ArrayList<>();
        agenda=new HashMap<>();
        loadContactFromTlf();
        contactos=ComunicarContactosPhoneNumber.getPhoneNumbers();
        txtSinComentarios=findViewById(R.id.mensajeSinComentarios);
        textoSerieComentarios=findViewById(R.id.textoSerieComentarios);
        txtSinComentarios.setVisibility(View.GONE);
        nombreSerie=getIntent().getStringExtra(Common.NOMBRE_SERIE_COMENTARIOS);
        imagenSerieComentarios=findViewById(R.id.imagenSerieComentarios);
        referenceABorrar = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS_LEIDOS_SERIE)
                .child(ComunicarCurrentUser.getPhoneNumberUser()).child(nombreSerie).child(FirebaseReferences.COM_LEIDOS);
         listener=referenceABorrar.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                referenceABorrar.setValue(0);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference r = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.SERIES_REFERENCE).child(nombreSerie);
        r.child(FirebaseReferences.IMAGEN_SERIE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url= (String) dataSnapshot.getValue();
                Glide.with(ComentariosActivity.this)
                        .load(url)
                        .centerCrop()
                        .fitCenter()
                        .into(imagenSerieComentarios);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        r.child(FirebaseReferences.NOMBRE_SERIE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nombreSerie= (String) dataSnapshot.getValue();
                textoSerieComentarios.setText(nombreSerie);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        nuevoComentario=findViewById(R.id.nuevoComentario);
        rv=findViewById(R.id.recyclerComentarios);
        comentarios=new ArrayList<>();
        contactosPhoneNumber=new ArrayList<>();
        contactosPhoneNumber= ComunicarContactosPhoneNumber.getPhoneNumbers();
        DatabaseReference fbref=FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS_LEIDOS_SERIE).child(ComunicarCurrentUser.getPhoneNumberUser())
                .child(nombreSerie).child(FirebaseReferences.COM_LEIDOS);
        fbref.setValue(0);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adaptadorComentarios=new AdaptadorComentarios(comentarios,this,agenda);
        rv.setAdapter(adaptadorComentarios);
        /*Drawable dividerDrawable = getResources().getDrawable(R.drawable.dividerdrawable);
        com.maniac.luis.series.DividerItemDecoration dividerItemDecoration = new com.maniac.luis.series.DividerItemDecoration(dividerDrawable);
        rv.addItemDecoration(dividerItemDecoration);*/
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comentarios.removeAll(comentarios);
                for(DataSnapshot snapshot:
                        dataSnapshot.getChildren()){
                    Comentario com = snapshot.getValue(Comentario.class);
                    if(contactosPhoneNumber.contains(com.getPhoneNumberUsuario()) && com.getSerie().equals(nombreSerie)
                            && com.getLiked().containsKey(ComunicarCurrentUser.getPhoneNumberUser())){
                        comentarios.add(com);
                        com.setTipo(Comentario.ComentarioType.OTHER_USERS);
                        }


                    if(com.getPhoneNumberUsuario().equals(ComunicarCurrentUser.getPhoneNumberUser()) && com.getSerie().equals(nombreSerie)){
                        com.setTipo(Comentario.ComentarioType.USER_PROP);
                        comentarios.add(com);
                    }


                }
                if(comentarios.size()==0){
                    txtSinComentarios.setVisibility(View.VISIBLE);
                }else{
                    txtSinComentarios.setVisibility(View.GONE);
                }

                adaptadorComentarios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        referenceABorrar.removeEventListener(listener);
    }

    public void enviarNuevoComentario(View view) {
        String coment=nuevoComentario.getText().toString().trim();
        if(coment.equals("")){
            Toast.makeText(this, R.string.debe_comentar,Toast.LENGTH_SHORT).show();
        }else{
            Map<String,Boolean> liked = new HashMap<>();
            List<String> numeroContactos=new ArrayList<>();
            numeroContactos=ComunicarContactosPhoneNumber.getPhoneNumbers();
            for (int i = 0; i < numeroContactos.size(); i++) {
                Log.i("liked","numeros -> " + numeroContactos.get(i));
                liked.put(numeroContactos.get(i),false);
            }
            liked.put("prueba",false);
            Comentario comentario = new Comentario(nuevoComentario.getText().toString(), ComunicarAvatarUsuario.getAvatarUsuario()
                    ,nombreSerie, ComunicarCurrentUser.getPhoneNumberUser(),liked,Comentario.ComentarioType.OTHER_USERS,"", (long) 0);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS);
            String key = databaseReference.push().getKey();
            databaseReference.child(key).setValue(comentario);
            DatabaseReference refdata=FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS).child(key).child("keyFB");
            refdata.setValue(key);
            nuevoComentario.setText("");
            final DatabaseReference dtRef=FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS_LEIDOS_SERIE);
            dtRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    contactos=ComunicarContactosPhoneNumber.getPhoneNumbers();
                    for(DataSnapshot snapshot:
                            dataSnapshot.getChildren()){
                       String phoneNumber = snapshot.getKey();
                       Log.i("snapshot.getKey()",phoneNumber);
                        if(!phoneNumber.equals(ComunicarCurrentUser.getPhoneNumberUser()) && contactos.contains(phoneNumber)){
                            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS_LEIDOS_SERIE)
                                    .child(phoneNumber).child(nombreSerie).child(FirebaseReferences.COM_LEIDOS);
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Long sinLeer= (Long) dataSnapshot.getValue();
                                    sinLeer++;
                                    ref.setValue(sinLeer);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

    public void loadContactFromTlf() {
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
                }
                agenda.put(phoneNumber,name);
            }

        }
        cursor.close();
    }




}
