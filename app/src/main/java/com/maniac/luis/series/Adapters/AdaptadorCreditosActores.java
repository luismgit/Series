package com.maniac.luis.series.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.maniac.luis.series.MovieDbInterface.SeriesCreditosActorResult;
import com.maniac.luis.series.R;
import com.maniac.luis.series.utilidades.Common;

import java.util.List;

public class AdaptadorCreditosActores extends RecyclerView.Adapter<AdaptadorCreditosActores.CreditosViewHolder>{

    List<SeriesCreditosActorResult.CastBean> papeles;
    Context context;
    String urlImagenActorDefecto;

    public AdaptadorCreditosActores(List<SeriesCreditosActorResult.CastBean> papeles, Context context,String urlImagenActorDefecto){
        this.papeles=papeles;
        this.context=context;
        this.urlImagenActorDefecto=urlImagenActorDefecto;
    }

    @Override
    public CreditosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_recycler_credito,parent,false);
        AdaptadorCreditosActores.CreditosViewHolder holder = new AdaptadorCreditosActores.CreditosViewHolder(view,context,this);
        return holder;
    }

    @Override
    public void onBindViewHolder(CreditosViewHolder holder, int position) {
        SeriesCreditosActorResult.CastBean papel = papeles.get(position);
        String imagenSeriePapel=papel.getPoster_path();
        if(imagenSeriePapel==null || imagenSeriePapel.equals("")){
            Glide.with(context)
                    .load(urlImagenActorDefecto)
                    .fitCenter()
                    .centerCrop()
                    .into(holder.imagenSerie);
        }else{
            Glide.with(context)
                    .load(Common.BASE_URL_POSTER+imagenSeriePapel)
                    .fitCenter()
                    .centerCrop()
                    .into(holder.imagenSerie);
        }

        String nombreSeriePapel = papel.getName();
        if (nombreSeriePapel.equals("") || nombreSeriePapel==null){
            holder.tituloSerie.setText(R.string.desconocido);
        }else{
            holder.tituloSerie.setText(nombreSeriePapel);
        }
        holder.setOnclickListener();
    }

    @Override
    public int getItemCount() {
        return papeles.size();
    }

    public static class CreditosViewHolder extends RecyclerView.ViewHolder {

        ImageView imagenSerie;
        TextView tituloSerie;
        AdaptadorCreditosActores adaptadorCreditosActores;
        Context context;

        public CreditosViewHolder(View itemView,Context context,AdaptadorCreditosActores adaptadorCreditosActores) {
            super(itemView);
            imagenSerie=itemView.findViewById(R.id.imagenSerieCredito);
            tituloSerie=itemView.findViewById(R.id.nombreSerieCredito);
            this.adaptadorCreditosActores=adaptadorCreditosActores;
            this.context=context;
        }

        public void setOnclickListener(){

        }
    }
}
