package com.example.luis.series.utilidades;


import java.util.List;


public class LoadPhoneNumbersFromContacts {
    private static List<String> phoneNumbersList;

    public static  void setListadoTelefonos(List<String> listado){
        phoneNumbersList=listado;
    }

    public  static List<String> getListadoTelefonos(){
        return phoneNumbersList;
    }
}
