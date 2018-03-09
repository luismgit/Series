package com.example.luis.series.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.luis.series.Objetos.Series;
import com.example.luis.series.Objetos.Suscripcion;
import com.example.luis.series.Objetos.Usuario;
import com.example.luis.series.R;
import com.example.luis.series.references.FirebaseReferences;
import com.example.luis.series.utilidades.ComunicarClaveUsuarioActual;
import com.example.luis.series.utilidades.ComunicarCurrentUser;
import com.example.luis.series.utilidades.Imagenes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.sql.Ref;
import java.util.List;


public class AdaptadorSeries extends RecyclerView.Adapter<AdaptadorSeries.SeriesViewHolder> {

    List<Series> series;
    private Context mContext;
    //private int [] iconos = Imagenes.getIconosSeries();

    public AdaptadorSeries(List<Series> series,Context mContext) {
        this.series = series;
        this.mContext=mContext;
    }

    @Override
    public SeriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_recycler_view_series,parent,false);
        SeriesViewHolder holder = new SeriesViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final SeriesViewHolder holder, int position) {
        Series serie = series.get(position);
        holder.textViewNombre.setText(serie.getNombre());
        Glide.with(mContext)
                .load(serie.getImagen())
                .into(holder.iconoSerie);
       // holder.iconoSerie.setImageResource(iconos[serie.getImagen()]);
        holder.numLikes.setText("" + serie.getLikes());
        holder.ratingBar.setRating(serie.getEstrellas());
        holder.setOnclickListener();

    }

    @Override
    public int getItemCount() {
        return series.size();
    }




    public static class SeriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        RatingBar ratingBar;
        TextView textViewNombre;
        ImageView iconoSerie;
        TextView numLikes;
        TextView textViewOptions;
        String claveUsuarioActual;
        String phoneNumber;
        Context context;
        DatabaseReference refLikes;
        RelativeLayout relativeLayout;
        FirebaseDatabase database;
        DatabaseReference rootRef;
        String imagenSuscripcion;
        long suscripciones;
        boolean repetidoFavorito;

        public SeriesViewHolder(View itemView) {
            super(itemView);
            context=itemView.getContext();
            relativeLayout=itemView.findViewById(R.id.relativeLayout);
            textViewNombre=itemView.findViewById(R.id.nombreSerie);
            numLikes=itemView.findViewById(R.id.numLikes);
            iconoSerie=itemView.findViewById(R.id.imagenSerie);
            textViewOptions=itemView.findViewById(R.id.textViewOptionsDigit);
            ratingBar=itemView.findViewById(R.id.ratingBar);
            claveUsuarioActual= ComunicarClaveUsuarioActual.getClave();
            phoneNumber=ComunicarCurrentUser.getPhoneNumberUser();

        }

        public void setOnclickListener(){
            textViewOptions.setOnClickListener(this);

        }


        //MÉTODO QUE SE EJECUTARÁ CUANDO SE PULSE EL MENÚ DE CADA VISTA
        @Override
        public void onClick(View view) {

            Log.i("HOLDER", "pulsado " );
            repetidoFavorito=false;
            PopupMenu popupMenu = new PopupMenu(context,textViewOptions);
            popupMenu.inflate(R.menu.option_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {

                    switch (menuItem.getItemId()){
                        //EN EL CASO DE QUE SE PULSE AÑADIR A FAVORITOS
                        case R.id.menu_item_favoritos:
                            Log.i("HOLDER", "pulsado favoritos" );
                            database=FirebaseDatabase.getInstance();
                            rootRef = database.getReference();

                            //CREAMOS UNA REFERENCIA AL NODO LIKES DE LA SERIE DE LA VISTA SELECCIONADA
                            refLikes = rootRef.child(FirebaseReferences.SERIES_REFERENCE).child(textViewNombre.getText().toString()).child(FirebaseReferences.LIKES_SERIE);
                            refLikes.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    suscripciones = dataSnapshot.getValue(Long.class);
                                    Log.i("HOLDER","likes -> " + suscripciones);
                                    FirebaseDatabase data = FirebaseDatabase.getInstance();

                                    //CONSEGUIMOS LA IMAGEN DE AVATAR DE LA SERIE Y COMPROBAMOS QUE ESA SERIE NO LA TENGA YA EL USUARIO EN SUS SUSCRIPCIONES CON repetidoFavorito
                                    data.getReference(FirebaseReferences.SERIES_REFERENCE).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                for(DataSnapshot snapshot:
                                                        dataSnapshot.getChildren()){
                                                    Series serie = snapshot.getValue(Series.class);
                                                    if(serie.getNombre().equals(textViewNombre.getText())){
                                                        Log.i("imagen","imagen -> " + serie.getNombre());
                                                        Log.i("imagen","imagen -> " + serie.getImagen());
                                                        imagenSuscripcion=serie.getImagen();
                                                        DatabaseReference df = FirebaseDatabase.getInstance().getReference();
                                                        df.child(FirebaseReferences.SUSCRIPCIONES).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                for(DataSnapshot snapshot:
                                                                        dataSnapshot.getChildren()){
                                                                    Suscripcion s = snapshot.getValue(Suscripcion.class);
                                                                    if(s.getTlf_serie().equals(ComunicarCurrentUser.getPhoneNumberUser()+"_"+textViewNombre.getText().toString())){
                                                                        repetidoFavorito=true;
                                                                        Toast.makeText(context,textViewNombre.getText().toString() + " " +  context.getString(R.string.rep_favoritos),Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                                //SI NO LA TIENE YA EN FAVORITOS AÑADIMOS LA SERIE A LAS SUSCRIPCIONES DEL USUSARIO ACTUAL Y  +1 AL CONTADOR DE SUSCRIPCIONES QUE TIENE LA SERIE
                                                                if(!repetidoFavorito){
                                                                    DatabaseReference dbr=FirebaseDatabase.getInstance().getReference();
                                                                    Suscripcion suscripcion = new Suscripcion(claveUsuarioActual,textViewNombre.getText().toString(), (float) 0,phoneNumber,imagenSuscripcion
                                                                            ,ComunicarCurrentUser.getPhoneNumberUser()+"_"+textViewNombre.getText().toString());
                                                                    //df.child("suscripciones").child(claveUsuarioActual).setValue(suscripcion);
                                                                    dbr.child(FirebaseReferences.SUSCRIPCIONES).push().setValue(suscripcion);
                                                                    suscripciones++;
                                                                    refLikes.setValue(suscripciones);
                                                                    Toast.makeText(context,textViewNombre.getText().toString() + " " + context.getString(R.string.anadida_fav),Toast.LENGTH_LONG).show();
                                                                }
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

                                @Override
                                public void onCancelled(DatabaseError databaseError) {


                                }
                            });

                            break;

                        //EN EL CASO DE QUE SE PULSE VER EN FILMAFFINITY ACCEDEMOS AL NODO QUE GUARDA EL ENLACE DE CADA SERIE E INCIAMOS UN INTENT HACIA ESA WEB
                        case (R.id.menu_item_filmaffinity):
                            database=FirebaseDatabase.getInstance();
                             rootRef = database.getReference();
                             rootRef.child(FirebaseReferences.SERIES_REFERENCE).child(textViewNombre.getText().toString()).child(FirebaseReferences.WEB_SERIE).addListenerForSingleValueEvent(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(DataSnapshot dataSnapshot) {
                                     String web = (String) dataSnapshot.getValue();
                                     irAFilmAffinity(web);
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

        public void irAFilmAffinity(String web){

            Uri uri = Uri.parse(web);
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            context.startActivity(intent);
        }


    }


}
