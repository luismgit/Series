package com.example.luis.series.Objetos;


import java.io.Serializable;

public class Usuario implements Serializable{
    String  avatar;
    String nick;
    String correo;
    String telefono;
    String conectado;
   // String ultimaconexion;

    public Usuario() {
    }

    public Usuario(String nick,String telefono,String correo,String avatar,String conectado) {
        this.correo = correo;
        this.telefono=telefono;
        this.nick=nick;
        this.avatar=avatar;
        this.conectado=conectado;
        //this.ultimaconexion=ultimaconexion;

    }

    public String getConectado() {
        return conectado;
    }

    public void setConectado(String conectado) {
        this.conectado = conectado;
    }

 /*   public String getUltimaconexion() {
        return ultimaconexion;
    }

    public void setUltimaconexion(String ultimaconexion) {
        this.ultimaconexion = ultimaconexion;
    }*/



    public String getCorreo() {
        return correo;
    }

    public String getNick() {
        return nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
