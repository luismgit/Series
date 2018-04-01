package com.maniac.luis.series.Objetos;


import java.io.Serializable;

public class Usuario implements Serializable{
    String  avatar;
    String nick;
    String correo;
    String telefono;
    String conectado;
    String nivel;
    Long numSuscripciones;
    String token;
    String fondoComentario;



    public Usuario() {
    }

    public Usuario(String nick,String telefono,String correo,String avatar,String conectado,String nivel,Long numSuscripciones,String token,String fondoComentario) {
        this.correo = correo;
        this.telefono=telefono;
        this.nick=nick;
        this.avatar=avatar;
        this.conectado=conectado;
        this.nivel=nivel;
        this.numSuscripciones=numSuscripciones;
        this.token=token;
        this.fondoComentario=fondoComentario;

    }

    public String getConectado() {
        return conectado;
    }

    public void setConectado(String conectado) {
        this.conectado = conectado;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getNumSuscripciones() {
        return numSuscripciones;
    }

    public void setNumSuscripciones(Long numSuscripciones) {
        this.numSuscripciones = numSuscripciones;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

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
