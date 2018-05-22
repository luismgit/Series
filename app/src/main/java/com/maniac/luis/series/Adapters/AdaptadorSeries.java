package com.maniac.luis.series.Adapters;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.maniac.luis.series.actividades.InfoSeriesActivity;
import com.maniac.luis.series.references.FirebaseReferences;
import com.maniac.luis.series.utilidades.Common;
import com.maniac.luis.series.utilidades.ComunicarClaveUsuarioActual;
import com.maniac.luis.series.utilidades.ComunicarCurrentUser;
import com.maniac.luis.series.utilidades.UrlApp;

import java.io.File;
import java.io.FileOutputStream;
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
         String nombreSerie = serie.getNombre();
        /* if(nombreSerie.length()>=26){
             nombreSerie=nombreSerie.substring(0,26) + Html.fromHtml("<br />") + nombreSerie.substring(26,nombreSerie.length());
         }*/
        holder.textViewNombre.setText(nombreSerie);
         String imagen = serie.getImagen();
         if(imagen.contains("null")){
             Glide.with(mContext)
                     .load(R.drawable.series_back)
                     .fitCenter()
                     .centerCrop()
                     .into(holder.iconoSerie);
             Glide.with(mContext)
                     .load(R.drawable.series_back)
                     .fitCenter()
                     .centerCrop()
                     .into(holder.iconoSerie2);
         }else{
             Glide.with(mContext)
                     .load(serie.getImagen())
                     .fitCenter()
                     .centerCrop()
                     .into(holder.iconoSerie);
             Glide.with(mContext)
                     .load(serie.getImagen())
                     .fitCenter()
                     .centerCrop()
                     .into(holder.iconoSerie2);
         }

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
        ImageView iconoSerie2;
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

        public SeriesViewHolder(View itemView,List series,ViewPager vp,AdaptadorSeries adaptadorSeries)  {
            super(itemView);
            context=itemView.getContext();
            this.series=series;
            this.vp=vp;
            relativeLayout=itemView.findViewById(R.id.relativeLayoutSeries);
            textViewNombre=itemView.findViewById(R.id.nombreSerie);
            numLikes=itemView.findViewById(R.id.numLikes);
            iconoSerie=itemView.findViewById(R.id.imagenSerie);
            iconoSerie2=itemView.findViewById(R.id.imagenSerie2);
            textViewOptions=itemView.findViewById(R.id.textViewOptionsDigit);
            ratingBar=itemView.findViewById(R.id.ratingBar);
            claveUsuarioActual= ComunicarClaveUsuarioActual.getClave();
            phoneNumber=ComunicarCurrentUser.getPhoneNumberUser();
            this.adaptadorSeries=adaptadorSeries;
            //StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
           // StrictMode.setVmPolicy(builder.build());
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
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
            Series serie = adaptadorSeries.getSeries().get(position);
            Intent intent=new Intent(context,InfoSeriesActivity.class);
            intent.putExtra(Common.CONTACTO,serie.getNombre());
            context.startActivity(intent);
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

                        case R.id.menu_item_share:

                           new MiAsync().execute();

                            break;

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





        public class MiAsync extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                iconoSerie2.setVisibility(View.INVISIBLE);

            }

            @Override
            protected Void doInBackground(Void... voids) {

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Bitmap bitmap =getBitmapFromView(iconoSerie2);
                try {
                    File file = new File(context.getExternalCacheDir(),"logicchip.png");
                    FileOutputStream fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    file.setReadable(true, false);
                    final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.te_recomiendo) + " " + textViewNombre.getText().toString()+"." + " "
                            + context.getString(R.string.descubre_mas_en) + " " + UrlApp.getUrl_app());
                    intent.setType("image/png");
                    context.startActivity(Intent.createChooser(intent, context.getString(R.string.comparte_con)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }



        private Bitmap getBitmapFromView(View view) {
            Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
            Drawable bgDrawable =view.getBackground();
            if (bgDrawable!=null) {
                //has background drawable, then draw it on the canvas
                bgDrawable.draw(canvas);
            }   else{
                //does not have background drawable, then draw white background on the canvas
                canvas.drawColor(Color.BLACK);
            }
            view.draw(canvas);
            return returnedBitmap;
        }


    }

    public void setFilter(List<Series> listaSeries){
        this.series=new ArrayList<>();
        this.series.addAll(listaSeries);
        notifyDataSetChanged();
    }
}
