package com.maniac.luis.series.Adapters;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.maniac.luis.series.MovieDbInterface.SeriesActoresResult;
import com.maniac.luis.series.R;
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
        ActoresViewHolder holder = new ActoresViewHolder(view,context,this,urlImagenSerie);
        return holder;
    }

    @Override
    public void onBindViewHolder(ActoresViewHolder holder, int position) {
        SeriesActoresResult.CastBean actor = actores.get(position);
        String imagenReparto=actor.getProfile_path();
        if(imagenReparto==null || imagenReparto.equals("")){
            Glide.with(context)
                    .load(urlImagenSerie)
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

        public ActoresViewHolder(View itemView,Context context,AdaptadorActores adaptadorActores,String urlImagenSerie) {
            super(itemView);
            this.context=context;
            imagenActor=itemView.findViewById(R.id.imagenActor);
            actorSerie=itemView.findViewById(R.id.nombreActorSerie);
            actorReal=itemView.findViewById(R.id.nombreActorReal);
            this.adaptadorActores=adaptadorActores;
            this.urlImagenSerie=urlImagenSerie;
        }

        public void setOnclickListener(){

            imagenActor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    miDialogo=new Dialog(context);
                    ImageView imagen;
                    miDialogo.setContentView(R.layout.image_pop_up);
                    imagen=miDialogo.findViewById(R.id.imagenAmpliada);
                    int position = getAdapterPosition();
                    SeriesActoresResult.CastBean actor = adaptadorActores.getActores().get(position);
                    if(actor.getProfile_path()==null){
                        Glide.with(context)
                                .load(urlImagenSerie)
                                .fitCenter()
                                .centerCrop()
                                .into(imagen);
                    }else{
                        Glide.with(context)
                                .load(Common.BASE_URL_POSTER+actor.getProfile_path())
                                .fitCenter()
                                .centerCrop()
                                .into(imagen);
                    }

                    miDialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    miDialogo.show();
                }
            });


        }

    }
}
