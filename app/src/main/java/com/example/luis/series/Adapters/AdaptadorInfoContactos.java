package com.example.luis.series.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.luis.series.Objetos.Suscripcion;
import com.example.luis.series.R;
import com.example.luis.series.references.FirebaseReferences;
import com.example.luis.series.utilidades.Imagenes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorInfoContactos extends RecyclerView.Adapter<AdaptadorInfoContactos.ContactosViewHolder>{

    List<Suscripcion> suscripcionesContactos;
    //private int [] iconos = Imagenes.getIconosSeries();
    Context contexto;

    public AdaptadorInfoContactos(List<Suscripcion> suscripcionesContactos,Context contexto){
        this.suscripcionesContactos=suscripcionesContactos;
        this.contexto=contexto;
    }
    @Override
    public ContactosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_recycler_view_infocontactos,parent,false);
        ContactosViewHolder holder = new ContactosViewHolder(view);
        holder.setOnclickListener();
        return holder;
    }

    @Override
    public void onBindViewHolder(ContactosViewHolder holder, int position) {
        Suscripcion suscripcion = suscripcionesContactos.get(position);
        holder.nombreSerie.setText(suscripcion.getSerie());
       // holder.avatarSerie.setImageResource(iconos[suscripcion.getImagen()]);
        Glide.with(contexto)
                .load(suscripcion.getImagen())
                .into(holder.avatarSerie);
        //AL TEXTVIEW NOTA LE APLICAMOS LA NOTA QUE EL USUARIO ELEGIDO LE HA PUESTO SOBRE 10
        if(suscripcion.getVotada().equals("si")){
            float notaSobreDiez=(suscripcion.getEstrellasUsuario()*10)/5;
            String nota=String.valueOf(notaSobreDiez);
            if(nota.equals("10.0")){
                nota=nota.substring(0,2);
            }
            holder.textViewNota.setTextSize(25);
            holder.textViewNota.setText(nota);
        }else{
            holder.textViewNota.setTypeface(null, Typeface.NORMAL);
            holder.textViewNota.setTextColor(Color.GRAY);
            holder.textViewNota.setText("Sin valorar");
            holder.barNota.setVisibility(View.INVISIBLE);

        }


        //AL PROGRESS CIRCULAR LE APLICAMOS LA NOTA DE LA SUSCRIPCIÓN
        holder.barNota.setProgress(Math.round(suscripcion.getEstrellasUsuario()));
    }

    @Override
    public int getItemCount() {
        return suscripcionesContactos.size();
    }

    public static class ContactosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView avatarSerie;
        TextView nombreSerie;
        ProgressBar barNota;
        TextView textViewNota;
        ImageView filmaffinityLink;
        Context context;

        public ContactosViewHolder(View itemView) {
            super(itemView);
            context=itemView.getContext();
            avatarSerie=itemView.findViewById(R.id.imagenSerieContactosInfo);
            nombreSerie=itemView.findViewById(R.id.nombreSerieFavoritos);
            barNota=itemView.findViewById(R.id.progressBarNota);
            textViewNota=itemView.findViewById(R.id.textViewNota);
            filmaffinityLink=itemView.findViewById(R.id.filmaffinityImage);
        }

        public void setOnclickListener(){
            filmaffinityLink.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {

            FirebaseDatabase database= FirebaseDatabase.getInstance();
            DatabaseReference rootRef = database.getReference();
            rootRef.child(FirebaseReferences.SERIES_REFERENCE).child(nombreSerie.getText().toString()).child(FirebaseReferences.WEB_SERIE).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String web = (String) dataSnapshot.getValue();
                    irAFilmAffinity(web);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }
        public void irAFilmAffinity(String web){

            Uri uri = Uri.parse(web);
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            context.startActivity(intent);
        }
    }
}
