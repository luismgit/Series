package com.example.luis.series.actividades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.luis.series.Adapters.AdaptadorComentarios;
import com.example.luis.series.Objetos.Comentario;
import com.example.luis.series.Objetos.Usuario;
import com.example.luis.series.R;
import com.example.luis.series.references.FirebaseReferences;
import com.example.luis.series.utilidades.Common;
import com.example.luis.series.utilidades.ComunicarAvatarUsuario;
import com.example.luis.series.utilidades.ComunicarContactosPhoneNumber;
import com.example.luis.series.utilidades.ComunicarCurrentUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);
        FirebaseDatabase.getInstance().goOnline();
        contactos=new ArrayList<>();
        contactos=ComunicarContactosPhoneNumber.getPhoneNumbers();
        txtSinComentarios=findViewById(R.id.mensajeSinComentarios);
        txtSinComentarios.setVisibility(View.GONE);
        nombreSerie=getIntent().getStringExtra(Common.NOMBRE_SERIE_COMENTARIOS);
        imagenSerieComentarios=findViewById(R.id.imagenSerieComentarios);
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

        nuevoComentario=findViewById(R.id.nuevoComentario);
        rv=findViewById(R.id.recyclerComentarios);
        comentarios=new ArrayList<>();
        contactosPhoneNumber=new ArrayList<>();
        contactosPhoneNumber= ComunicarContactosPhoneNumber.getPhoneNumbers();
        DatabaseReference fbref=FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS_LEIDOS_SERIE).child(ComunicarCurrentUser.getPhoneNumberUser())
                .child(nombreSerie).child(FirebaseReferences.COM_LEIDOS);
        fbref.setValue(0);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adaptadorComentarios=new AdaptadorComentarios(comentarios,this);
        rv.setAdapter(adaptadorComentarios);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comentarios.removeAll(comentarios);
                for(DataSnapshot snapshot:
                        dataSnapshot.getChildren()){
                    Comentario com = snapshot.getValue(Comentario.class);
                    Log.i("comentario",com.getAvatarUsuario());
                    /*if(contactosPhoneNumber.contains(com.getPhoneNumberUsuario())){
                        comentarios.add(com);
                    }*/
                    if(com.getSerie().equals(nombreSerie)){
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


    public void enviarNuevoComentario(View view) {
        String coment=nuevoComentario.getText().toString().trim();
        if(coment.equals("")){
            Toast.makeText(this, R.string.debe_comentar,Toast.LENGTH_SHORT).show();
        }else{
            Comentario comentario = new Comentario(nuevoComentario.getText().toString(), ComunicarAvatarUsuario.getAvatarUsuario(),nombreSerie, ComunicarCurrentUser.getPhoneNumberUser());
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS);
            databaseReference.push().setValue(comentario);
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


}
