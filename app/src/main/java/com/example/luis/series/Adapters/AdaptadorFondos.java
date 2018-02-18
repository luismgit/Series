package com.example.luis.series.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.luis.series.R;

public class AdaptadorFondos extends BaseAdapter {

    Context context;
    int [] fondos;
    LayoutInflater inflater;

    public AdaptadorFondos(Context context, int [] fondos) {
        this.context=context;
        this.fondos=fondos;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return fondos.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    //MÉTODO QUE DEVOLVERA CADA VISTA QUE NECESITE NUESTRA LISTA DE FONDOS
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View layout = view;
        FondosViewHolder viewHolder;
        //SI LA VISTA QUE LLEGA EL NULL QUERRÁ DECIR QUE LA VAMOS A CREAR POR PRIMERA VEZ
        if(layout==null){
            //INFLAMOS LA VISTA PARA PASARLA DE XML A JAVA CON INFLATER
            layout=inflater.inflate(R.layout.item_fondo,viewGroup,false);
            //CREAMOS UNA INSTANCIA DE NUESTRA CLASE VIERHOLDER AL QUE LE PASAMOS LA VISTA
            viewHolder=new FondosViewHolder(layout);
            //APLICAMOS A LA VISTA LA "PLANTILLA DE NUESTRO VIEWHOLDER"
            layout.setTag(viewHolder);

            //SI LA VISTA QUE NOS LLEGA ES RECICLADA RECUPERAMOS NUESTRO VIEWHOLDER
        }else{
            viewHolder= (FondosViewHolder) layout.getTag();
        }
        //A TRAVÉS DE NUESTRO VIEWHOLDER APLICAMOS EL AVATAR QUE TOQUE PARA ESA VISTA Y DEVOLVEMOS LA VISTA
        viewHolder.fondo.setImageResource(fondos[i]);
        return layout;

    }
}
