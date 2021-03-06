package com.maniac.luis.series.actividades;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maniac.luis.series.Adapters.AdaptadorInfoContactos;
import com.maniac.luis.series.Objetos.Suscripcion;
import com.maniac.luis.series.R;
import com.maniac.luis.series.references.FirebaseReferences;
import com.maniac.luis.series.utilidades.Common;
import com.maniac.luis.series.utilidades.NetworkStatus;

import java.util.ArrayList;
import java.util.List;

public class InfoContactoActivity extends AppCompatActivity {

private TextView nombreUsuario;
private ImageView avatarUsuario;

    private List<Suscripcion> suscripciones;
    private RecyclerView rv;
    private AdaptadorInfoContactos adaptadorInfoContactos;
    private String telefonoUsuarioSeleccionado;
    private TextView sinFavoritos;
    private ImageView imagenContacto;
    private Dialog miDialogo;
    String imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_contacto);
        if(!NetworkStatus.isConnected(InfoContactoActivity.this)) NetworkStatus.buildDialog(InfoContactoActivity.this).show();
        FirebaseDatabase.getInstance().goOnline();
        nombreUsuario=findViewById(R.id.nombreContacto);
        avatarUsuario=findViewById(R.id.avatarContacto);
        sinFavoritos=findViewById(R.id.sinFavoritos);
        imagenContacto=findViewById(R.id.avatarContacto);


        String contact = getIntent().getStringExtra(Common.CONTACTO);
        nombreUsuario.setText(contact);

        //LE ASIGNAMOS SU AVATAR
        imagen=getIntent().getStringExtra(Common.AVATAR);
        Glide.with(this)
                .load(imagen)
                .fitCenter()
                .centerCrop()
                .into(avatarUsuario);
        imagenContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                miDialogo=new Dialog(InfoContactoActivity.this);
                ImageView imagenC;
                miDialogo.setContentView(R.layout.image_pop_up);
                imagenC=miDialogo.findViewById(R.id.imagenAmpliada);
                Glide.with(InfoContactoActivity.this)
                        .load(imagen)
                        .fitCenter()
                        .centerCrop()
                        .into(imagenC);
                miDialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                miDialogo.show();
            }
        });
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
