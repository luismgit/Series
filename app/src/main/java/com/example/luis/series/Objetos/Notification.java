package com.example.luis.series.Objetos;



public class Notification {

    String token;
    String avatar;
    String telefono;
    String serie;

    public Notification(String token,String avatar,String telefono,String serie) {
        this.token=token;
        this.avatar=avatar;
        this.telefono=telefono;
        this.serie=serie;
    }
    public  Notification(){

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }
}
