package com.example.luis.series.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.luis.series.Objetos.Series;
import com.example.luis.series.R;

import java.util.List;


public class AdaptadorSeries extends RecyclerView.Adapter<AdaptadorSeries.SeriesViewHolder>{

    List<Series> series;
    private int [] iconos = new int[]
            {       R.drawable.breaking,
                    R.drawable.thrones,
                    R.drawable.theory,
                    R.drawable.narcos,
                    R.drawable.simpson,
                    R.drawable.anarchy,
                    R.drawable.stranger,
                    R.drawable.vikins,
                    R.drawable.mirror,
                    R.drawable.walking,
                    R.drawable.west,
                    R.drawable.lost,
                    R.drawable.cards,
                    R.drawable.dexter};

    public AdaptadorSeries(List<Series> series) {
        this.series = series;
    }

    @Override
    public SeriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_recycler_view_series,parent,false);
        SeriesViewHolder holder = new SeriesViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(SeriesViewHolder holder, int position) {
        Series serie = series.get(position);
        holder.textViewNombre.setText(serie.getNombre());
        holder.iconoSerie.setImageResource(iconos[serie.getImagen()]);
    }

    @Override
    public int getItemCount() {
        return series.size();
    }

    public static class SeriesViewHolder extends RecyclerView.ViewHolder{

        TextView textViewNombre;
        ImageView iconoSerie;

        public SeriesViewHolder(View itemView) {
            super(itemView);
            textViewNombre=itemView.findViewById(R.id.nombreSerie);
            iconoSerie=itemView.findViewById(R.id.imagenSerie);
        }
    }

}
