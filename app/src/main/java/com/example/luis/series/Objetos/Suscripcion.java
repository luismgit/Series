package com.example.luis.series.Objetos;


public class Suscripcion {

    String idUsuario;
    String serie;
    Float estrellasUsuario;
    String telefono;
    int imagen;

    public Suscripcion(){

    }

    public Suscripcion(String idUsuario, String serie, Float estrellasUsuario,String telefono,int imagen) {
        this.idUsuario = idUsuario;
        this.serie = serie;
        this.estrellasUsuario = estrellasUsuario;
        this.telefono=telefono;
        this.imagen=imagen;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Float getEstrellasUsuario() {
        return estrellasUsuario;
    }

    public void setEstrellasUsuario(Float estrellasUsuario) {
        this.estrellasUsuario = estrellasUsuario;
    }
}
