package com.example.luis.series.utilidades;


import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.example.luis.series.R;

import java.util.ArrayList;
import java.util.List;

public class Imagenes {

    private static int [] iconos = new int[]
            {       R.drawable.breaking,
                    R.drawable.thrones,
                    R.drawable.theory,
                    R.drawable.narcos,
                    R.drawable.simpson,
                    R.drawable.anarchy,
                    R.drawable.stranger,
                    R.drawable.vikins,
                    R.drawable.mirror,
                    R.drawable.walking,
                    R.drawable.west,
                    R.drawable.lost,
                    R.drawable.cards,
                    R.drawable.dexter};

    private static int [] avatares = new int[]
            {       R.drawable.avatar0,
                    R.drawable.avatar1,
                    R.drawable.avatar2,
                    R.drawable.avatar3,
                    R.drawable.avatar4,
                    R.drawable.avatar5,
                    R.drawable.avatar6,
                    R.drawable.avatar7,
                    R.drawable.avatar8,
                    R.drawable.avatar9,
                    R.drawable.avatar10,
                    R.drawable.avatar11,
                    R.drawable.avatar12,
                    R.drawable.avatar13};

    private static List<String> listaFondos =new ArrayList<>();


    public static List<String> getListaFondos() {
        return listaFondos;
    }

    public static void setListaFondos(List<String> listaFondos) {
        Imagenes.listaFondos = listaFondos;
    }

    public static int [] getIconosSeries(){
        return iconos;
    }
    public static int [] getAvataresUsuarios(){
        return avatares;
    }

}
