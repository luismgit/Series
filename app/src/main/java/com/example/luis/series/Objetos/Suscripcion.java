package com.example.luis.series.Objetos;


public class Suscripcion {

    String idUsuario;
    String serie;
    Float estrellasUsuario;
    String telefono;
    String imagen;
    String tlf_serie;
    String votada;

    public Suscripcion(){

    }

    public Suscripcion(String idUsuario, String serie, Float estrellasUsuario,String telefono,String imagen,String tlf_serie,String votada) {
        this.idUsuario = idUsuario;
        this.serie = serie;
        this.estrellasUsuario = estrellasUsuario;
        this.telefono=telefono;
        this.imagen=imagen;
        this.tlf_serie=tlf_serie;
        this.votada=votada;
    }

    public String getVotada() {
        return votada;
    }

    public void setVotada(String votada) {
        this.votada = votada;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getTlf_serie() {
        return tlf_serie;
    }

    public void setTlf_serie(String tlf_serie) {
        this.tlf_serie = tlf_serie;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
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
