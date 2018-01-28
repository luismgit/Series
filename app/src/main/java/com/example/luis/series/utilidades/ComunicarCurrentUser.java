package com.example.luis.series.utilidades;

import com.google.firebase.auth.FirebaseUser;


public class ComunicarCurrentUser {
    private static FirebaseUser usuario=null;

    public static  void setUser(FirebaseUser user){
        usuario=user;
    }

    public  static FirebaseUser getUser(){
        return usuario;
    }
    public static String getPhoneNumberUser(){
        String phoneNumber=usuario.getPhoneNumber();
        phoneNumber.replaceAll("\\s","");
        if(phoneNumber.substring(0,3).equals("+34")){
            phoneNumber=phoneNumber.substring(3,phoneNumber.length());
        }
        return phoneNumber;
    }
}
