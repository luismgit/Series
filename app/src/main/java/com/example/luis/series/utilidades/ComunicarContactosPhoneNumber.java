package com.example.luis.series.utilidades;


import java.util.ArrayList;
import java.util.List;

public class ComunicarContactosPhoneNumber {

    private static List<String> phoneNumbers=new ArrayList<>();

    public static List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public static void setPhoneNumbers(List<String> phoneNumbers) {
        ComunicarContactosPhoneNumber.phoneNumbers = phoneNumbers;
    }
    public static void removeAllPhoneNumbers(){
        phoneNumbers.removeAll(phoneNumbers);
    }

    public static void addPhoneNumber(String phoneNumber){
        phoneNumbers.add(phoneNumber);
    }
}
