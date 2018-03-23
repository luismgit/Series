package com.maniac.luis.series.utilidades;


public class ComunicarCurrentUser {
   // private static FirebaseUser usuario=null;
    private  static String phoneNumbe;

   /*public static  void setUser(FirebaseUser user){
        usuario=user;

    }

    public  static FirebaseUser getUser(){
        return usuario;
    }*/
    public static String getPhoneNumberUser(){
        String phoneNumber=phoneNumbe;
        phoneNumber.replaceAll("\\s","");
        if(phoneNumber.substring(0,3).equals("+34")){
            phoneNumber=phoneNumber.substring(3,phoneNumber.length());
        }
        return phoneNumber;
    }

    public static void setPhoneNumber(String phoneNumber) {
        ComunicarCurrentUser.phoneNumbe = phoneNumber;
    }
}
