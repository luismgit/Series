package com.example.luis.series.Adapters;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.luis.series.R;
import com.example.luis.series.actividades.TabActivity;
import com.example.luis.series.utilidades.Imagenes;

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
    public void onBindViewHolder(FondosViewHolderRecycler holder, int position) {

        Glide.with(contexto)
                .load(listaFondos.get(position))
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
        public FondosViewHolderRecycler(View itemView) {
            super(itemView);
            fondo=itemView.findViewById(R.id.fondoImagenSerie);
        }


    }
}
