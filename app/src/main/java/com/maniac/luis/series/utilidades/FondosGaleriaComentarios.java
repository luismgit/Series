package com.maniac.luis.series.utilidades;

import java.util.ArrayList;
import java.util.List;



public class FondosGaleriaComentarios {

    private static List<String> fondos = new ArrayList<>();

    public static List<String> getFondos() {
        return fondos;
    }

    public static void setFondos(List<String> fondos) {
        FondosGaleriaComentarios.fondos = fondos;
    }
}
