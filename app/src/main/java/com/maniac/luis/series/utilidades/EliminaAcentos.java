package com.maniac.luis.series.utilidades;



public class EliminaAcentos {

    // La siguiente funcion elimina los acentos de las letras
    public static String eliminarAcentos(String str) {

        final String ORIGINAL = "ÁáÉéÍíÓóÚúÜü";
        final String REEMPLAZO = "AaEeIiOoUuUu";

        if (str == null) {
            return null;
        }
        char[] array = str.toCharArray();
        for (int indice = 0; indice < array.length; indice++) {
            int pos = ORIGINAL.indexOf(array[indice]);
            if (pos > -1) {
                array[indice] = REEMPLAZO.charAt(pos);
            }
        }
        return new String(array);
    }
}
