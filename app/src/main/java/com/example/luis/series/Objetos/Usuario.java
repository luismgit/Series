package com.example.luis.series.Objetos;


public class Usuario {
    int avatar;
    String nick;
    String correo;
    String telefono;

    public Usuario() {
    }

    public Usuario(String nick,String telefono,String correo,int avatar) {
        this.correo = correo;
        this.telefono=telefono;
        this.nick=nick;
        this.avatar=avatar;
    }

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
