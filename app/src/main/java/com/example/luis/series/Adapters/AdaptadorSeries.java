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
    private int [] iconos = Imagenes.getIconosSeries();

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
        holder.iconoSerie.setImageResource(iconos[serie.getImagen()]);
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
        TextView textViewOptions;
        String claveUsuarioActual;
        String phoneNumber;
        Context context;
        DatabaseReference refLikes;
        RelativeLayout relativeLayout;
        FirebaseDatabase database;
        DatabaseReference rootRef;
        int imagenSuscripcion;
        long suscripciones;

        public SeriesViewHolder(View itemView) {
            super(itemView);
            context=itemView.getContext();
            relativeLayout=itemView.findViewById(R.id.relativeLayout);
            textViewNombre=itemView.findViewById(R.id.nombreSerie);
            iconoSerie=itemView.findViewById(R.id.imagenSerie);
            textViewOptions=itemView.findViewById(R.id.textViewOptionsDigit);
            ratingBar=itemView.findViewById(R.id.ratingBar);
            claveUsuarioActual= ComunicarClaveUsuarioActual.getClave();
            phoneNumber=ComunicarCurrentUser.getPhoneNumberUser();
        }

        public void setOnclickListener(){
            textViewOptions.setOnClickListener(this);
          /*  relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("HOLDER", "pulsado " );
                    PopupMenu popupMenu = new PopupMenu(context,textViewOptions);
                    popupMenu.inflate(R.menu.option_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            switch (menuItem.getItemId()){
                                case R.id.menu_item_favoritos:
                                    Log.i("HOLDER", "pulsado favoritos" );
                                    FirebaseDatabase database=FirebaseDatabase.getInstance();
                                    DatabaseReference rootRef = database.getReference();
                                    refLikes = rootRef.child(FirebaseReferences.SERIES_REFERENCE).child(textViewNombre.getText().toString()).child("likes");
                                    refLikes.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            long suscripciones = dataSnapshot.getValue(Long.class);
                                            Log.i("HOLDER","likes -> " + suscripciones);
                                            DatabaseReference df = FirebaseDatabase.getInstance().getReference();
                                            df.child("suscripciones").child(claveUsuarioActual).child(textViewNombre.getText().toString()).setValue(phoneNumber);
                                            suscripciones++;
                                            refLikes.setValue(suscripciones);

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {


                                        }
                                    });
                                    Toast.makeText(context,"Añadido a favoritos",Toast.LENGTH_LONG).show();
                                    break;
                                case (R.id.menu_item_filmaffinity):


                                    break;
                                default:
                                    break;

                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });*/
        }


        @Override
        public void onClick(View view) {

            Log.i("HOLDER", "pulsado " );
            PopupMenu popupMenu = new PopupMenu(context,textViewOptions);
            popupMenu.inflate(R.menu.option_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {

                    switch (menuItem.getItemId()){
                        case R.id.menu_item_favoritos:
                            Log.i("HOLDER", "pulsado favoritos" );
                            database=FirebaseDatabase.getInstance();
                            rootRef = database.getReference();
                            refLikes = rootRef.child(FirebaseReferences.SERIES_REFERENCE).child(textViewNombre.getText().toString()).child("likes");
                            refLikes.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    suscripciones = dataSnapshot.getValue(Long.class);
                                    Log.i("HOLDER","likes -> " + suscripciones);
                                    FirebaseDatabase data = FirebaseDatabase.getInstance();
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
                                                        Suscripcion suscripcion = new Suscripcion(claveUsuarioActual,textViewNombre.getText().toString(), (float) 0,phoneNumber,imagenSuscripcion
                                                                ,ComunicarCurrentUser.getPhoneNumberUser()+"_"+textViewNombre.getText().toString());
                                                        //df.child("suscripciones").child(claveUsuarioActual).setValue(suscripcion);
                                                        df.child("suscripciones").push().setValue(suscripcion);
                                                        suscripciones++;
                                                        refLikes.setValue(suscripciones);
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
                            Toast.makeText(context,"Añadido a favoritos",Toast.LENGTH_LONG).show();
                            break;
                        case (R.id.menu_item_filmaffinity):
                            database=FirebaseDatabase.getInstance();
                             rootRef = database.getReference();
                             rootRef.child(FirebaseReferences.SERIES_REFERENCE).child(textViewNombre.getText().toString()).child("web").addListenerForSingleValueEvent(new ValueEventListener() {
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
