package com.example.luis.series.Adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.luis.series.Objetos.Usuario;
import com.example.luis.series.R;
import com.example.luis.series.actividades.InfoContactoActivity;
import com.example.luis.series.utilidades.Common;
import com.example.luis.series.utilidades.Imagenes;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorContactos extends RecyclerView.Adapter<AdaptadorContactos.UsuariosviewHolder>{

    List<Usuario> usuarios=new ArrayList<>();
    private int [] iconos = Imagenes.getAvataresUsuarios();
    Context context;
    public AdaptadorContactos(List usuarios, Context context) {
        this.usuarios = usuarios;
        this.context=context;
    }

    //MÉTODO QUE SE EJECUTA CUANDO NUESTRO RECYCLER NECESITA UN NUEVO UsuariosviewHolder PARA REPRESENTAR UN ELEMENTO
    @Override
    public UsuariosviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_recycler_view_contactos,parent,false);
        UsuariosviewHolder holder = new UsuariosviewHolder(v,context,usuarios);
        return holder;
    }

    //MÉTODO QUE MUESTRA LOS DATOS DE LA POSICION DE LA VARIABLE position, Y MODIFICA EL CONTENIDO DE LA VISTA
    @Override
    public void onBindViewHolder(UsuariosviewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);

        holder.textViewNick.setText(usuario.getNick());
        Log.i("avatar","-> " + usuario.getAvatar());
        Glide.with(context)
                .load(usuario.getAvatar())
                .into(holder.avatar);
        if(usuario.getConectado().equals("online")){
            holder.estado.setTextColor(Color.GREEN);
        }else{
            holder.estado.setTextColor(Color.BLACK);
        }
        holder.estado.setText(usuario.getConectado());

    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public static  class UsuariosviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textViewNick;
        ImageView avatar;
        TextView estado;
        List<Usuario> usuarios = new ArrayList<>();
        Context context;

        public UsuariosviewHolder(View itemView, Context context, List usuarios) {
            super(itemView);
            this.usuarios=usuarios;
            this.context=context;
            itemView.setOnClickListener(this);
            textViewNick=itemView.findViewById(R.id.nombre);
            avatar=itemView.findViewById(R.id.avatarImagen);
            estado=itemView.findViewById(R.id.estado);
        }


        //CUANDO SE PULSE UNA DE LAS VISTAS , LANZAMOS UN INTENT CON VARIOS PARÁMETROS PARA INICIAR LA ACTIVIDAD QUE MUESTRA LOS FAVORITOS DE CADA USUARIO
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Usuario usuario = this.usuarios.get(position);
            Intent intent=new Intent(context,InfoContactoActivity.class);
            intent.putExtra(Common.CONTACTO,usuario.getNick());
            intent.putExtra(Common.AVATAR,usuario.getAvatar());
            intent.putExtra(Common.TELEFONO,usuario.getTelefono());
            this.context.startActivity(intent);
        }
    }
}
