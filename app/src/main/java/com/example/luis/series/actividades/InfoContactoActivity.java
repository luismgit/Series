package com.example.luis.series.actividades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.luis.series.Adapters.AdaptadorInfoContactos;
import com.example.luis.series.Objetos.Suscripcion;
import com.example.luis.series.R;
import com.example.luis.series.references.FirebaseReferences;
import com.example.luis.series.utilidades.Common;
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
    private TextView sinFavoritos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_contacto);
        avatares= Imagenes.getAvataresUsuarios();
        nombreUsuario=findViewById(R.id.nombreContacto);
        avatarUsuario=findViewById(R.id.avatarContacto);
        sinFavoritos=findViewById(R.id.sinFavoritos);

        //RECOGEMOS EL NOMBRE DEL CONTACTO QUE QUEREMOS VISUALIZAR
        /*String contact=getIntent().getStringExtra(Common.CONTACTO);
        if(contact.length()>10){
            contact=contact.substring(0,10);
            contact=contact+"...";
        }*/
        nombreUsuario.setText(getIntent().getStringExtra(Common.CONTACTO));

        //LE ASIGNAMOS SU AVATAR
        //avatarUsuario.setImageResource(avatares[getIntent().getIntExtra(Common.AVATAR,0)]);
        Glide.with(this)
                .load(getIntent().getStringExtra(Common.AVATAR))
                .fitCenter()
                .centerCrop()
                .into(avatarUsuario);

        //RECOGEMOS EL TELÉFONO DEL USUARIO SELECCIONADO
        telefonoUsuarioSeleccionado=getIntent().getStringExtra(Common.TELEFONO);
        rv=findViewById(R.id.recyclerInfoContacto);
        rv.setLayoutManager(new LinearLayoutManager(this));
        suscripciones=new ArrayList<>();
        adaptadorInfoContactos=new AdaptadorInfoContactos(suscripciones,this.getBaseContext());
        rv.setAdapter(adaptadorInfoContactos);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //CONSEGUIMOS DE TODAS LAS SUSCRIPCIONES SOLO LAS DEL USUARIO SELECCIONADO Y LAS AÑADIMOS AL ARRAYLIST SUSCRIPCIONES
        database.getReference(FirebaseReferences.SUSCRIPCIONES).addValueEventListener(new ValueEventListener() {
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
                //SI NO TIENE SUSCRIPCIONES MOSTRAMOS EL MENSAJE
                if(suscripciones.size()==0){
                    sinFavoritos.setText(R.string.usu_sin_fav);
                }
                adaptadorInfoContactos.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
