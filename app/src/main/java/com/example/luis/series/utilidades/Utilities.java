package com.example.luis.series.utilidades;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utilities {
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
    public static boolean validateEmail(String email){
        Pattern pattern;
        Matcher matcher;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }


}
