package com.maniac.luis.series.utilidades;

/**
 * Created by Luis on 01/04/2018.
 */

public class ImagenesColoresSolidos {

    private static String [] listaColores=new String[]{
            "#ca1313",
            "#2fa335",
            "#0cb1f2",
            "#0773e7",
            "#09fde1",
            "#f9e487",
            "#efe528",
            "#ed9315",
            "#c5f5dc",
            "#686c6b",
            "#d1d4d3",
            "#8456f1",
            "#c9b7f2",
            "#ffffff"
    };

    public static String[] getListaColores() {
        return listaColores;
    }

    public static void setListaColores(String[] listaColores) {
        ImagenesColoresSolidos.listaColores = listaColores;
    }
}
