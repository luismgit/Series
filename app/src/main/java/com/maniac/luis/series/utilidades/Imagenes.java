package com.maniac.luis.series.utilidades;


import java.util.ArrayList;
import java.util.List;

public class Imagenes {





    private static List<String> listaFondos =new ArrayList<>();


    public static List<String> getListaFondos() {
        return listaFondos;
    }

    public static void setListaFondos(List<String> listaFondos) {
        Imagenes.listaFondos = listaFondos;
    }



}
