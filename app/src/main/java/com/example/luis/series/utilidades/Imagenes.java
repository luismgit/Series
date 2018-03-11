package com.example.luis.series.utilidades;


import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.example.luis.series.R;

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
