package com.maniac.luis.series.Objetos;


public class Series {

    String nombre;
    String imagen;
    Long likes;
    String web;
    Float estrellas;
    Long numComentarios;

    public Series(){

    }
    public Series(String nombre,String imagen,Long likes,String web,Float estrellas,Long numComentarios){
        this.nombre=nombre;
        this.imagen=imagen;
        this.likes=likes;
        this.web=web;
        this.estrellas=estrellas;
        this.numComentarios=numComentarios;
    }

    public Long getNumComentarios() {
        return numComentarios;
    }

    public void setNumComentarios(Long numComentarios) {
        this.numComentarios = numComentarios;
    }

    public Long getLikes() {
        return likes;
    }

    public String getWeb() {
        return web;
    }

    public Float getEstrellas() {
        return estrellas;
    }

    public void setEstrellas(Float estrellas) {
        this.estrellas = estrellas;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}