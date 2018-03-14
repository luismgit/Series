package com.example.luis.series.utilidades;



public class ComunicarAvatarUsuario {

    private static String avatarUsuario;

    public static String getAvatarUsuario() {
        return avatarUsuario;
    }

    public static void setAvatarUsuario(String avatarUsuario) {
        ComunicarAvatarUsuario.avatarUsuario = avatarUsuario;
    }
}
