package com.example.luis.series.Adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.luis.series.Objetos.Usuario;
import com.example.luis.series.R;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.UsuariosviewHolder>{

    List<Usuario> usuarios;

    public Adapter(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public UsuariosviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_recycler_view,parent,false);
        UsuariosviewHolder holder = new UsuariosviewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(UsuariosviewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);

        holder.textViewCorreo.setText(usuario.getCorreo());
        holder.textViewTelefono.setText(usuario.getTelefono());
        holder.textViewNick.setText(usuario.getNick());
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public static  class UsuariosviewHolder extends RecyclerView.ViewHolder{

        TextView textViewCorreo,textViewTelefono,textViewNick;


        public UsuariosviewHolder(View itemView) {
            super(itemView);
            textViewCorreo=itemView.findViewById(R.id.textview_correo);
            textViewTelefono=itemView.findViewById(R.id.textview_telefono);
            textViewNick=itemView.findViewById(R.id.textview_nick);
        }
    }
}
