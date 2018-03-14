package com.example.luis.series.Adapters;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.luis.series.Objetos.Comentario;
import com.example.luis.series.Objetos.Usuario;
import com.example.luis.series.R;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorComentarios extends RecyclerView.Adapter<AdaptadorComentarios.ComentariosViewHolder>{

    List<Comentario> comentarios=new ArrayList<>();
    Context context;

    public AdaptadorComentarios(List comentarios,Context context){
       this.comentarios=comentarios;
       this.context=context;
    }

    @Override
    public ComentariosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_recycler_view_comentarios,parent,false);
        ComentariosViewHolder holder = new ComentariosViewHolder(view,context,comentarios);
        return holder;
    }

    @Override
    public void onBindViewHolder(ComentariosViewHolder holder, int position) {
        Comentario comentario= comentarios.get(position);
        Glide.with(context)
                .load(comentario.getAvatarUsuario())
                .centerCrop()
                .fitCenter()
                .into(holder.avatarComentario);
        holder.editTextComentario.setText(comentario.getComentario());
        holder.setOnclickListener();
    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }


    public static class ComentariosViewHolder extends RecyclerView.ViewHolder{

        TextView editTextComentario;
        ImageView avatarComentario;
        Context context;
        Dialog miDialogo;
        List<Comentario> comentarios=new ArrayList<>();

        public ComentariosViewHolder(View itemView,Context context,List comentarios) {
            super(itemView);
            this.context=context;
            editTextComentario=itemView.findViewById(R.id.editTextComentario);
            avatarComentario=itemView.findViewById(R.id.avatarComentario);
            this.comentarios=comentarios;
        }
        public void setOnclickListener(){
            avatarComentario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    miDialogo=new Dialog(context);
                    ImageView imagen;
                    miDialogo.setContentView(R.layout.image_pop_up);
                    imagen=miDialogo.findViewById(R.id.imagenAmpliada);
                    int position = getAdapterPosition();
                    Comentario comentario= comentarios.get(position);
                    Glide.with(context)
                            .load(comentario.getAvatarUsuario())
                            .fitCenter()
                            .centerCrop()
                            .into(imagen);
                    miDialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    miDialogo.show();
                }
            });
        }
    }
}
