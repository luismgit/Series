package com.maniac.luis.series;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maniac.luis.series.utilidades.ImagenesColoresSolidos;


public class AdaptadorColorSolido extends RecyclerView.Adapter<AdaptadorColorSolido.ColorSolidoViewHolderRecycler> implements View.OnClickListener{

    Context context;
    String [] colores = ImagenesColoresSolidos.getListaColores();
    private View.OnClickListener listener;

    public AdaptadorColorSolido(Context context){
       this.context=context;
    }

    @Override
    public ColorSolidoViewHolderRecycler onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista= LayoutInflater.from(context).inflate(R.layout.item_fondo_color_solido,null);
        vista.setOnClickListener(this);
        return new ColorSolidoViewHolderRecycler(vista);
    }


    @Override
    public void onBindViewHolder(ColorSolidoViewHolderRecycler holder, int position) {
        holder.fondo.setBackgroundColor(Color.parseColor(colores[position]));
    }

    @Override
    public int getItemCount() {
        return colores.length;
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


    public class ColorSolidoViewHolderRecycler extends RecyclerView.ViewHolder {

        ImageView fondo;
        public ColorSolidoViewHolderRecycler(View itemView) {
            super(itemView);
            fondo=itemView.findViewById(R.id.colorSolido);
        }
    }
}
