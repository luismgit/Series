package com.example.luis.series.Objetos;

/**
 * Created by Luis on 27/01/2018.
 */

public class Series {

    String nombre;
    int imagen;

    public Series(){

    }
    public Series(String nombre,int imagen){
        this.nombre=nombre;
        this.imagen=imagen;
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
