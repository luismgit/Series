package com.example.luis.series.Objetos;


public class Series {

    String nombre;
    int imagen;
    Long likes;
    String web;

    public Series(){

    }
    public Series(String nombre,int imagen,Long likes,String web){
        this.nombre=nombre;
        this.imagen=imagen;
        this.likes=likes;
        this.web=web;
    }

    public Long getLikes() {
        return likes;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
