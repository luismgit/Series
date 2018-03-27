package com.maniac.luis.series;

import com.maniac.luis.series.references.FirebaseReferences;
import com.maniac.luis.series.utilidades.ComunicarClaveUsuarioActual;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MiFirebaseInstanceIdService extends FirebaseInstanceIdService{

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
