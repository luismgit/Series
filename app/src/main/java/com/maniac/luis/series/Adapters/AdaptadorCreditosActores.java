package com.maniac.luis.series.Adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.maniac.luis.series.MovieDbInterface.ApiInterfaceDetailsSerie;
import com.maniac.luis.series.MovieDbInterface.SeriesCreditosActorResult;
import com.maniac.luis.series.MovieDbInterface.SeriesDetailsResult;
import com.maniac.luis.series.Objetos.Series;
import com.maniac.luis.series.R;
import com.maniac.luis.series.actividades.InfoSeriesActivity;
import com.maniac.luis.series.utilidades.Common;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_recycler_papel_actor,parent,false);
        AdaptadorCreditosActores.CreditosViewHolder holder = new AdaptadorCreditosActores.CreditosViewHolder(view,context,this,papeles);
        return holder;
    }

    @Override
    public void onBindViewHolder(CreditosViewHolder holder, int position) {
        SeriesCreditosActorResult.CastBean papel = papeles.get(position);
        String imagenSeriePapel=papel.getPoster_path();
        if(imagenSeriePapel==null || imagenSeriePapel.equals("")){

            Glide.with(context)
                    .load(R.drawable.series_back_256_256)
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
        List<SeriesCreditosActorResult.CastBean> papeles;

        public CreditosViewHolder(View itemView,Context context,AdaptadorCreditosActores adaptadorCreditosActores,List papeles) {
            super(itemView);
            imagenSerie=itemView.findViewById(R.id.imagenSerieCredito);
            tituloSerie=itemView.findViewById(R.id.nombreSerieCredito);
            this.adaptadorCreditosActores=adaptadorCreditosActores;
            this.papeles=papeles;
            this.context=context;
        }

        public void setOnclickListener(){
            imagenSerie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    irASerieDetalles(papeles.get(getAdapterPosition()).getId());
                }
            });
            tituloSerie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    irASerieDetalles(papeles.get(getAdapterPosition()).getId());
                }
            });


        }

        public void irASerieDetalles(int idSerie){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Common.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiInterfaceDetailsSerie interfaceDetailsSerie = retrofit.create(ApiInterfaceDetailsSerie.class);
            Call<SeriesDetailsResult> call2 = interfaceDetailsSerie.listOfSeriesDetails(idSerie,Common.API_KEY_MOVIE_DB,"es");
            call2.enqueue(new Callback<SeriesDetailsResult>() {
                @Override
                public void onResponse(Call<SeriesDetailsResult> call, Response<SeriesDetailsResult> response) {
                    SeriesDetailsResult result = response.body();
                     Long likes= Long.valueOf(0);
                     Float estrellas= Float.valueOf(0);
                     Long numComentarios= Long.valueOf(0);
                    if(result!=null){
                        String web="https://www.themoviedb.org/tv/" + result.getId();
                        Series serie = new Series(result.getName(),result.getPoster_path(),likes,web,estrellas,numComentarios,result.getId(),result.getFirst_air_date(),
                        result.getOriginal_name(),result.getOriginal_language(),result.getOverview(),result.getOrigin_country());
                        Intent intent=new Intent(context,InfoSeriesActivity.class);
                        intent.putExtra(Common.SERIE_OBJETO,serie);
                        context.startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<SeriesDetailsResult> call, Throwable t) {

                }
            });
        }
    }
}
