package com.maniac.luis.series.Adapters;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maniac.luis.series.Objetos.Comentario;
import com.maniac.luis.series.Objetos.Notification;
import com.maniac.luis.series.Objetos.Usuario;
import com.maniac.luis.series.R;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.maniac.luis.series.references.FirebaseReferences;
import com.maniac.luis.series.utilidades.ComunicarAvatarUsuario;
import com.maniac.luis.series.utilidades.ComunicarCurrentUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdaptadorComentarios extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int NO_USER = 1;
    private static final int USER = 2;
    List<Comentario> comentarios=new ArrayList<>();
    Context context;
    Map<String,String> agenda;

    public AdaptadorComentarios(List comentarios, Context context, Map agenda){
       this.comentarios=comentarios;
       this.context=context;
       this.agenda=agenda;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == NO_USER){
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_recycler_view_comentarios,parent,false);
            return new ComentariosViewHolder(view,context,comentarios);
        }else{
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_recycler_view_comentarios_dos,parent,false);
            return new ComentariosViewHolderUser(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case NO_USER:
                initLayoutNoUser((ComentariosViewHolder) holder,position);
                break;
            case USER:
                iniLayoutUser((ComentariosViewHolderUser)holder,position);
                break;
        }
    }


    private void initLayoutNoUser(ComentariosViewHolder holder, int position) {
        Comentario comentario= comentarios.get(position);
        String contacto = agenda.get(comentario.getPhoneNumberUsuario());
        Glide.with(context)
                .load(comentario.getAvatarUsuario())
                .centerCrop()
                .fitCenter()
                .into(holder.avatarComentario);
       // String texto=contacto + ": " + comentario.getComentario();
        holder.textUserName.setText(contacto);
        //Spannable spannable = new SpannableString(texto);
       // spannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0,texto.length()-comentario.getComentario().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //holder.editTextComentario.setText(spannable,TextView.BufferType.SPANNABLE);
        holder.editTextComentario.setText(comentario.getComentario());
        holder.setOnclickListener();
    }

    private void iniLayoutUser(ComentariosViewHolderUser holder, int position) {
        Comentario comentario= comentarios.get(position);
        /*String texto="Yo: " + comentario.getComentario();
        Spannable spannable = new SpannableString(texto);
        spannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0,texto.length()-comentario.getComentario().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.editTextComentario.setText(spannable,TextView.BufferType.SPANNABLE);*/
        holder.editTextComentario.setText(comentario.getComentario());
    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }

    @Override
    public int getItemViewType(int position) {
        Comentario com=comentarios.get(position);
        if(com.getTipo() == Comentario.ComentarioType.OTHER_USERS){
            return NO_USER;
        }else{
            return USER;
        }
    }

    public static class ComentariosViewHolderUser extends RecyclerView.ViewHolder{

        TextView editTextComentario;
        public ComentariosViewHolderUser(View itemView) {
            super(itemView);
            editTextComentario=itemView.findViewById(R.id.editTextComentario);
        }
    }

    public static class ComentariosViewHolder extends RecyclerView.ViewHolder{

        TextView editTextComentario;
        ImageView avatarComentario;
        Context context;
        Dialog miDialogo;
        List<Comentario> comentarios=new ArrayList<>();
        LikeButton botonMegusta;
        TextView textUserName;
        String claveUsuarioActual="";

        public ComentariosViewHolder(View itemView,Context context,List comentarios) {
            super(itemView);
            this.context=context;
            editTextComentario=itemView.findViewById(R.id.editTextComentario);
            textUserName=itemView.findViewById(R.id.text_message_name);
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


           /* botonMegusta.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    Log.i("like","true");
                    likeButton.setLiked(true);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    likeButton.setLiked(false);
                }
            });*/


            botonMegusta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final Comentario comentario= comentarios.get(position);
                    DatabaseReference dr= FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.USUARIOS_REFERENCE);
                    dr.orderByChild("telefono").equalTo(comentario.getPhoneNumberUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                Usuario usuario = childSnapshot.getValue(Usuario.class);
                                Log.i("prueba",usuario.getTelefono());
                                if(!usuario.getTelefono().equals(ComunicarCurrentUser.getPhoneNumberUser())){
                                    Notification not = new Notification(usuario.getToken(), ComunicarAvatarUsuario.getAvatarUsuario(), ComunicarCurrentUser.getPhoneNumberUser(),comentario.getSerie(),usuario.getTelefono());
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("notifications");
                                    ref.push().setValue(not);
                                }

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
