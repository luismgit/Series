package com.example.luis.series.actividades;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luis.series.BuildConfig;
import com.example.luis.series.R;
import com.example.luis.series.utilidades.ComunicarCurrentUser;
import com.example.luis.series.references.FirebaseReferences;
import com.example.luis.series.Objetos.Usuario;
import com.example.luis.series.utilidades.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;



public class AutentificacionActivity extends AppCompatActivity  implements TextView.OnEditorActionListener{

    EditText etxtPhone,etxtPhoneCode;
    EditText editTextEmail, editTextPass;
    Button botonSMS,botonSIGIN;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase dataBase;
    FirebaseUser user;
    FirebaseAuth mAuth;
    private String mVerificationId;
    boolean usuarioRegistrado=false;
    boolean usuarioReg;
    String phoneNumber;
    ProgressBar progressBarCircular;
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private boolean registroCerrado;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autentificacion_activity);
        botonSMS=findViewById(R.id.botonSMS);
        botonSIGIN=findViewById(R.id.botonSIGIN);
        etxtPhone=findViewById(R.id.etxtPhone);
        etxtPhone.setOnEditorActionListener(this);
        etxtPhoneCode=findViewById(R.id.etxtPhoneCode);
        etxtPhoneCode.setOnEditorActionListener(this);
        progressBarCircular=findViewById(R.id.progressBarCircular);
        pedirPermisos();
        SharedPreferences sharedPref = getSharedPreferences("Preferencias",Context.MODE_PRIVATE);
        registroCerrado=sharedPref.getBoolean("registroCerrado",false);
        Log.i("REGISTRO","el valor de registro en la autentificacion es -> " + registroCerrado);
        mAuth=FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.i("SESION", "Sesion iniciada con telefono " + user.getPhoneNumber());
                    ComunicarCurrentUser.setUser(user);
                    if(registroCerrado){
                        irARegistro(user.getPhoneNumber());
                    }else{
                        if(getFirstTimeRun(AutentificacionActivity.this)==1){
                            irAPrincipal();
                            finish();
                        }
                    }

                } else {
                    Log.i("SESION", "Sesion cerrada");

                }
            }
        };



    }


    private void irARegistro(String phoneNumber) {
        String num=phoneNumber;
        Intent intent = new Intent(this, registroActivity.class);
        intent.putExtra("phoneNumber",num);
        startActivity(intent);
    }


    private void irAPrincipal() {
        Intent intent = new Intent(this, TabActivity.class);
        startActivity(intent);
    }


    public void requestCode(View view) {
        esconderTeclado(etxtPhone);
        phoneNumber=etxtPhone.getText().toString();
        if(TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(AutentificacionActivity.this,"Debe introducir el número de telefono",Toast.LENGTH_LONG).show();
            Log.i("SESION", "Debe introducir el número de telefono");
            return;
        }
        //new AsyncTask_load().execute().;
        progressBarCircular.setVisibility(View.VISIBLE);
        botonSMS.setClickable(false);
        botonSIGIN.setClickable(false);
        Log.i("SESION", "1----");
        //phoneNumber="+34"+phoneNumber;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                //SI GOOGLE CONSIDERA QUE NO ES NECESARIO UN CODIGO DE VERIFICACION
                signInWithCredential(phoneAuthCredential);
                Log.i("SESION", "SI GOOGLE CONSIDERA QUE NO ES NECESARIO UN CODIGO DE VERIFICACION");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                //telefono incorrecto,codigo de verificacion o emulador...
                Toast.makeText(AutentificacionActivity.this,"onVerificationFailed" + e.getMessage(),Toast.LENGTH_LONG).show();
                Log.i("SESION", "telefono incorrecto,codigo de verificacion o emulador...");
                progressBarCircular.setVisibility(View.INVISIBLE);
                botonSMS.setClickable(true);
                botonSIGIN.setClickable(true);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                //el codigo ha sido enviado , guardamos el codigo de verificacion
                super.onCodeSent(s, forceResendingToken);
                mVerificationId=mVerificationId;
                Log.i("SESION", "el codigo ha sido enviado , guardamos el codigo de verificacion");
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                //se ha acabado el tiempo y el codigo de verficacion no se ha enviado
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(AutentificacionActivity.this,"onCodeAutoRetrievalTimeOut : " + mVerificationId,Toast.LENGTH_LONG).show();
                Log.i("SESION", "se ha acabado el tiempo y el codigo de verficacion no se ha enviado");
                progressBarCircular.setVisibility(View.INVISIBLE);
                botonSMS.setClickable(true);
                botonSIGIN.setClickable(true);
            }
        });
    }



    private void signInWithCredential(final PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful() ){
                            comprobarUsuarioYaRegistrado();
                            Log.i("SESION", "On complete() -> task.isSuccessful()");
                        }else{
                            Toast.makeText(AutentificacionActivity.this,"Fallo al loguearse " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            Log.i("SESION", "On complete() -> task.isSuccessful() , fallo al loguearse");
                            progressBarCircular.setVisibility(View.INVISIBLE);
                            botonSMS.setClickable(true);
                            botonSIGIN.setClickable(true);
                        }
                    }
                });

    }

    private boolean comprobarUsuarioYaRegistrado(){

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        database.getReference(FirebaseReferences.USUARIOS_REFERENCE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot :
                        dataSnapshot.getChildren()){
                    Usuario u = snapshot.getValue(Usuario.class);
                    String formatNumber="+34" + u.getTelefono();
                    Log.i("prueba","formatNumber-> " + formatNumber);
                    Log.i("prueba","user-> " + user.getPhoneNumber());
                    if(formatNumber.equals(user.getPhoneNumber())){
                        usuarioRegistrado=true;
                    }
                }
                if(!usuarioRegistrado){
                    //Toast.makeText(AutentificacionActivity.this,"Usuario registrado",Toast.LENGTH_SHORT).show();
                    //Log.i("SESION", "Usuario registrado");
                    String phoneNumber=user.getPhoneNumber();
                    phoneNumber.replaceAll("\\s","");
                    if(phoneNumber.substring(0,3).equals("+34")){
                        phoneNumber=phoneNumber.substring(3,phoneNumber.length());
                    }
                    irARegistro(phoneNumber);
                    finish();
                }else{
                    Toast.makeText(AutentificacionActivity.this,"Usuario ya registrado en la BB.DD",Toast.LENGTH_LONG).show();
                    Log.i("SESION", "Usuario ya registrado en la BB.DD");
                    irAPrincipal();
                    finish();
                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return usuarioRegistrado;

    }

    public void signIn(View view) {
        Utilities.esconderTeclado((EditText) etxtPhoneCode,this);
        String code = etxtPhoneCode.getText().toString();
        if(TextUtils.isEmpty(code)) {
            Toast.makeText(AutentificacionActivity.this,"Debe introducir el código recibido pos SMS",Toast.LENGTH_LONG).show();
            Log.i("SESION", "Debe introducir el código recibido pos SMS");
            progressBarCircular.setVisibility(View.INVISIBLE);
            botonSMS.setClickable(true);
            botonSIGIN.setClickable(true);
            return;
        }
        try{
            signInWithCredential(PhoneAuthProvider.getCredential(mVerificationId,code));
        }catch(Exception e){
            Log.i("SESION", "Codigo incorrecto");
            Toast.makeText(AutentificacionActivity.this,"Código incorrecto",Toast.LENGTH_LONG).show();
            progressBarCircular.setVisibility(View.INVISIBLE);
            botonSMS.setClickable(true);
            botonSIGIN.setClickable(true);
        }



    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }


    private void pedirPermisos() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                pedirPermisos();
            } else {
                Toast.makeText(this, "Si no acepta los permisos la aplicacion no puede seguir", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public static int getFirstTimeRun(Context contexto) {
        SharedPreferences sp = contexto.getSharedPreferences("MYAPP", 0);
        int result, currentVersionCode = BuildConfig.VERSION_CODE;
        int lastVersionCode = sp.getInt("FIRSTTIMERUN", -1);
        if (lastVersionCode == -1) result = 0; else
            result = (lastVersionCode == currentVersionCode) ? 1 : 2;
        sp.edit().putInt("FIRSTTIMERUN", currentVersionCode).apply();
        return result;
    }


    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if(i== EditorInfo.IME_ACTION_NEXT){
            Log.i("EDITORINFO","NEXT");
            requestCode(null);
            //esconderTeclado((EditText) textView);
            //Utilities.esconderTeclado((EditText) textView,this);
        }
        if(i== EditorInfo.IME_ACTION_DONE){
            Log.i("EDITORINFO","DONE");
            signIn(null);
            //esconderTeclado((EditText) textView);
            //Utilities.esconderTeclado((EditText) textView,this);
        }
       return false;
    }

    //MÉTODO QUE ESCONDE EL TECLADO
    private void esconderTeclado(EditText editText){
        EditText edit=editText;
        InputMethodManager teclado = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        teclado.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }

   /* public class AsyncTask_load extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("SESION", "onPreExecute()");
            progressBarCircular.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.i("SESION", "doInBackground()");
            Log.i("SESION", "1----");
            PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, AutentificacionActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                    //SI GOOGLE CONSIDERA QUE NO ES NECESARIO UN CODIGO DE VERIFICACION
                    signInWithCredential(phoneAuthCredential);
                    Log.i("SESION", "SI GOOGLE CONSIDERA QUE NO ES NECESARIO UN CODIGO DE VERIFICACION");
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    //telefono incorrecto,codigo de verificacion o emulador...
                    Toast.makeText(AutentificacionActivity.this,"onVerificationFailed" + e.getMessage(),Toast.LENGTH_LONG).show();
                    Log.i("SESION", "telefono incorrecto,codigo de verificacion o emulador...");
                }

                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    //el codigo ha sido enviado , guardamos el codigo de verificacion
                    super.onCodeSent(s, forceResendingToken);
                    mVerificationId=mVerificationId;
                    Log.i("SESION", "el codigo ha sido enviado , guardamos el codigo de verificacion");
                }

                @Override
                public void onCodeAutoRetrievalTimeOut(String s) {
                    //se ha acabado el tiempo y el codigo de verficacion no se ha enviado
                    super.onCodeAutoRetrievalTimeOut(s);
                    Toast.makeText(AutentificacionActivity.this,"onCodeAutoRetrievalTimeOut : " + mVerificationId,Toast.LENGTH_LONG).show();
                    Log.i("SESION", "se ha acabado el tiempo y el codigo de verficacion no se ha enviado");
                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
             super.onProgressUpdate(values);
            Log.i("SESION", "onProgressUpdate()");
            progressBarCircular.setProgress(values[0]);
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i("SESION", "onPostExecute()");

        }




    }*/



}
