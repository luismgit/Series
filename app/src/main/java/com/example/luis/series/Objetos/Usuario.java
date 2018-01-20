package com.example.luis.series.Objetos;


public class Usuario {
    String nick;
    String correo;
    String telefono;

    public Usuario() {
    }

    public Usuario(String nick,String telefono,String correo) {
        this.correo = correo;
        this.telefono=telefono;
        this.nick=nick;
    }

    public String getCorreo() {
        return correo;
    }

    public String getNick() {
        return nick;
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
