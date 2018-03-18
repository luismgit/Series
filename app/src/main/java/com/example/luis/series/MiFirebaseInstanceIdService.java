package com.example.luis.series;

import android.util.Log;

import com.example.luis.series.references.FirebaseReferences;
import com.example.luis.series.utilidades.ComunicarClaveUsuarioActual;
import com.example.luis.series.utilidades.ComunicarCurrentUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MiFirebaseInstanceIdService extends FirebaseInstanceIdService{
    String TAG="Firebase_token";

    @Override
    public void onTokenRefresh() {
        //super.onTokenRefresh();
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if(ComunicarClaveUsuarioActual.getClave()!=null){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.USUARIOS_REFERENCE).child(ComunicarClaveUsuarioActual.getClave()).child("token");
            ref.setValue(refreshedToken);
        }


    }


}
