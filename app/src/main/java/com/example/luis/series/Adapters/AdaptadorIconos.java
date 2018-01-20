package com.example.luis.series.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.luis.series.R;
import com.example.luis.series.actividades.IconosViewHolder;

public class AdaptadorIconos extends BaseAdapter {

    Context context;
    int [] iconos;
    LayoutInflater inflater;
    public AdaptadorIconos(Context context, int [] iconos) {
        this.context=context;
        this.iconos=iconos;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return iconos.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    //MÉTODO QUE DEVOLVERA CADA VISTA QUE NECESITE NUESTRA LISTA DE AVATARES
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View layout = view;
        IconosViewHolder viewHolder;
        //SI LA VISTA QUE LLEGA EL NULL QUERRÁ DECIR QUE LA VAMOS A CREAR POR PRIMERA VEZ
        if(layout==null){
            //INFLAMOS LA VISTA PARA PASARLA DE XML A JAVA CON INFLATER
            layout=inflater.inflate(R.layout.itemicono,viewGroup,false);
            //CREAMOS UNA INSTANCIA DE NUESTRA CLASE VIERHOLDER AL QUE LE PASAMOS LA VISTA
            viewHolder=new IconosViewHolder(layout);
            //APLICAMOS A LA VISTA LA "PLANTILLA DE NUESTRO VIEWHOLDER"
            layout.setTag(viewHolder);

            //SI LA VISTA QUE NOS LLEGA ES RECICLADA RECUPERAMOS NUESTRO VIEWHOLDER
        }else{
            viewHolder= (IconosViewHolder) layout.getTag();
        }
        //A TRAVÉS DE NUESTRO VIEWHOLDER APLICAMOS EL AVATAR QUE TOQUE PARA ESA VISTA Y DEVOLVEMOS LA VISTA
        viewHolder.icono.setImageResource(iconos[i]);
        return layout;
        /*ImageView icono;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView=inflater.inflate(R.layout.itemicono,viewGroup,false);*/
        //icono = itemView.findViewById(R.id.iconoItem);
        //ImageView icono = layout.findViewById(R.id.iconoItem);
       // icono.setImageResource(iconos[i]);

    }
}
