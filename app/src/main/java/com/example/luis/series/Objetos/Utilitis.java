package com.example.luis.series.Objetos;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;



public class Utilitis {
    public static boolean REGISTRO_CERRADO;
    public static void esconderTeclado(EditText editText, Context contexto){
        EditText edit=editText;
        InputMethodManager teclado = (InputMethodManager) contexto.getSystemService(contexto.INPUT_METHOD_SERVICE);
        teclado.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }
    public static boolean getRegistroCerrado(boolean registroCerrado){
        REGISTRO_CERRADO=registroCerrado;
        return REGISTRO_CERRADO;
    }


}
