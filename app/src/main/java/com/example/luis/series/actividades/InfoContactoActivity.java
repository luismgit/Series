package com.example.luis.series.actividades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.luis.series.Adapters.AdaptadorInfoContactos;
import com.example.luis.series.Objetos.Suscripcion;
import com.example.luis.series.R;
import com.example.luis.series.utilidades.Imagenes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InfoContactoActivity extends AppCompatActivity {

private TextView nombreUsuario;
private ImageView avatarUsuario;
private int [] avatares;
    private List<Suscripcion> suscripciones;
    private RecyclerView rv;
    private AdaptadorInfoContactos adaptadorInfoContactos;
    private String telefonoUsuarioSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_contacto);
        avatares= Imagenes.getAvataresUsuarios();
        nombreUsuario=findViewById(R.id.nombreContacto);
        avatarUsuario=findViewById(R.id.avatarContacto);
        nombreUsuario.setText(getIntent().getStringExtra("contacto")+ " Favoritos.");
        avatarUsuario.setImageResource(avatares[getIntent().getIntExtra("avatar",0)]);
        telefonoUsuarioSeleccionado=getIntent().getStringExtra("telefono");
        rv=findViewById(R.id.recyclerInfoContacto);
        rv.setLayoutManager(new LinearLayoutManager(this));
        suscripciones=new ArrayList<>();
        adaptadorInfoContactos=new AdaptadorInfoContactos(suscripciones);
        rv.setAdapter(adaptadorInfoContactos);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("suscripciones").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                suscripciones.removeAll(suscripciones);
                for(DataSnapshot snapshot:
                        dataSnapshot.getChildren()){
                    Suscripcion suscripcion=snapshot.getValue(Suscripcion.class);
                    if(suscripcion.getTelefono().equals(telefonoUsuarioSeleccionado)){
                        Log.i("susc","telefono -> " + suscripcion.getTelefono());
                        Log.i("susc","telefonoUsuarioSeleccionado -> " + telefonoUsuarioSeleccionado);
                        suscripciones.add(suscripcion);
                    }
                }
                adaptadorInfoContactos.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
