package com.maniac.luis.series.Adapters;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.maniac.luis.series.Objetos.Usuario;
import com.maniac.luis.series.R;
import com.maniac.luis.series.actividades.InfoContactoActivity;
import com.maniac.luis.series.references.FirebaseReferences;
import com.maniac.luis.series.utilidades.Common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdaptadorContactos extends RecyclerView.Adapter<AdaptadorContactos.UsuariosviewHolder>{

    List<Usuario> usuarios=new ArrayList<>();
    Context context;
    ShowcaseView showcaseView;
    boolean primeraVez=true;
    boolean isShowedToturial;
    ViewPager vp;
    SharedPreferences sharedPref;

    public AdaptadorContactos(List usuarios, Context context, ViewPager vp) {
        this.usuarios = usuarios;
        this.context=context;
        this.vp=vp;
    }

    //MÉTODO QUE SE EJECUTA CUANDO NUESTRO RECYCLER NECESITA UN NUEVO UsuariosviewHolder PARA REPRESENTAR UN ELEMENTO
    @Override
    public UsuariosviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_recycler_view_contactos,parent,false);
        UsuariosviewHolder holder = new UsuariosviewHolder(v,context,usuarios,this);
        return holder;
    }

    public List<Usuario> getUsuarios(){
        return this.usuarios;
    }




    //MÉTODO QUE MUESTRA LOS DATOS DE LA POSICION DE LA VARIABLE position, Y MODIFICA EL CONTENIDO DE LA VISTA
    @Override
    public void onBindViewHolder(UsuariosviewHolder holder, int position) {

        Usuario usuario = usuarios.get(position);
        if(usuario.getNick().length()>17){
            holder.textViewNick.setTextSize(14);
        }else{
            holder.textViewNick.setTextSize(17);
        }
        holder.textViewNick.setText(usuario.getNick());
        Glide.with(context)
                .load(usuario.getAvatar())
                .into(holder.avatar);
        if(usuario.getConectado().equals(FirebaseReferences.ONLINE)){
            holder.estado.setTextColor(Color.GREEN);
            holder.estado.setText(R.string.en_linea);
        }else{
            holder.estado.setTextColor(Color.BLACK);
            holder.estado.setText(getMensajeUltimaConexion(usuario.getConectado()));
        }
        switch (usuario.getNivel()){
            case Common.PRINCIPIANTE:
                holder.medalla.setImageResource(R.drawable.bronce_mod);
                break;
            case Common.INTERMEDIO:
                holder.medalla.setImageResource(R.drawable.plata_mod);
                break;
            case Common.AVANZADO:
                holder.medalla.setImageResource(R.drawable.oro_mod);
                break;
        }
        holder.setOnclickListener();
    }



    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public static  class UsuariosviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textViewNick;
        ImageView avatar;
        TextView estado;
        ImageView medalla;
        List<Usuario> usuarios = new ArrayList<>();
        Context context;
        Dialog miDialogo;
        RelativeLayout vista;
        AdaptadorContactos adaptadorContactos;

        public UsuariosviewHolder(View itemView, Context context, List usuarios,AdaptadorContactos adaptadorContactos) {
            super(itemView);
            this.usuarios=usuarios;
            this.context=context;
            itemView.setOnClickListener(this);
            textViewNick=itemView.findViewById(R.id.nombre);
            avatar=itemView.findViewById(R.id.avatarImagen);
            estado=itemView.findViewById(R.id.estado);
            medalla=itemView.findViewById(R.id.imagenMedalla);
            vista=itemView.findViewById(R.id.relativeLayoutFavoritos);
            this.adaptadorContactos=adaptadorContactos;
        }



        public void setOnclickListener(){
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    miDialogo=new Dialog(context);
                    ImageView imagen;
                    miDialogo.setContentView(R.layout.image_pop_up);
                    imagen=miDialogo.findViewById(R.id.imagenAmpliada);
                    int position = getAdapterPosition();
                    Usuario usuario = adaptadorContactos.getUsuarios().get(position);
                    Glide.with(context)
                            .load(usuario.getAvatar())
                            .fitCenter()
                            .centerCrop()
                            .into(imagen);
                    miDialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    miDialogo.show();
                }
            });
        }




        //CUANDO SE PULSE UNA DE LAS VISTAS , LANZAMOS UN INTENT CON VARIOS PARÁMETROS PARA INICIAR LA ACTIVIDAD QUE MUESTRA LOS FAVORITOS DE CADA USUARIO
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Usuario usuario = adaptadorContactos.getUsuarios().get(position);
            Intent intent=new Intent(context,InfoContactoActivity.class);
            intent.putExtra(Common.CONTACTO,usuario.getNick());
            intent.putExtra(Common.AVATAR,usuario.getAvatar());
            intent.putExtra(Common.TELEFONO,usuario.getTelefono());
            this.context.startActivity(intent);
        }
    }


    private String getMensajeUltimaConexion(String fechaConexion) {
        String fechaActual=getFecha();
        String fechaUltimaConexion=fechaConexion;
        if(fechaActual.substring(0,4).equals(fechaUltimaConexion.substring(0,4))){
            return context.getString(R.string.ultima_conex_hoy) + " " + fechaUltimaConexion.substring(6,fechaUltimaConexion.length());
        }else{
            return context.getString(R.string.ultima_conex) + " " + fechaUltimaConexion;
        }
    }

    private String getFecha(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf= new SimpleDateFormat("dd/MM HH:mm");
        return sdf.format(date);
    }

    public void setFilter(List<Usuario> listaUsuarios){
        this.usuarios=new ArrayList<>();
        this.usuarios.addAll(listaUsuarios);
        notifyDataSetChanged();
    }


}
