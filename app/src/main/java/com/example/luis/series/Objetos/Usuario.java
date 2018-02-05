package com.example.luis.series.Objetos;


public class Usuario {
    int avatar;
    String nick;
    String correo;
    String telefono;
    String conectado;
    //String ultimaconexion;

    public Usuario() {
    }

    public Usuario(String nick,String telefono,String correo,int avatar,String conectado) {
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

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
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
