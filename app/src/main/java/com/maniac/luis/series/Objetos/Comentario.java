package com.maniac.luis.series.Objetos;


import java.util.HashMap;
import java.util.Map;

public class Comentario {

    private String comentario;
    private String avatarUsuario;
    private String phoneNumberUsuario;
    private String serie;
    private Map<String,Boolean> liked=new HashMap<>();
    private ComentarioType tipo;

    public enum ComentarioType {
            USER_PROP,OTHER_USERS;
    }

    public Comentario(){

    }
    public Comentario(String comentario, String avatarUsuario,String serie,String phoneNumberUsuario,Map<String,Boolean> liked,ComentarioType type) {
        this.comentario = comentario;
        this.avatarUsuario = avatarUsuario;
        this.serie=serie;
        this.phoneNumberUsuario=phoneNumberUsuario;
        this.liked=liked;
        this.tipo=tipo;
    }

    public ComentarioType getTipo() {
        return tipo;
    }

    public void setTipo(ComentarioType tipo) {
        this.tipo = tipo;
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

    public Map<String, Boolean> getLiked() {
        return liked;
    }

    public void setLiked(Map<String, Boolean> liked) {
        this.liked = liked;
    }
    public  void addNumberToLiked(String number){
        liked.put(number,false);
    }
}