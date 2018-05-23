package com.maniac.luis.series.Objetos;


import java.io.Serializable;
import java.util.List;

public class Series implements Serializable{

    String nombre;
    String imagen;
    Long likes;
    String web;
    Float estrellas;
    Long numComentarios;
    int idMovieDb;
    String fechaEmision,nombreOriginal,lenguajeOriginal,sinopsis;
    List<String> paises;

    public Series(){

    }
    public Series(String nombre,String imagen,Long likes,String web,Float estrellas,Long numComentarios,int idMovieDb,String fechaEmision,String nombreOriginal,String lenguajeOriginal,String sinopsis,List<String> paises){
        this.nombre=nombre;
        this.imagen=imagen;
        this.likes=likes;
        this.web=web;
        this.estrellas=estrellas;
        this.numComentarios=numComentarios;
        this.idMovieDb=idMovieDb;
        this.fechaEmision=fechaEmision;
        this.nombreOriginal=nombreOriginal;
        this.lenguajeOriginal=lenguajeOriginal;
        this.paises=paises;
        this.sinopsis=sinopsis;
    }

    public int getIdMovieDb() {
        return idMovieDb;
    }

    public void setIdMovieDb(int idMovieDb) {
        this.idMovieDb = idMovieDb;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getNombreOriginal() {
        return nombreOriginal;
    }

    public void setNombreOriginal(String nombreOriginal) {
        this.nombreOriginal = nombreOriginal;
    }

    public String getLenguajeOriginal() {
        return lenguajeOriginal;
    }

    public void setLenguajeOriginal(String lenguajeOriginal) {
        this.lenguajeOriginal = lenguajeOriginal;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public List<String> getPaises() {
        return paises;
    }

    public void setPaises(List<String> paises) {
        this.paises = paises;
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
