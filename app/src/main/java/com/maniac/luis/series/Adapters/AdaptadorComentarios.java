package com.maniac.luis.series.Adapters;


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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.maniac.luis.series.Objetos.Comentario;
import com.maniac.luis.series.Objetos.Notification;
import com.maniac.luis.series.Objetos.Usuario;
import com.maniac.luis.series.R;
import com.maniac.luis.series.references.FirebaseReferences;
import com.maniac.luis.series.utilidades.ComunicarAvatarUsuario;
import com.maniac.luis.series.utilidades.ComunicarCurrentUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        holder.textUserName.setText(contacto);
        if(comentario.getNumLikes()!=0){
            holder.numeroMeGustan.setText("("+comentario.getNumLikes()+")");
        }else{
            holder.numeroMeGustan.setText("");
        }
        holder.fechaComentario.setText(getFechaComentarioFormateada(comentario.getFecha()));

        holder.editTextComentario.setText(comentario.getComentario());
        Map<String,Boolean> liked = comentario.getLiked();
        Boolean isLiked=liked.get(ComunicarCurrentUser.getPhoneNumberUser());

        holder.botonMegusta.setLiked(isLiked);
        holder.setOnclickListener();
    }

    private void iniLayoutUser(ComentariosViewHolderUser holder, int position) {
        Comentario comentario= comentarios.get(position);
        if(comentario.getNumLikes()!=0){
            holder.numMeGustan.setText("("+comentario.getNumLikes()+")");
        }else{
            holder.numMeGustan.setText("");
        }
        holder.editTextComentario.setText(comentario.getComentario());
        holder.fechaComentUsu.setText(getFechaComentarioFormateada(comentario.getFecha()));
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
        TextView numMeGustan;
        TextView fechaComentUsu;
        public ComentariosViewHolderUser(View itemView) {
            super(itemView);
            editTextComentario=itemView.findViewById(R.id.editTextComentario);
            numMeGustan=itemView.findViewById(R.id.numerodeMegusta);
            fechaComentUsu=itemView.findViewById(R.id.fechaComentarioUsu);
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
        TextView numeroMeGustan;
        TextView fechaComentario;

        public ComentariosViewHolder(View itemView,Context context,List comentarios) {
            super(itemView);
            this.context=context;
            editTextComentario=itemView.findViewById(R.id.editTextComentario);
            textUserName=itemView.findViewById(R.id.text_message_name);
            avatarComentario=itemView.findViewById(R.id.avatarComentario);
            this.comentarios=comentarios;
            botonMegusta=itemView.findViewById(R.id.botonMegusta);
            numeroMeGustan=itemView.findViewById(R.id.numeroMegusta);
            fechaComentario=itemView.findViewById(R.id.fechaComentario);

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


            botonMegusta.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    final Comentario comentario=comentarios.get(getAdapterPosition());
                    DatabaseReference re=FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS)
                            .child(comentario.getKeyFB()).child("liked").child(ComunicarCurrentUser.getPhoneNumberUser());
                    re.setValue(true);
                    DatabaseReference dr= FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.USUARIOS_REFERENCE);
                    dr.orderByChild(FirebaseReferences.TELEFONO).equalTo(comentario.getPhoneNumberUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                Usuario usuario = childSnapshot.getValue(Usuario.class);
                                if(!usuario.getTelefono().equals(ComunicarCurrentUser.getPhoneNumberUser())){
                                    Notification not = new Notification(usuario.getToken(), ComunicarAvatarUsuario.getAvatarUsuario(), ComunicarCurrentUser.getPhoneNumberUser()
                                            ,comentario.getSerie(),usuario.getTelefono(),comentario.getComentario());
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.NOTIFICATIONS);
                                    ref.push().setValue(not);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    final DatabaseReference refD=FirebaseDatabase.getInstance().getReference(FirebaseReferences.COMENTARIOS).child(comentario.getKeyFB())
                            .child(FirebaseReferences.NUM_LIKES);
                    refD.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Long num= (Long) dataSnapshot.getValue();
                            num++;
                            refD.setValue(num);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    Comentario comentario=comentarios.get(getAdapterPosition());
                    DatabaseReference re=FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS)
                            .child(comentario.getKeyFB()).child(FirebaseReferences.LIKED).child(ComunicarCurrentUser.getPhoneNumberUser());
                    re.setValue(false);
                    final DatabaseReference refD=FirebaseDatabase.getInstance().getReference(FirebaseReferences.COMENTARIOS).child(comentario.getKeyFB())
                            .child(FirebaseReferences.NUM_LIKES);
                    refD.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Long num= (Long) dataSnapshot.getValue();
                            num--;
                            refD.setValue(num);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });

        }
    }

    private String getFechaComentarioFormateada(String fechaConexion) {
        String fechaActual=getFecha();
        String fechaUltimaConexion=fechaConexion;
        if(fechaActual.substring(0,4).equals(fechaUltimaConexion.substring(0,4))){
            return context.getString(R.string.hoy_a_las) + " " + fechaUltimaConexion.substring(6,fechaUltimaConexion.length());
        }else{
            return fechaUltimaConexion;
        }
    }

    private String getFecha(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf= new SimpleDateFormat("dd/MM HH:mm");
        return sdf.format(date);
    }



}
