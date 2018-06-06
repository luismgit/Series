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

public class AdaptadorCreditosProduccionActores extends RecyclerView.Adapter<AdaptadorCreditosProduccionActores.CreditosProduccionViewHolder>{

    List<SeriesCreditosActorResult.CrewBean> papeles;
    Context context;

    public AdaptadorCreditosProduccionActores(List<SeriesCreditosActorResult.CrewBean> papeles, Context context){
        this.papeles=papeles;
        this.context=context;
    }

    @Override
    public CreditosProduccionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_recycler_credito,parent,false);
        AdaptadorCreditosProduccionActores.CreditosProduccionViewHolder holder = new AdaptadorCreditosProduccionActores.CreditosProduccionViewHolder(view,context,this);
        return holder;
    }

    @Override
    public void onBindViewHolder(CreditosProduccionViewHolder holder, int position) {
        SeriesCreditosActorResult.CrewBean produccion = papeles.get(position);
        String imagenSeriePapel=produccion.getPoster_path();
        if(imagenSeriePapel==null || imagenSeriePapel.equals("")){

            Glide.with(context)
                    .load(R.drawable.series_back)
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

        String nombreSeriePapel = produccion.getName();
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

    public static class CreditosProduccionViewHolder extends RecyclerView.ViewHolder {

        ImageView imagenSerie;
        TextView tituloSerie;
        AdaptadorCreditosProduccionActores adaptadorCreditosProduccionActores;
        Context context;

        public CreditosProduccionViewHolder(View itemView,Context context,AdaptadorCreditosProduccionActores adaptadorCreditosProduccionActores) {
            super(itemView);
            imagenSerie=itemView.findViewById(R.id.imagenSerieCredito);
            tituloSerie=itemView.findViewById(R.id.nombreSerieCredito);
            this.adaptadorCreditosProduccionActores=adaptadorCreditosProduccionActores;
            this.context=context;
        }

        public void setOnclickListener(){

        }
    }
}

