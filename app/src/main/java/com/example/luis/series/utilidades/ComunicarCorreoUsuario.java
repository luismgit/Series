package com.example.luis.series.utilidades;



public class ComunicarCorreoUsuario {
    private static String correoUsuario;

    public static String getCorreoUsuario() {
        return correoUsuario;
    }

    public static void setCorreoUsuario(String correoUsuario) {
        ComunicarCorreoUsuario.correoUsuario = correoUsuario;
    }
}
