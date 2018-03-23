package com.maniac.luis.series.utilidades;



public class ComunicarClaveUsuarioActual {
    private static String  claveUsuario=null;

    public static  void setClave(String claveUsu){
        claveUsuario=claveUsu;
    }

    public  static String getClave(){
        return claveUsuario;
    }
}
