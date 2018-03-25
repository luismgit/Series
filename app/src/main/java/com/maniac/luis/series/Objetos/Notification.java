package com.maniac.luis.series.Objetos;



public class Notification {

    String token;
    String avatar;
    String telefono;
    String serie;
    String telefono_usuario_final;
    String comentario;

    public Notification(String token,String avatar,String telefono,String serie,String telefono_usuario_final,String comentario) {
        this.token=token;
        this.avatar=avatar;
        this.telefono=telefono;
        this.serie=serie;
        this.telefono_usuario_final=telefono_usuario_final;
        this.comentario=comentario;
    }
    public  Notification(){

    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
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

    public String getTelefono_usuario_final() {
        return telefono_usuario_final;
    }

    public void setTelefono_usuario_final(String telefono_usuario_final) {
        this.telefono_usuario_final = telefono_usuario_final;
    }
}
