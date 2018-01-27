package com.example.luis.series.Adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.luis.series.Objetos.Usuario;
import com.example.luis.series.R;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.UsuariosviewHolder>{

    List<Usuario> usuarios;
    private int [] iconos = new int[]
            {       R.drawable.breaking,
                    R.drawable.thrones,
                    R.drawable.theory,
                    R.drawable.narcos,
                    R.drawable.simpson,
                    R.drawable.anarchy,
                    R.drawable.stranger,
                    R.drawable.vikins,
                    R.drawable.mirror,
                    R.drawable.walking,
                    R.drawable.west,
                    R.drawable.lost,
                    R.drawable.cards,
                    R.drawable.dexter};
    public Adapter(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public UsuariosviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_recycler_view_contactos,parent,false);
        UsuariosviewHolder holder = new UsuariosviewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(UsuariosviewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);

        //holder.textViewCorreo.setText(usuario.getCorreo());
       // holder.textViewTelefono.setText(usuario.getTelefono());
        holder.textViewNick.setText(usuario.getNick());
        holder.avatar.setImageResource(iconos[usuario.getAvatar()]);
        
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public static  class UsuariosviewHolder extends RecyclerView.ViewHolder{

        TextView textViewNick;
        ImageView avatar;

        public UsuariosviewHolder(View itemView) {
            super(itemView);
            /*textViewCorreo=itemView.findViewById(R.id.textview_correo);
            textViewTelefono=itemView.findViewById(R.id.textview_telefono);*/
            textViewNick=itemView.findViewById(R.id.nombre);
            avatar=itemView.findViewById(R.id.avatarImagen);
        }
    }
}
