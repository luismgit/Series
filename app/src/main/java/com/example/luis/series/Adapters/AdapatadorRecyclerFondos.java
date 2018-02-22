package com.example.luis.series.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.luis.series.R;
import com.example.luis.series.utilidades.Imagenes;


public class AdapatadorRecyclerFondos extends RecyclerView.Adapter<AdapatadorRecyclerFondos.FondosViewHolderRecycler> implements View.OnClickListener{

    Context contexto;
    int [] fondos=Imagenes.getCambiaFondos();
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
        holder.fondo.setImageResource(fondos[position]);
    }

    @Override
    public int getItemCount() {
        return fondos.length;
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
