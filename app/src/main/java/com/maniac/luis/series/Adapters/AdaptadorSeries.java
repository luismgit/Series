package com.maniac.luis.series.Adapters;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maniac.luis.series.Objetos.Series;
import com.maniac.luis.series.Objetos.Suscripcion;
import com.maniac.luis.series.R;
import com.maniac.luis.series.references.FirebaseReferences;
import com.maniac.luis.series.utilidades.Common;
import com.maniac.luis.series.utilidades.ComunicarClaveUsuarioActual;
import com.maniac.luis.series.utilidades.ComunicarCurrentUser;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class AdaptadorSeries extends RecyclerView.Adapter<AdaptadorSeries.SeriesViewHolder> {

    List<Series> series;
    private Context mContext;
    private Hashtable<String,String> contactos;
    Series serie;
    ViewPager vp;

    public AdaptadorSeries(List<Series> series,Context mContext,ViewPager vp) {
        this.series = series;
        this.mContext=mContext;
        this.vp=vp;

    }

    public List<Series> getSeries(){
        return this.series;
    }

    @Override
    public SeriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_recycler_view_series,parent,false);
        SeriesViewHolder holder = new SeriesViewHolder(v,series,vp,this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final SeriesViewHolder holder, int position) {

         serie = series.get(position);
        holder.textViewNombre.setText(serie.getNombre());
        Glide.with(mContext)
                .load(serie.getImagen())
                .fitCenter()
                .centerCrop()
                .into(holder.iconoSerie);
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
        TextView textComentarios;
        ImageView iconoComentarios;
        TextView numComentarios;
        Dialog miDialogo;
        Context contexto;
        List<Series> series=new ArrayList<>();
        ViewPager vp;
        AdaptadorSeries adaptadorSeries;

        public SeriesViewHolder(View itemView,List series,ViewPager vp,AdaptadorSeries adaptadorSeries) {
            super(itemView);
            context=itemView.getContext();
            this.series=series;
            this.vp=vp;
            relativeLayout=itemView.findViewById(R.id.relativeLayout);
            textViewNombre=itemView.findViewById(R.id.nombreSerie);
            numLikes=itemView.findViewById(R.id.numLikes);
            iconoSerie=itemView.findViewById(R.id.imagenSerie);
            textViewOptions=itemView.findViewById(R.id.textViewOptionsDigit);
            ratingBar=itemView.findViewById(R.id.ratingBar);
            claveUsuarioActual= ComunicarClaveUsuarioActual.getClave();
            phoneNumber=ComunicarCurrentUser.getPhoneNumberUser();
            this.adaptadorSeries=adaptadorSeries;
        }

        public void setOnclickListener(){
            textViewOptions.setOnClickListener(this);
            iconoSerie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    miDialogo=new Dialog(context);
                    ImageView imagen;
                    miDialogo.setContentView(R.layout.image_pop_up);
                    imagen=miDialogo.findViewById(R.id.imagenAmpliada);
                    int position = getAdapterPosition();
                    Series serie = adaptadorSeries.getSeries().get(position);
                    Glide.with(context)
                            .load(serie.getImagen())
                            .fitCenter()
                            .centerCrop()
                            .into(imagen);
                    miDialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    miDialogo.show();
                }
            });

        }


        //MÉTODO QUE SE EJECUTARÁ CUANDO SE PULSE EL MENÚ DE CADA VISTA

        @Override
        public void onClick(View view) {
            @SuppressLint("RestrictedApi") Context wrapper = new ContextThemeWrapper(context, R.style.ThemeOverlay_MyTheme);
            repetidoFavorito=false;
            PopupMenu popupMenu = new PopupMenu(wrapper, textViewOptions);
            popupMenu.inflate(R.menu.option_menu);

            try {
                Field[] fields = popupMenu.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(popupMenu);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper
                                .getClass().getName());
                        Method setForceIcons = classPopupHelper.getMethod(
                                "setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        //EN EL CASO DE QUE SE PULSE AÑADIR A FAVORITOS
                        case R.id.menu_item_favoritos:
                            database=FirebaseDatabase.getInstance();
                            rootRef = database.getReference();

                            //CREAMOS UNA REFERENCIA AL NODO LIKES DE LA SERIE DE LA VISTA SELECCIONADA
                            refLikes = rootRef.child(FirebaseReferences.SERIES_REFERENCE).child(textViewNombre.getText().toString()).child(FirebaseReferences.LIKES_SERIE);
                            refLikes.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    suscripciones = dataSnapshot.getValue(Long.class);
                                    FirebaseDatabase data = FirebaseDatabase.getInstance();

                                    //CONSEGUIMOS LA IMAGEN DE AVATAR DE LA SERIE Y COMPROBAMOS QUE ESA SERIE NO LA TENGA YA EL USUARIO EN SUS SUSCRIPCIONES CON repetidoFavorito
                                    data.getReference(FirebaseReferences.SERIES_REFERENCE).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                for(DataSnapshot snapshot:
                                                        dataSnapshot.getChildren()){
                                                    Series serie = snapshot.getValue(Series.class);
                                                    if(serie.getNombre().equals(textViewNombre.getText())){
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
                                                                            ,ComunicarCurrentUser.getPhoneNumberUser()+"_"+textViewNombre.getText().toString(),"no");
                                                                    dbr.child(FirebaseReferences.SUSCRIPCIONES).push().setValue(suscripcion);
                                                                    suscripciones++;
                                                                    refLikes.setValue(suscripciones);
                                                                    final DatabaseReference dataRef=FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.USUARIOS_REFERENCE)
                                                                            .child(ComunicarClaveUsuarioActual.getClave()).child(FirebaseReferences.NUM_SUSCRIPCIONES);
                                                                    dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                            Long numSuscripciones= (Long) dataSnapshot.getValue();
                                                                            numSuscripciones++;
                                                                            String nivel;
                                                                            if(numSuscripciones>=5 && numSuscripciones<10){
                                                                                nivel= Common.INTERMEDIO;
                                                                            }else if(numSuscripciones>=10){
                                                                                nivel=Common.AVANZADO;
                                                                            }else{
                                                                                nivel=Common.PRINCIPIANTE;
                                                                            }

                                                                            dataRef.setValue(numSuscripciones);
                                                                            DatabaseReference datRef=FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.USUARIOS_REFERENCE)
                                                                                    .child(ComunicarClaveUsuarioActual.getClave()).child(FirebaseReferences.NIVEL_USUARIO);
                                                                            datRef.setValue(nivel);
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                        }
                                                                    });
                                                                    SharedPreferences sp = context.getSharedPreferences(Common.TUTORIAL_PREF,context.getApplicationContext().MODE_PRIVATE);
                                                                    if(sp.getBoolean(Common.TUTORIAL_FAVORITOS,true)){
                                                                        vp.setCurrentItem(2);
                                                                    }
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

    public void setFilter(List<Series> listaSeries){
        this.series=new ArrayList<>();
        this.series.addAll(listaSeries);
        notifyDataSetChanged();
    }
}
