package com.example.luis.series.Adapters;


import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luis.series.Objetos.Suscripcion;
import com.example.luis.series.R;
import com.example.luis.series.references.FirebaseReferences;
import com.example.luis.series.utilidades.ComunicarClaveUsuarioActual;
import com.example.luis.series.utilidades.ComunicarCurrentUser;
import com.example.luis.series.utilidades.Imagenes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdaptadorFavoritos extends RecyclerView.Adapter<AdaptadorFavoritos.FavoritosViewHolder>{

    private List<Suscripcion> suscripciones;
    private Context mContext;
    private int [] iconos = Imagenes.getIconosSeries();

    public AdaptadorFavoritos(List<Suscripcion> suscripciones,Context context){
        this.suscripciones=suscripciones;
        this.mContext=context;
    }

    @Override
    public FavoritosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_recycler_view_favoritos,parent,false);
        FavoritosViewHolder holder = new FavoritosViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(FavoritosViewHolder holder, int position) {
        Suscripcion suscripcion = suscripciones.get(position);
        holder.textViewNombre.setText(suscripcion.getSerie());
        holder.imagenSerie.setImageResource(iconos[suscripcion.getImagen()]);
        holder.ratingBarFavoritos.setRating(suscripcion.getEstrellasUsuario());
        holder.setOnclickListener();
    }

    @Override
    public int getItemCount() {
        return suscripciones.size();
    }

    public static class FavoritosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imagenSerie;
        RatingBar ratingBarFavoritos;
        TextView textViewNombre;
        Context context;
        TextView menuFavoritos;
        FloatingActionButton botonVoto;
        FirebaseDatabase database;
        String claveSuscripcionActual;
        Float puntuacion;
        int contador=0;
        Double totalEstrellas;
        Double estrellas;
        String nombreSerie;

        public FavoritosViewHolder(View itemView) {
            super(itemView);
            context=itemView.getContext();
            imagenSerie=itemView.findViewById(R.id.imagenSerieFavoritos);
            ratingBarFavoritos=itemView.findViewById(R.id.estrellasFav);
            textViewNombre=itemView.findViewById(R.id.nombreSerieFavoritos);
            menuFavoritos=itemView.findViewById(R.id.textViewMenuFavoritos);
            botonVoto=itemView.findViewById(R.id.botonVoto);
            botonVoto.setTag(botonVoto.getParent());

        }

        public void setOnclickListener(){
            menuFavoritos.setOnClickListener(this);
            botonVoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    RelativeLayout miVista = (RelativeLayout) botonVoto.getTag();
                   /* ScaleAnimation animationSmall = new ScaleAnimation(1.0f, 0.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animationSmall.setDuration(1000);
                    animationSmall.setFillAfter(true);
                    miVista.startAnimation(animationSmall);
                    ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(1000);
                    animation.setFillAfter(true);
                    miVista.startAnimation(animation);*/
                    final Animation myRotation = AnimationUtils.loadAnimation(context, R.anim.rotator);
                    myRotation.setRepeatCount(0);
                    miVista.startAnimation(myRotation);
                    nombreSerie=textViewNombre.getText().toString();
                    contador=0;
                    totalEstrellas=0.0;
                    puntuacion = ratingBarFavoritos.getRating();
                    FirebaseDatabase data = FirebaseDatabase.getInstance();
                    final DatabaseReference root = data.getReference();
                    root.child("suscripciones").orderByChild("tlf_serie").equalTo(ComunicarCurrentUser.getPhoneNumberUser()+"_"+nombreSerie)
                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                claveSuscripcionActual = childSnapshot.getKey();
                                root.child("suscripciones").child(claveSuscripcionActual).child("estrellasUsuario").setValue(puntuacion);
                                Toast.makeText(context,"Voto registrado",Toast.LENGTH_LONG).show();

                            }
                            FirebaseDatabase fbd = FirebaseDatabase.getInstance();
                            final DatabaseReference r = fbd.getReference();

                            r.child("suscripciones").orderByChild("serie").equalTo(nombreSerie).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                        Double estrellas =  childSnapshot.child("estrellasUsuario").getValue(Double.class);
                                        Log.i("Puntuacion","Estrellas -> " + estrellas);
                                        contador++;
                                        totalEstrellas+=estrellas;
                                        Log.i("Puntuacion","totalEstrellas -> " + totalEstrellas);
                                    }
                                    Log.i("Puntuacion","contador " + contador);
                                    Log.i("Puntuacion","total estrellas " + totalEstrellas);
                                    Double media = totalEstrellas/contador;
                                    Log.i("Puntuacion","Media  " + media);
                                    FirebaseDatabase f = FirebaseDatabase.getInstance();
                                    DatabaseReference d = f.getReference();
                                    Log.i("Puntuacion","serie -> " + nombreSerie);
                                    d.child(FirebaseReferences.SERIES_REFERENCE).child(nombreSerie).child("estrellas").setValue(media);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            });


        }

        @Override
        public void onClick(View view) {

            PopupMenu popupMenu = new PopupMenu(context,menuFavoritos);
            popupMenu.inflate(R.menu.option_menu_favoritos);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {

                    switch (menuItem.getItemId()){

                        case R.id.menu_item_borrar_favoritos:

                            FirebaseDatabase fd =FirebaseDatabase.getInstance();
                            DatabaseReference root = fd.getReference();
                            final DatabaseReference refLikes = root.child(FirebaseReferences.SERIES_REFERENCE).child(textViewNombre.getText().toString()).child("likes");
                            refLikes.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long like = dataSnapshot.getValue(Long.class);
                                    final FirebaseDatabase database=FirebaseDatabase.getInstance();
                                    database.getReference("suscripciones").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot snapshot:
                                                    dataSnapshot.getChildren()){
                                                Suscripcion suscripcion = snapshot.getValue(Suscripcion.class);

                                               // Log.i("Clave","clave Serie -> " + claveSerie);
                                               // Log.i("Clave","idUsu -> " + suscripcion.getIdUsuario());
                                               // Log.i("Clave","clave Usu -> " + ComunicarClaveUsuarioActual.getClave());

                                                if(suscripcion.getTlf_serie().equals(ComunicarCurrentUser.getPhoneNumberUser()+"_"+textViewNombre.getText().toString())){
                                                    String claveSerie = snapshot.getKey();
                                                    Log.i("Clave","--------------------------------------------------");
                                                    Log.i("Clave","suscripcion.getSerie() -> " + suscripcion.getSerie());
                                                    Log.i("Clave","textViewNombre.getText() -> " + textViewNombre.getText());
                                                    Log.i("Clave","clave Serie -> " + claveSerie);
                                                    Log.i("Clave","--------------------------------------------------");
                                                    database.getReference("suscripciones").child(claveSerie).removeValue();
                                                }

                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    like--;
                                    refLikes.setValue(like);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            break;

                            default:
                            break;
                    }
                    return false;
                }

            });
            popupMenu.show();
        }
    }

}
