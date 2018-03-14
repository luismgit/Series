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

import com.example.luis.series.Adapters.AdaptadorComentarios;
import com.example.luis.series.Objetos.Comentario;
import com.example.luis.series.Objetos.Usuario;
import com.example.luis.series.R;
import com.example.luis.series.references.FirebaseReferences;
import com.example.luis.series.utilidades.ComunicarAvatarUsuario;
import com.example.luis.series.utilidades.ComunicarContactosPhoneNumber;
import com.example.luis.series.utilidades.ComunicarCurrentUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ComentariosActivity extends AppCompatActivity {

    RecyclerView rv;
    List<Comentario> comentarios;
    List<String> contactosPhoneNumber;
    AdaptadorComentarios adaptadorComentarios;
    EditText nuevoComentario;
    String nombreSerie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);
        FirebaseDatabase.getInstance().goOnline();
        nombreSerie=getIntent().getStringExtra("nombreSerie");
        nuevoComentario=findViewById(R.id.nuevoComentario);
        rv=findViewById(R.id.recyclerComentarios);
        comentarios=new ArrayList<>();
        contactosPhoneNumber=new ArrayList<>();
        contactosPhoneNumber= ComunicarContactosPhoneNumber.getPhoneNumbers();
        rv.setLayoutManager(new LinearLayoutManager(this));
        adaptadorComentarios=new AdaptadorComentarios(comentarios,this);
        rv.setAdapter(adaptadorComentarios);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("comentarios");
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
                adaptadorComentarios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void enviarNuevoComentario(View view) {
        Comentario comentario = new Comentario(nuevoComentario.getText().toString(), ComunicarAvatarUsuario.getAvatarUsuario(),nombreSerie, ComunicarCurrentUser.getPhoneNumberUser());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("comentarios");

        databaseReference.push().setValue(comentario);
        nuevoComentario.setText("");
    }
}
