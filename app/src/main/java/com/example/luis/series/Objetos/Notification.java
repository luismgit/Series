package com.example.luis.series.Objetos;



public class Notification {

    String token;
    String contacto;

    public Notification(String token,String contacto) {
        this.contacto = contacto;
        this.token=token;
    }
    public  Notification(){

    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getContacto() {
        return contacto;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
