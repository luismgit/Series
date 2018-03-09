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
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

    public List<Suscripcion> suscripciones;
    boolean flag=false;
    private Context mContext;
    //private int [] iconos = Imagenes.getIconosSeries();

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
       // holder.imagenSerie.setImageResource(iconos[suscripcion.getImagen()]);
        Glide.with(mContext)
                .load(suscripcion.getImagen())
                .into(holder.imagenSerie);
        holder.ratingBarFavoritos.setRating(suscripcion.getEstrellasUsuario());
        holder.ratingBarFavoritos.setAnimation(holder.myRotation);
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
        int contador=0;
        Double totalEstrellas;
        Double estrellas;
        String nombreSerie;
        Animation myRotation;
        RelativeLayout miVista;

        public FavoritosViewHolder(View itemView) {
            super(itemView);
            context=itemView.getContext();
            imagenSerie=itemView.findViewById(R.id.imagenSerieFavoritos);
            ratingBarFavoritos=itemView.findViewById(R.id.estrellasFav);
            textViewNombre=itemView.findViewById(R.id.nombreSerieFavoritos);
            menuFavoritos=itemView.findViewById(R.id.textViewMenuFavoritos);
            botonVoto=itemView.findViewById(R.id.botonVoto);
        }



        public void setOnclickListener(){
            menuFavoritos.setOnClickListener(this);

            //MÉTODO QUE SE EJECUTARÁ CUANDO SE PULSE EL BOTÓN DE VOTAR DE CADA VISTA
            botonVoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick( View view) {

                    //COGEMOS EL ELEMENTO RATINBAR Y LE APLICAMOS UNA ANIMACIÓN
                    miVista= (RelativeLayout) view.getParent();
                    View vista = miVista.findViewById(R.id.estrellasFav);
                    myRotation = AnimationUtils.loadAnimation(vista.getContext(), R.anim.rotator);
                    myRotation.setRepeatCount(0);
                    vista.startAnimation(myRotation);


                    nombreSerie=textViewNombre.getText().toString();
                    contador=0;
                    totalEstrellas=0.0;

                    //COGEMOS EL NODO DE SUSCRIPCIONES QUE COINCIDE CON EL USUARIO Y LA SERIE ELEGIDA Y LE AÑADIMOS EL VALOR DE LA VOTACIÓN
                    FirebaseDatabase data = FirebaseDatabase.getInstance();
                    final DatabaseReference root = data.getReference();
                    root.child(FirebaseReferences.SUSCRIPCIONES).orderByChild(FirebaseReferences.TLF_SERIE).equalTo(ComunicarCurrentUser.getPhoneNumberUser()+"_"+nombreSerie)
                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                claveSuscripcionActual = childSnapshot.getKey();
                                root.child(FirebaseReferences.SUSCRIPCIONES).child(claveSuscripcionActual).child(FirebaseReferences.ESTRELLAS_USUARIO).setValue(ratingBarFavoritos.getRating());
                                Toast.makeText(context, R.string.voto_registrado,Toast.LENGTH_SHORT).show();

                            }
                            FirebaseDatabase fbd = FirebaseDatabase.getInstance();
                            final DatabaseReference r = fbd.getReference();

                            //TAMBIÉN ACTUALIZAMOS EN EL NODO SERIE SU SPUNTUACIÓN DE TODOS LOS USUARIOS HACIENDO UNA MEDIA DE TODAS LAS VOTACIONES QUE TIENE
                            r.child(FirebaseReferences.SUSCRIPCIONES).orderByChild(FirebaseReferences.SERIE_SUSCRIPCIONES).equalTo(nombreSerie).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                        double estrellas =  childSnapshot.child(FirebaseReferences.ESTRELLAS_USUARIO).getValue(Double.class);
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
                                    d.child(FirebaseReferences.SERIES_REFERENCE).child(nombreSerie).child(FirebaseReferences.ESTRELLAS_SERIE).setValue(media);

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

        //ESTE MÉTODO SE EJECURÁ CUANDO SE PULSE EL MENÚ DE CADA VISTA
        @Override
        public void onClick(View view) {

            //CREA UN POPMENU , LO INFLA(LO PASA DE XML A JAVA) Y LE AÑADIMOS UN LISTENER PARA VER QUE ITEM SE PULSA
            PopupMenu popupMenu = new PopupMenu(context,menuFavoritos);
            popupMenu.inflate(R.menu.option_menu_favoritos);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {

                    switch (menuItem.getItemId()){

                        //EN CASO DE QUE ESCOJA ELIMINAR UN FAVORITO
                        case R.id.menu_item_borrar_favoritos:

                            //ELIMINAMOS LA SUSCRIPCIÓN DE ESE USUARIO Y LE QUITAMOS UN LIKE A LA SERIE
                            FirebaseDatabase fd =FirebaseDatabase.getInstance();
                            DatabaseReference root = fd.getReference();
                            final DatabaseReference refLikes = root.child(FirebaseReferences.SERIES_REFERENCE).child(textViewNombre.getText().toString()).child("likes");
                            refLikes.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long like = dataSnapshot.getValue(Long.class);
                                    final FirebaseDatabase database=FirebaseDatabase.getInstance();
                                    database.getReference(FirebaseReferences.SUSCRIPCIONES).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot snapshot:
                                                    dataSnapshot.getChildren()){
                                                Suscripcion suscripcion = snapshot.getValue(Suscripcion.class);


                                                if(suscripcion.getTlf_serie().equals(ComunicarCurrentUser.getPhoneNumberUser()+"_"+textViewNombre.getText().toString())){
                                                    String claveSerie = snapshot.getKey();
                                                    Log.i("Clave","--------------------------------------------------");
                                                    Log.i("Clave","suscripcion.getSerie() -> " + suscripcion.getSerie());
                                                    Log.i("Clave","textViewNombre.getText() -> " + textViewNombre.getText());
                                                    Log.i("Clave","clave Serie -> " + claveSerie);
                                                    Log.i("Clave","--------------------------------------------------");
                                                    database.getReference(FirebaseReferences.SUSCRIPCIONES).child(claveSerie).removeValue();
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
            //MUESTRA EL POPMENÚ
            popupMenu.show();
        }
    }

}
