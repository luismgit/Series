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
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
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
import com.example.luis.series.utilidades.Common;
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
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private boolean registroCerrado;
    TextView ayudaTexto;

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
        ayudaTexto=findViewById(R.id.ayudaTexto);
        String mystring=new String("Aquí");
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        ayudaTexto.setText(content);
        botonSIGIN=findViewById(R.id.botonSIGIN);
        etxtPhone=findViewById(R.id.etxtPhone);
        etxtPhone.setOnEditorActionListener(this);
        etxtPhoneCode=findViewById(R.id.etxtPhoneCode);
        etxtPhoneCode.setOnEditorActionListener(this);
        progressBarCircular=findViewById(R.id.progressBarCircular);

        //MÉTODO QUE COMPRUEBA LA VERSIÓN DEK SDK Y SI EL PÉRMISO ESTÁ YA DADO.
        pedirPermisos();

        //LA VARIABLE PREFERENCIAS COMPRUEBA SI ALGUNA VEZ EL USUARIO HA PASADO POR LA VENTANA DE REGISTRO
        SharedPreferences sharedPref = getSharedPreferences(Common.PREFERENCIAS,Context.MODE_PRIVATE);
        registroCerrado=sharedPref.getBoolean(Common.REGISTRO_CERRADO,false);
        Log.i("REGISTRO","El valor de registro en la autentificacion es -> " + registroCerrado);

        //INSTANCIAMOS EL OBJETO DE TIPO FirebaseAuth QUE COMPRUEBA SI EL USUARIO ESTÁ REGISTRADO EN FIREBASE
        mAuth=FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                //SI EL USUARIO NUNCA SE HA REGISTRADO LO MANDAMOS A LA ACTIVITY DE REGISTRO SI NUNCA HA PASADO POR ELLA PASÁNDOLE SU NÚMERO DE TELÉFONO
                if (user != null) {
                    Log.i("SESION", "Sesion iniciada con telefono " + user.getPhoneNumber());
                    ComunicarCurrentUser.setUser(user);
                    if(registroCerrado){
                        irARegistro(user.getPhoneNumber());

                    //SI EL USUARIO YA ESTÁ EN FIREBASE COMPROBAMOS SI NO ES LA 1º VEZ QUE EJECUTA LA APP  PARA MANDARLO DIRECTAMENTE A LA PANTALLA PRINCIPAL
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


    //MÉTODO QUE LANZA UN INTENT A LA PANTALLA DE REGISTRO
    private void irARegistro(String phoneNumber) {
        String num=phoneNumber;
        Intent intent = new Intent(this, registroActivity.class);
        intent.putExtra(Common.PHONE_NUMB,num);
        startActivity(intent);
    }

    //MÉTODO QUE LANZA UN INTENT A LA PANTALLA DE PRINCIPAL
    private void irAPrincipal() {
        Intent intent = new Intent(this, TabActivity.class);
        startActivity(intent);
    }

    //MÉTODO QUE SE EJECUTA CUANDO SE PULSA EL BOTÓN DE REGISTRAR , COMPROBAMOS QUE EL USUARIO HAYA INTRODUCIDO UN NÚMERO DE TELÉFONO
    public void requestCode(View view) {
        esconderTeclado(etxtPhone);
        phoneNumber=etxtPhone.getText().toString();
        if(TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(AutentificacionActivity.this, R.string.req_num_telef,Toast.LENGTH_LONG).show();
            Log.i("SESION", "Debe introducir el número de telefono");
            return;
        }
        //HACEMOS EL PROGESSBAR VISIBLE Y EVITAMOS EL USUARIO PUEDA VOLVER A PULSAR LOS BOTONES DEL LAYOUT
        progressBarCircular.setVisibility(View.VISIBLE);
        botonSMS.setClickable(false);
        botonSIGIN.setClickable(false);
        Log.i("SESION", "1----");
        //DAMOS 60 SEGUNDOS PARA QUE LLEGUE EL CÓDIGO DE SEGURIDAD
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            //ESTE MÉTODO SE EJECUTARÁ SI GOOGLE CONSIDERA QUE NO ES NECESARIO UN CODIGO DE VERIFICACION
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithCredential(phoneAuthCredential);
                Log.i("SESION", "SI GOOGLE CONSIDERA QUE NO ES NECESARIO UN CODIGO DE VERIFICACION");
            }

            //ESTE MÉTODO SE EJECUTARÁ SI EL TELÉFONO ES INCORRECTO, FALLA EL CÓDIGO DE VERIFICACIÓN O HAY UN ERROR EN EL EMULADOR
            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(AutentificacionActivity.this,getString(R.string.error_verif) + e.getMessage(),Toast.LENGTH_LONG).show();
                Log.i("SESION", "telefono incorrecto,codigo de verificacion o emulador...");
                progressBarCircular.setVisibility(View.INVISIBLE);
                botonSMS.setClickable(true);
                botonSIGIN.setClickable(true);
            }

            //ESTE MÉTODO SE EJECUTARÁ CUANDO EL CÓDIGO HAYA SIDO ENVIADO , GUARDAMOS EL CÓDIGO EN mVerificationId
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerificationId=mVerificationId;
                Log.i("SESION", "el codigo ha sido enviado , guardamos el codigo de verificacion");
            }

            //ESTE MÉTODO SE EJECUTARÁ CUANDO SE HAYA AGOTADO EL TIEMPO Y EL CÓDIGO DE VERFICACIÓN NO SE HAYA ENVIADO
            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(AutentificacionActivity.this,getString(R.string.time_out_code) + mVerificationId,Toast.LENGTH_LONG).show();
                Log.i("SESION", "se ha acabado el tiempo y el codigo de verficacion no se ha enviado");
                progressBarCircular.setVisibility(View.INVISIBLE);
                botonSMS.setClickable(true);
                botonSIGIN.setClickable(true);
            }
        });
    }


    //MÉTODO QUE SE EJECUTARÁ CUANDO LLEGUE EL CÓDIGO DE VERIFICACIÓN
    private void signInWithCredential(final PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful() ){
                            comprobarUsuarioYaRegistrado();
                            Log.i("SESION", "On complete() -> task.isSuccessful()");
                        }else{
                            Toast.makeText(AutentificacionActivity.this,getString(R.string.error_log) + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            Log.i("SESION", "On complete() -> task.isSuccessful() , fallo al loguearse");
                            progressBarCircular.setVisibility(View.INVISIBLE);
                            botonSMS.setClickable(true);
                            botonSIGIN.setClickable(true);
                        }
                    }
                });

    }

    //MÉTODO QUE COMPRUEBA SI EL USUARIO YA ESTÁ REGISTRADO EN LA BB.DD , SINO LO ESTÁ LE QUITAMOS LOS ESPACIOS Y EL +34 SI LOS TUVIERA Y LO MANDAMOS A LA ACTIVITY DE REGISTRO
    //SI ESTUVIERA YA REGISTRADO EN LA BB.DD  LO MADAMOS DIRECTAMENTE A LA PANTALLA PRINCIPAL
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

                    String phoneNumber=user.getPhoneNumber();
                    phoneNumber.replaceAll("\\s","");
                    if(phoneNumber.substring(0,3).equals("+34")){
                        phoneNumber=phoneNumber.substring(3,phoneNumber.length());
                    }
                    irARegistro(phoneNumber);
                    finish();
                }else{
                    Toast.makeText(AutentificacionActivity.this, R.string.usu_ya_registrado,Toast.LENGTH_LONG).show();
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

    //MÉTODO QUE SE EJECUTA CUANDO EL USUARIO INTRODUZCA EL CÓDIGO DE VERIFICACIÓN A MANO , EN NINGUNA PRUEBA DE LA APP HA SIDO NECESARIO INTRODUCIR EL CÓDIGO A MANO
    public void signIn(View view) {
        Utilities.esconderTeclado((EditText) etxtPhoneCode,this);
        String code = etxtPhoneCode.getText().toString();
        if(TextUtils.isEmpty(code)) {
            Toast.makeText(AutentificacionActivity.this, R.string.introd_cod_sms,Toast.LENGTH_LONG).show();
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
            Toast.makeText(AutentificacionActivity.this, R.string.error_cod_verif,Toast.LENGTH_LONG).show();
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    //MÉTODO QUE RECOGE CUANDO EL USUARIO ACEPTA O NO LOS PERMISOS DE LA APP
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                pedirPermisos();
            } else {
                Toast.makeText(this, R.string.perm_no_aceptados, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public static int getFirstTimeRun(Context contexto) {
        SharedPreferences sp = contexto.getSharedPreferences(Common.MYAPP, 0);
        int result, currentVersionCode = BuildConfig.VERSION_CODE;
        int lastVersionCode = sp.getInt(Common.FIRSTTIMERUN, -1);
        if (lastVersionCode == -1) result = 0; else
            result = (lastVersionCode == currentVersionCode) ? 1 : 2;
        sp.edit().putInt(Common.FIRSTTIMERUN, currentVersionCode).apply();
        return result;
    }


    //MÉTODO QUE SE EJECUTA CUANDO EL USUARIO PULSA EL BOTÓN DE NEXT O HECHO DEL TECLADO
    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if(i== EditorInfo.IME_ACTION_NEXT){
            Log.i("EDITORINFO","NEXT");
            requestCode(null);
        }
        if(i== EditorInfo.IME_ACTION_DONE){
            Log.i("EDITORINFO","DONE");
            signIn(null);

        }
       return false;
    }

    //MÉTODO QUE ESCONDE EL TECLADO
    private void esconderTeclado(EditText editText){
        EditText edit=editText;
        InputMethodManager teclado = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        teclado.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }


    public void ayuda(View view) {
        Toast.makeText(this,"Ayuda",Toast.LENGTH_SHORT).show();
    }
}
