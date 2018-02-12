package com.example.luis.series.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.luis.series.Objetos.Suscripcion;
import com.example.luis.series.R;
import com.example.luis.series.utilidades.Imagenes;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorInfoContactos extends RecyclerView.Adapter<AdaptadorInfoContactos.ContactosViewHolder>{

    List<Suscripcion> suscripcionesContactos;
    private int [] iconos = Imagenes.getIconosSeries();

    public AdaptadorInfoContactos(List<Suscripcion> suscripcionesContactos){
        this.suscripcionesContactos=suscripcionesContactos;
    }
    @Override
    public ContactosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_recycler_view_infocontactos,parent,false);
        ContactosViewHolder holder = new ContactosViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ContactosViewHolder holder, int position) {
        Suscripcion suscripcion = suscripcionesContactos.get(position);
        holder.nombreSerie.setText(suscripcion.getSerie());
        holder.avatarSerie.setImageResource(iconos[suscripcion.getImagen()]);
        //AL TEXTVIEW NOTA LE APLICAMOS LA NOTA QUE EL USUARIO ELEGIDO LE HA PUESTO SOBRE 10
        float notaSobreDiez=(suscripcion.getEstrellasUsuario()*10)/5;
        String nota=String.valueOf(notaSobreDiez);
        if(nota.equals("10.0")){
            nota=nota.substring(0,2);
        }
        holder.textViewNota.setText(nota);

        //AL PROGRESS CIRCULAR LE APLICAMOS LA NOTA DE LA SUSCRIPCIÓN
        holder.barNota.setProgress(Math.round(suscripcion.getEstrellasUsuario()));
    }

    @Override
    public int getItemCount() {
        return suscripcionesContactos.size();
    }

    public static class ContactosViewHolder extends RecyclerView.ViewHolder{

        ImageView avatarSerie;
        TextView nombreSerie;
        ProgressBar barNota;
        TextView textViewNota;

        public ContactosViewHolder(View itemView) {
            super(itemView);
            avatarSerie=itemView.findViewById(R.id.imagenSerieContactosInfo);
            nombreSerie=itemView.findViewById(R.id.nombreSerieFavoritos);
            barNota=itemView.findViewById(R.id.progressBarNota);
            textViewNota=itemView.findViewById(R.id.textViewNota);
        }
    }
}
