package com.example.luis.series.utilidades;


import com.example.luis.series.R;

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

    private static int [] fondos =new int[]
            {   R.drawable.black,
                    R.drawable.big,
                    R.drawable.breaking_bad,
                    R.drawable.dexter_back,
                    R.drawable.game_back,
                    R.drawable.house_back1,
                    R.drawable.lost_back,
                    R.drawable.narcos_back,
                    R.drawable.sons_back1,
                    R.drawable.stranger_back,
                    R.drawable.simpson_back,
                    R.drawable.walking_back,
                    R.drawable.vikings_back

            };


    public static int [] getIconosSeries(){
        return iconos;
    }
    public static int [] getAvataresUsuarios(){
        return avatares;
    }
    public static int [] getFondosPantalla(){
        return fondos;
    }
}
