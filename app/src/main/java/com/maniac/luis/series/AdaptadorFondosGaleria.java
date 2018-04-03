package com.maniac.luis.series;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.maniac.luis.series.Adapters.AdaptadorColorSolido;
import com.maniac.luis.series.utilidades.FondosGaleriaComentarios;

import java.util.List;

public class AdaptadorFondosGaleria extends RecyclerView.Adapter<AdaptadorFondosGaleria.FondosGaleriaViewHolderRecycler> implements View.OnClickListener{

    Context context;
    List<String> listaFondosComentarios = FondosGaleriaComentarios.getFondos();
    private View.OnClickListener listener;

    public  AdaptadorFondosGaleria(Context context){
        this.context=context;
    }

    @Override
    public FondosGaleriaViewHolderRecycler onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista= LayoutInflater.from(context).inflate(R.layout.item_fondo_galeria,null);
        vista.setOnClickListener(this);
        return new AdaptadorFondosGaleria.FondosGaleriaViewHolderRecycler(vista);
    }

    @Override
    public void onBindViewHolder(final FondosGaleriaViewHolderRecycler holder, int position) {

        Glide.with(context)
                .load(listaFondosComentarios.get(position))
                .error(R.drawable.sin_conexion)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        holder.fondo.setImageResource(R.drawable.sin_conexion);
                        holder.cargaFondos.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.cargaFondos.setVisibility(View.GONE);
                        return false;
                    }
                })
                .centerCrop()
                .fitCenter()
                .into(holder.fondo);
    }

    @Override
    public int getItemCount() {
        return listaFondosComentarios.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener != null){
            listener.onClick(view);
        }
    }

    public class FondosGaleriaViewHolderRecycler extends RecyclerView.ViewHolder {

        ImageView fondo;
        ProgressBar cargaFondos;
        public FondosGaleriaViewHolderRecycler(View itemView) {
            super(itemView);
            fondo=itemView.findViewById(R.id.fondoGaleria);
            cargaFondos=itemView.findViewById(R.id.cargaFondos);
        }
    }
}
