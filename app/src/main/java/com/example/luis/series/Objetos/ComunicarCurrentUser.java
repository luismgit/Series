package com.example.luis.series.Objetos;

import com.google.firebase.auth.FirebaseUser;


public class ComunicarCurrentUser {
    private static FirebaseUser usuario=null;

    public static  void setUser(FirebaseUser user){
        usuario=user;
    }

    public  static FirebaseUser getUser(){
        return usuario;
    }
}
