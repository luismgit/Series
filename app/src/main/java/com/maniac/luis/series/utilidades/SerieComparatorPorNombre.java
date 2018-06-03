package com.maniac.luis.series.utilidades;

import com.maniac.luis.series.Objetos.Series;

import java.util.Comparator;

/**
 * Created by Luis on 03/06/2018.
 */

public class SerieComparatorPorNombre implements Comparator<Series> {

    private boolean asc;
    private String parametro;
    public SerieComparatorPorNombre(boolean asc, String parametro) {
        this.asc = asc;
        this.parametro=parametro;
    }

    @Override
    public int compare(Series serie1, Series serie2) {
        int ret=0;

        if (asc) {
            if(parametro.equals("nombre")){
                ret = serie1.getNombre().compareTo(serie2.getNombre());
            }else if(parametro.equals("fechaEmision")){
                ret = serie1.getFechaEmision().compareTo(serie2.getFechaEmision());
            }else if(parametro.equals("likes")){
                ret = serie1.getLikes().compareTo(serie2.getLikes());
            }

        } else {
            ret = serie2.getNombre().compareTo(serie1.getNombre());
        }
        return ret;
    }
}
