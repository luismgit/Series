package com.maniac.luis.series.Adapters;

import android.content.Context;
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
import com.maniac.luis.series.R;
import com.maniac.luis.series.utilidades.Imagenes;

import java.util.List;


public class AdapatadorRecyclerFondos extends RecyclerView.Adapter<AdapatadorRecyclerFondos.FondosViewHolderRecycler> implements View.OnClickListener{

    Context contexto;
    //int [] fondos=Imagenes.getCambiaFondos();
    List<String> listaFondos = Imagenes.getListaFondos();

    private View.OnClickListener listener;


    public AdapatadorRecyclerFondos(Context contexto) {
        this.contexto = contexto;
    }

    @Override
    public FondosViewHolderRecycler onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(contexto).inflate(R.layout.item_fondo,null);
        vista.setOnClickListener(this);
        return new FondosViewHolderRecycler(vista);
    }

    @Override
    public void onBindViewHolder(final FondosViewHolderRecycler holder, int position) {

        Glide.with(contexto)
                .load(listaFondos.get(position))
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        holder.carga.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.carga.setVisibility(View.GONE);
                        return false;
                    }
                })
                .centerCrop()
                .fitCenter()
                .into(holder.fondo);
    }

    @Override
    public int getItemCount() {
        return listaFondos.size();
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

    public class FondosViewHolderRecycler extends RecyclerView.ViewHolder {

        ImageView fondo;
        ProgressBar carga;
        public FondosViewHolderRecycler(View itemView) {
            super(itemView);
            fondo=itemView.findViewById(R.id.fondoImagenSerie);
            carga=itemView.findViewById(R.id.carga);
        }


    }
}
