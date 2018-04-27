package com.maniac.luis.series.Adapters;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maniac.luis.series.Objetos.Suscripcion;
import com.maniac.luis.series.R;
import com.maniac.luis.series.actividades.ComentariosActivity;
import com.maniac.luis.series.references.FirebaseReferences;
import com.maniac.luis.series.utilidades.Common;
import com.maniac.luis.series.utilidades.ComunicarClaveUsuarioActual;
import com.maniac.luis.series.utilidades.ComunicarCurrentUser;
import com.maniac.luis.series.utilidades.CustomViewTarget;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AdaptadorFavoritos extends RecyclerView.Adapter<AdaptadorFavoritos.FavoritosViewHolder>{

    public List<Suscripcion> suscripciones;
    boolean flag=false;
    private Context mContext;
    private ViewPager vp;
    SharedPreferences sharedPref;
    boolean isShowedToturial;
    ShowcaseView showcaseView;

    public AdaptadorFavoritos(List<Suscripcion> suscripciones,Context context,ViewPager vp){
        this.suscripciones=suscripciones;
        this.mContext=context;
        this.vp=vp;
    }

    public void mostrarSegundoShowCase(){
        showcaseView=new ShowcaseView.Builder((Activity)mContext)
                .setTarget(new CustomViewTarget(R.id.iconComentarios, 100, 0, (Activity) mContext))
                .setContentTitle("Favoritos")
                .setStyle(R.style.CustomShowcaseTheme4)
                .hideOnTouchOutside()
                .setContentText("Y acceder a un foro donde podrás compartir tus opiniones solo con tus contactos.")
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showcaseView.hide();
                    }
                })
                .build();
    }


    @Override
    public FavoritosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_recycler_view_favoritos,parent,false);
        FavoritosViewHolder holder = new FavoritosViewHolder(v,suscripciones);
        return holder;
    }

    @Override
    public void onBindViewHolder(final FavoritosViewHolder holder, int position) {
        sharedPref = mContext.getSharedPreferences(Common.TUTORIAL_PREF,Context.MODE_PRIVATE);
        isShowedToturial=sharedPref.getBoolean(Common.TUTORIAL_FAVORITOS,true);
        if(position==0 && isShowedToturial) {
            vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 2 && isShowedToturial) {
                        isShowedToturial = false;
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean(Common.TUTORIAL_FAVORITOS, false);
                        editor.commit();
                        ViewTarget target1 = new ViewTarget(holder.botonVoto);
                        ViewTarget target2 = new ViewTarget(holder.imagenSerie);
                        showcaseView=new ShowcaseView.Builder((Activity)mContext)
                                .setTarget(new CustomViewTarget(R.id.botonVoto, -125, 0, (Activity) mContext))
                                .setContentTitle("Favoritos")
                                .setStyle(R.style.CustomShowcaseTheme3)
                                .setContentText("Podrás votar la serie.")
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        showcaseView.hide();
                                        mostrarSegundoShowCase();
                                    }
                                })
                                .build();

                    }

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
        final Suscripcion suscripcion = suscripciones.get(position);
        if(suscripcion.getSerie().length()>16){
            holder.textViewNombre.setTextSize(17);
        }else{
            holder.textViewNombre.setTextSize(20);
        }
        holder.textViewNombre.setText(suscripcion.getSerie());
        Glide.with(mContext)
                .load(suscripcion.getImagen())
                .fitCenter()
                .centerCrop()
                .into(holder.imagenSerie);
        holder.ratingBarFavoritos.setRating(suscripcion.getEstrellasUsuario());
        holder.ratingBarFavoritos.setAnimation(holder.myRotation);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS_LEIDOS_SERIE).child(ComunicarCurrentUser.getPhoneNumberUser())
                .child(suscripcion.getSerie()).child(FirebaseReferences.COM_LEIDOS);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long sinleer= (Long) dataSnapshot.getValue();
                try{
                    if(sinleer==0){
                        holder.numComentarios.setVisibility(View.INVISIBLE);
                    }else{
                        holder.numComentarios.setText(String.valueOf(sinleer));
                        holder.numComentarios.setVisibility(View.VISIBLE);
                    }
                }catch(Exception e){

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        Dialog miDialogo;
        List<Suscripcion> suscripciones=new ArrayList<>();
        ImageView iconoComentarios;
        TextView numComentarios;
        TextView textComentarios;

        public FavoritosViewHolder(View itemView,List suscripciones) {
            super(itemView);
            context=itemView.getContext();
            imagenSerie=itemView.findViewById(R.id.imagenSerieFavoritos);
            ratingBarFavoritos=itemView.findViewById(R.id.estrellasFav);
            textViewNombre=itemView.findViewById(R.id.nombreSerieFavoritos);
            menuFavoritos=itemView.findViewById(R.id.textViewMenuFavoritos);
            botonVoto=itemView.findViewById(R.id.botonVoto);
            iconoComentarios=itemView.findViewById(R.id.iconComentarios);
            numComentarios=itemView.findViewById(R.id.numerComentarios);
            textComentarios=itemView.findViewById(R.id.textComentarios);
            this.suscripciones=suscripciones;
        }



        public void setOnclickListener(){
            menuFavoritos.setOnClickListener(this);
            textComentarios.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    irAComentarios();
                }

            });
            iconoComentarios.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    irAComentarios();
                }
            });
            imagenSerie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    miDialogo=new Dialog(context);
                    ImageView imagen;
                    miDialogo.setContentView(R.layout.image_pop_up);
                    imagen=miDialogo.findViewById(R.id.imagenAmpliada);
                    int position = getAdapterPosition();
                    Suscripcion suscripcion = suscripciones.get(position);
                    Glide.with(context)
                            .load(suscripcion.getImagen())
                            .fitCenter()
                            .centerCrop()
                            .into(imagen);
                    miDialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    miDialogo.show();
                }
            });

            //MÉTODO QUE SE EJECUTARÁ CUANDO SE PULSE EL BOTÓN DE VOTAR DE CADA VISTA
            botonVoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick( View view) {

                    //COGEMOS EL ELEMENTO RATINBAR Y LE APLICAMOS UNA ANIMACIÓN
                    /*miVista= (RelativeLayout) view.getParent();
                    View vista = miVista.findViewById(R.id.estrellasFav);
                    myRotation = AnimationUtils.loadAnimation(vista.getContext(), R.anim.rotator);
                    myRotation.setRepeatCount(0);
                    vista.startAnimation(myRotation);*/



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
                                root.child(FirebaseReferences.SUSCRIPCIONES).child(claveSuscripcionActual).child(FirebaseReferences.SERIE_VOTADA).setValue("si");

                            }
                            FirebaseDatabase fbd = FirebaseDatabase.getInstance();
                            final DatabaseReference r = fbd.getReference();

                            //TAMBIÉN ACTUALIZAMOS EN EL NODO SERIE SU SPUNTUACIÓN DE TODOS LOS USUARIOS HACIENDO UNA MEDIA DE TODAS LAS VOTACIONES QUE TIENE
                            r.child(FirebaseReferences.SUSCRIPCIONES).orderByChild(FirebaseReferences.SERIE_SUSCRIPCIONES).equalTo(nombreSerie).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                        String votada = (String) childSnapshot.child("votada").getValue();
                                        Log.i("Puntuacion","Votada -> " + votada);
                                        if(votada.equalsIgnoreCase("si")){
                                            double estrellas =  childSnapshot.child(FirebaseReferences.ESTRELLAS_USUARIO).getValue(Double.class);
                                            Log.i("Puntuacion","Estrellas -> " + estrellas);
                                            contador++;
                                            totalEstrellas+=estrellas;
                                            Log.i("Puntuacion","totalEstrellas -> " + totalEstrellas);
                                        }

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
            @SuppressLint("RestrictedApi") Context wrapper = new ContextThemeWrapper(context, R.style.ThemeOverlay_MyTheme);
            PopupMenu popupMenu = new PopupMenu(wrapper,menuFavoritos);
            popupMenu.inflate(R.menu.option_menu_favoritos);
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
                                                    final DatabaseReference dataRef=FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.USUARIOS_REFERENCE)
                                                            .child(ComunicarClaveUsuarioActual.getClave()).child(FirebaseReferences.NUM_SUSCRIPCIONES);
                                                    dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Long numSuscripciones= (Long) dataSnapshot.getValue();
                                                            numSuscripciones--;
                                                            String nivel;
                                                            if(numSuscripciones>=5 && numSuscripciones<10){
                                                                nivel= Common.PRINCIPIANTE;
                                                            }else if(numSuscripciones>=10){
                                                                nivel=Common.INTERMEDIO;
                                                            }else{
                                                                nivel=Common.AVANZADO;
                                                            }

                                                            dataRef.setValue(numSuscripciones);
                                                            DatabaseReference datRef=FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.USUARIOS_REFERENCE)
                                                                    .child(ComunicarClaveUsuarioActual.getClave()).child("nivel");
                                                            datRef.setValue(nivel);
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

        private void irAComentarios() {
            Intent intent = new Intent(context, ComentariosActivity.class);
            intent.putExtra(Common.NOMBRE_SERIE_COMENTARIOS,textViewNombre.getText().toString());
            context.startActivity(intent);
        }
    }
    public void setFilter(List<Suscripcion> listaFavoritos){
        this.suscripciones=new ArrayList<>();
        this.suscripciones.addAll(listaFavoritos);
        notifyDataSetChanged();
    }




}
