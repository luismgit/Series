package com.maniac.luis.series.Adapters;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.maniac.luis.series.MovieDbInterface.SeriesActoresResult;
import com.maniac.luis.series.R;
import com.maniac.luis.series.actividades.InfoActorActivity;
import com.maniac.luis.series.utilidades.Common;

import java.util.List;

public class AdaptadorActores extends RecyclerView.Adapter<AdaptadorActores.ActoresViewHolder>{

    List<SeriesActoresResult.CastBean> actores;
    Context context;
    String urlImagenSerie;

    public AdaptadorActores(List<SeriesActoresResult.CastBean> actores,String urlImagenSerie, Context context){
      this.actores=actores;
      this.context=context;
      this.urlImagenSerie=urlImagenSerie;
    }

    public List<SeriesActoresResult.CastBean> getActores(){
        return actores;
    }


    @Override
    public ActoresViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_recycler_actor,parent,false);
        ActoresViewHolder holder = new ActoresViewHolder(view,context,this,urlImagenSerie,actores);
        return holder;
    }

    @Override
    public void onBindViewHolder(ActoresViewHolder holder, int position) {
        SeriesActoresResult.CastBean actor = actores.get(position);
        String imagenReparto=actor.getProfile_path();
        if(imagenReparto==null || imagenReparto.equals("")){
            Glide.with(context)
                    .load(Common.BASE_URL_POSTER+urlImagenSerie)
                    .fitCenter()
                    .centerCrop()
                    .into(holder.imagenActor);
        }else{
            Glide.with(context)
                    .load(Common.BASE_URL_POSTER+imagenReparto)
                    .fitCenter()
                    .centerCrop()
                    .into(holder.imagenActor);
        }
        String nombreActorSerie = actor.getCharacter();
        if (nombreActorSerie.equals("") || nombreActorSerie==null){
            holder.actorSerie.setText(R.string.desconocido);
        }else{
            holder.actorSerie.setText(nombreActorSerie);
        }
        String nombreActorReal = actor.getName();
        if (nombreActorReal.equals("") || nombreActorReal==null){
            holder.actorReal.setText(R.string.desconocido);
        }else{
            holder.actorReal.setText(nombreActorReal);
        }
        holder.setOnclickListener();

    }

    @Override
    public int getItemCount() {
        return actores.size();
    }

    public static class ActoresViewHolder extends RecyclerView.ViewHolder{

        ImageView imagenActor;
        TextView actorSerie,actorReal;
        Dialog miDialogo;
        Context context;
        AdaptadorActores adaptadorActores;
        String urlImagenSerie;
        List<SeriesActoresResult.CastBean> actores;

        public ActoresViewHolder(View itemView,Context context,AdaptadorActores adaptadorActores,String urlImagenSerie,List actores) {
            super(itemView);
            this.context=context;
            imagenActor=itemView.findViewById(R.id.imagenActor);
            actorSerie=itemView.findViewById(R.id.nombreActorSerie);
            actorReal=itemView.findViewById(R.id.nombreActorReal);
            this.adaptadorActores=adaptadorActores;
            this.urlImagenSerie=urlImagenSerie;
            this.actores=actores;
        }

        public void setOnclickListener(){

            imagenActor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   intentVerActor();
                }
            });

            actorReal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   intentVerActor();
                }
            });

            actorSerie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intentVerActor();
                }
            });


        }

        public void intentVerActor(){
            //Toast.makeText(context,"Actor -> " + actores.get(getAdapterPosition()).getId(),Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, InfoActorActivity.class);
            SeriesActoresResult.CastBean actor = actores.get(getAdapterPosition());
            intent.putExtra(Common.SERIE_OBJETO,actor);
            context.startActivity(intent);
        }

    }
}
