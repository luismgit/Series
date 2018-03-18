package com.example.luis.series.Adapters;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.luis.series.Objetos.Comentario;
import com.example.luis.series.Objetos.Usuario;
import com.example.luis.series.R;
import com.example.luis.series.references.FirebaseReferences;
import com.example.luis.series.utilidades.ComunicarClaveUsuarioActual;
import com.example.luis.series.utilidades.ComunicarCurrentUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        Button botonMegusta;
        String claveUsuarioActual="";

        public ComentariosViewHolder(View itemView,Context context,List comentarios) {
            super(itemView);
            this.context=context;
            editTextComentario=itemView.findViewById(R.id.editTextComentario);
            avatarComentario=itemView.findViewById(R.id.avatarComentario);
            this.comentarios=comentarios;
            botonMegusta=itemView.findViewById(R.id.botonMegusta);
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

            botonMegusta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    Comentario comentario= comentarios.get(position);
                    DatabaseReference dr= FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.USUARIOS_REFERENCE);
                    dr.orderByChild("telefono").equalTo(comentario.getPhoneNumberUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                         Usuario usuario = childSnapshot.getValue(Usuario.class);
                                         Log.i("prueba",usuario.getTelefono());
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                }
            });

        }
    }
}
