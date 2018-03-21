package com.example.luis.series.utilidades;

import java.util.Hashtable;

/**
 * Created by Luis on 21/03/2018.
 */

public class ListaNumerosAgendaTelefonos {

   private static Hashtable<String,String> contactos;

    public static Hashtable<String, String> getContactos() {
        return contactos;
    }

    public static void setContactos(Hashtable<String, String> contactos) {
        ListaNumerosAgendaTelefonos.contactos = contactos;
    }
}
