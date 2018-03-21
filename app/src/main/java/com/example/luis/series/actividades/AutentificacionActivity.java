package com.example.luis.series.actividades;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaCas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.luis.series.BuildConfig;
import com.example.luis.series.R;
import com.example.luis.series.utilidades.Common;
import com.example.luis.series.utilidades.ComunicarCurrentUser;
import com.example.luis.series.references.FirebaseReferences;
import com.example.luis.series.Objetos.Usuario;
import com.example.luis.series.utilidades.Imagenes;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


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
    private static final int PERMISSIONS_REQUEST_WRITE = 200;
    private static final int PERMISSIONS_REQUEST_READ = 400;
    private static final int PERMISSIONS_REQUEST_CAMERA = 500;
    private boolean registroCerrado;
    TextView ayudaTexto;
    String emailAyuda;
    String passwordAyuda;
    Session session;
    EditText editTextMensajeAyuda;
    EditText correoUsuAyuda;
    int contador=0;
    List<String> listaFondos;
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("compruebaNotif","entra autentifOncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
       // SharedPreferences Pref = getSharedPreferences(Common.NOTIFICACION,Context.MODE_PRIVATE);
       //SharedPreferences.Editor editor = Pref.edit();
        //editor.putBoolean("notify",false);
        //editor.commit();
        listaFondos=new ArrayList<>();
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
                            boolean deNotificacion=false;
                            String accion = getIntent().getStringExtra(Common.NOTIFICACION);
                            SharedPreferences sharedPre = getSharedPreferences(Common.NOTIFICACION,Context.MODE_PRIVATE);
                            deNotificacion=sharedPre.getBoolean("notify",false);
                            Log.i("entra","deNotificacion-> " + deNotificacion);
                            if(deNotificacion){
                                if(accion!=null){
                                    if(accion.equals(Common.NOTIFICACION)){
                                       /* SharedPreferences sharedPreferences = getSharedPreferences(Common.NOTIFICACION,Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean("notify",false);
                                        editor.commit();*/
                                        Log.i("compruebaNotif","Autentifica -- ha llegado despues de una notificacion");
                                        String nombreSerie=getIntent().getStringExtra(Common.NOMBRE_SERIE_COMENTARIOS);
                                        irAPrincipal(true,nombreSerie);
                                    }
                                }
                            }else{
                                Log.i("compruebaNotif","Autentifica -- no ha habido ninguna notificacion");
                                irAPrincipal(false,"");
                            }
                            finish();
                        }
                    }

                } else {
                    Log.i("SESION", "Sesion cerrada");
                    setContentView(R.layout.autentificacion_activity);
                    botonSMS=findViewById(R.id.botonSMS);
                    ayudaTexto=findViewById(R.id.ayudaTexto);
                    String mystring=new String("Aquí");
                    SpannableString content = new SpannableString(mystring);
                    content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
                    ayudaTexto.setText(content);
                    botonSIGIN=findViewById(R.id.botonSIGIN);
                    etxtPhone=findViewById(R.id.etxtPhone);
                    etxtPhone.setOnEditorActionListener(AutentificacionActivity.this);
                    etxtPhoneCode=findViewById(R.id.etxtPhoneCode);
                    etxtPhoneCode.setOnEditorActionListener(AutentificacionActivity.this);
                    progressBarCircular=findViewById(R.id.progressBarCircular);
                }
            }
        };

        //SACAMOS DE LA BB.DD EL EMAIL Y PASSWORD DE LA CUENTA DE CORREO PARA ENVIAR UN CORREO SI EL USUARIO PULSA EN EL MENÚ DE CONTACTO
        FirebaseDatabase fdb=FirebaseDatabase.getInstance();
        DatabaseReference dr=fdb.getReference();
        dr.child(FirebaseReferences.COMMON).child(FirebaseReferences.EMAIL_AYUDA).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                emailAyuda= (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseDatabase f=FirebaseDatabase.getInstance();
        DatabaseReference r=f.getReference();
        r.child(FirebaseReferences.COMMON).child(FirebaseReferences.PASSWORD_AYUDA).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                passwordAyuda= (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


    //MÉTODO QUE LANZA UN INTENT A LA PANTALLA DE REGISTRO
    private void irARegistro(String phoneNumber) {
        String num=phoneNumber;
        Intent intent = new Intent(this, registroActivity.class);
        intent.putExtra(Common.PHONE_NUMB,num);
        startActivity(intent);
    }

    //MÉTODO QUE LANZA UN INTENT A LA PANTALLA DE PRINCIPAL
    private void irAPrincipal(boolean notificacion,String nombreSerie) {
        if(notificacion){
            Log.i("compruebaNotif","Se va a tabACt con notif");
            Intent intent = new Intent(AutentificacionActivity.this, TabActivity.class);
            intent.putExtra(Common.NOTIFICACION,Common.NOTIFICACION);
            intent.putExtra(Common.NOMBRE_SERIE_COMENTARIOS,nombreSerie);
            String accion = intent.getStringExtra(Common.NOTIFICACION);
            Log.i("compruebaNotif","Accion en autenif -> " + accion);
            startActivity(intent);
        }else{
            Log.i("compruebaNotif","Se va a tabACt SIN notif");
            Intent intent = new Intent(AutentificacionActivity.this, TabActivity.class);
            startActivity(intent);
        }

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
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+34"+phoneNumber, 60, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

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
                    irAPrincipal(false,"");
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


        //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
        //      requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        // }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if ((checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                    (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                    (checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                    (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)) {

            }

            if ((shouldShowRequestPermissionRationale(CAMERA)) ||
                    (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) ||
                    (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) ||
                    (shouldShowRequestPermissionRationale(READ_CONTACTS))) {

                cargarDialogoReacomendacion();
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA,READ_EXTERNAL_STORAGE,READ_CONTACTS},100);
                }
            }


        }
    }

    //MÉTODO QUE RECOGE CUANDO EL USUARIO ACEPTA O NO LOS PERMISOS DE LA APP


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100){
            if(grantResults.length==4 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED
                     && grantResults[2]==PackageManager.PERMISSION_GRANTED
                    && grantResults[3]==PackageManager.PERMISSION_GRANTED){

            }else{
                solicitarPermisosManual();
            }
        }
    }

    private void solicitarPermisosManual() {
       final CharSequence[] opciones ={"si","no"};
       final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(AutentificacionActivity.this);
       alertOpciones.setTitle("Desea configurar los permisos de forma manual?");
       alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {

               if(opciones[i].equals("si")){
                   Intent intent=new Intent();
                   intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                   Uri uri = Uri.fromParts("package",getPackageName(),null);
                   intent.setData(uri);
                   startActivity(intent);
               }else{
                   Toast.makeText(getApplicationContext(),"La aplicación no puede seguir sin estos permisos",Toast.LENGTH_LONG).show();
                   dialogInterface.dismiss();
                   finish();
               }
           }
       });
       alertOpciones.show();
    }

    private void cargarDialogoReacomendacion() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(AutentificacionActivity.this);
        dialogo.setTitle("Permisos desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la aplicación");
        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA,READ_EXTERNAL_STORAGE,READ_CONTACTS},100);
                }
            }
        });
        dialogo.show();

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

    //MÉTODO QUE CREA UN DIÁLOGO DE AYUDA POR SI NO LE LLEGA EL CÓDIGO DE VERFICACIÓN
    public void ayuda(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.ayuda);
        builder.setMessage(R.string.msg_ayuda);

        //CONSEGUIMOS LAS VISTAS DE NUESTRO layout dialogo_view INFLÁNDOLO Y SE LO APLICAMOS AL BUILDER
        LayoutInflater inflater = getLayoutInflater();
        View miVista = inflater.inflate(R.layout.dialogo_view,null);
        builder.setView(miVista);
        editTextMensajeAyuda=miVista.findViewById(R.id.editTextAyuda);
        correoUsuAyuda=miVista.findViewById(R.id.userEmail);


        builder.setPositiveButton(R.string.btn_enviar_ayuda, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {



            }
        });

        builder.setNegativeButton(R.string.btn_cancelar_ayuda,null);
        builder.setCancelable(false);
        final AlertDialog dialog =builder.create();
        dialog.show();

        //IMPLEMENTA UN LISTENER SOBRE EL BOTON DE ACEPTAR QUE EN CASO DE QUE EL CORREO INTRODUCIDO POR EL USUARIO SEA VALIDO ENVIA UN MENSAJE A UNA CUENTA DE CORREO
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Utilities.validateEmail(correoUsuAyuda.getText().toString())){
                    Toast.makeText(AutentificacionActivity.this,R.string.error_email_format,Toast.LENGTH_SHORT).show();
                }else{
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    Properties properties=new Properties();
                    properties.put("mail.smtp.host","smtp.gmail.com");
                    properties.put("mail.smtp.socketFactory,port","465");
                    properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
                    properties.put("mail.smtp.auth","true");
                    properties.put("mail.smtp.port","465");

                    try{

                        session=Session.getDefaultInstance(properties, new Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(emailAyuda,passwordAyuda);
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    if(session!=null){
                        javax.mail.Message message = new MimeMessage(session);
                        try {
                            message.setFrom(new InternetAddress(emailAyuda));
                            message.setSubject(correoUsuAyuda.getText().toString());
                            message.setRecipients(javax.mail.Message.RecipientType.TO,InternetAddress.parse(emailAyuda));
                            message.setContent(editTextMensajeAyuda.getText().toString(),"text/html;charset=utf-8");
                            Transport.send(message);
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(AutentificacionActivity.this, R.string.msg_enviado_ayuda,Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

    }
}
