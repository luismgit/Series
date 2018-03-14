package com.example.luis.series.Objetos;


public class Comentario {

    private String comentario;
    private String avatarUsuario;
    private String phoneNumberUsuario;
    private String serie;

    public Comentario(){

    }
    public Comentario(String comentario, String avatarUsuario,String serie,String phoneNumberUsuario) {
        this.comentario = comentario;
        this.avatarUsuario = avatarUsuario;
        this.serie=serie;
        this.phoneNumberUsuario=phoneNumberUsuario;
    }


    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getAvatarUsuario() {
        return avatarUsuario;
    }

    public void setAvatarUsuario(String avatarUsuario) {
        this.avatarUsuario = avatarUsuario;
    }

    public String getPhoneNumberUsuario() {
        return phoneNumberUsuario;
    }

    public void setPhoneNumberUsuario(String phoneNumberUsuario) {
        this.phoneNumberUsuario = phoneNumberUsuario;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }
}
