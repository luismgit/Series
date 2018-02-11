package com.example.luis.series.actividades;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luis.series.R;
import com.example.luis.series.references.FirebaseReferences;
import com.example.luis.series.Objetos.Usuario;
import com.example.luis.series.utilidades.Common;
import com.example.luis.series.utilidades.Imagenes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class registroActivity extends AppCompatActivity  implements TextView.OnEditorActionListener{

 private EditText editTextNick,editTextEmail;
 private TextView textViewError;
 private ProgressBar progressBar;
 private Button botonAvatar,botonRegistro;
    private ImageView avatarIcono;
    private int iconoSeleccionado;
    private String claveUsuarioActual;
    private String nick,correo,phoneNumber;
    private int [] iconos;
    private static final int LISTA_ICONOS=1;
    private boolean error=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_activity);
        iconos= Imagenes.getAvataresUsuarios();
        avatarIcono=findViewById(R.id.avatarIcono);
        avatarIcono.setImageResource(iconos[0]);
        iconoSeleccionado=0;
        botonAvatar=findViewById(R.id.botonAvatar);
        botonRegistro=findViewById(R.id.botonRegistro);
        progressBar=findViewById(R.id.progressBar);
        editTextNick=findViewById(R.id.editTextNick);
        editTextEmail=findViewById(R.id.editTextCorreo);
        editTextEmail.setOnEditorActionListener(this);
        textViewError=findViewById(R.id.textViewError);
        textViewError.setText("");

        Intent intent=getIntent();

        //GUARDAMOS EN LA VARIABLE phoneNumber EL NÚMERO DE TELÉFONO DEL USUARIO QUE NOS HA LLEGADO EN EL INTENT
        phoneNumber=intent.getStringExtra(Common.PHONE_NUMB);

    }

    //MÉTODO QUE SE EJECUTARÁ CUANDO SE PULSE EL BOTÓN DE REGISTRO E INICIARÁ UNA ASYNTASK PARA PODER REALIZAR LA ANIMACIÓN DEL BOTÓN REGISTRO
    public void registro(View view) {

       new HiloRegistro().execute();

    }

    private void irAPrincipal() {
        Intent intent = new Intent(this, TabActivity.class);
        startActivity(intent);
    }

//MÉTODO QUE COMPRUEBA SI EL EMAIL INTRODUCIDO ES UN PATRÓN VÁLIDO DE EMAIL
    public boolean validateEmail(String email) {

        Pattern pattern;
        Matcher matcher;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();

    }

    //MÉTODO PARA QUE CUANDO EL USUARIO PULSE EL BOTÓN HECHO EN EL TECLADO SE REESTABLEZCAN A VACÍO EL CAMPO DE ERROR
    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

        if(i== EditorInfo.IME_ACTION_DONE){
            textViewError.setText("");

        }
        return false;
    }

    //MÉTODO QUE SE EJECUTA CUANDO EL USUARIO PULSE ATRÁS , LO GUARDAMOS EN UNA PREFERENCIA PARA QUE LA PRÓXIMA VEZ QUE ENTRE EN LA APP VAYA DIRECTAMENTE A ESTA PANTALLA
    public void onBackPressed(){
        SharedPreferences sharedPref = getSharedPreferences(Common.PREFERENCIAS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(Common.REGISTRO_CERRADO,true);
        editor.commit();
        Log.i("REGISTRO","Hemos cerrado la pantalla de registro");
        super.onBackPressed();
    }

    //MÉTODO QUE ABRIRÁ LA PANTALLA DE SELECCIÓN DE AVATARES DE ListaIconos ESPERANDO UN RESULTADO
    public void seleccionarAvatar(View view) {

        Intent intent = new Intent(this,ListaIconos.class);
        startActivityForResult(intent,LISTA_ICONOS);
    }

    //MÉTODO QUE SE EJECUTARÁ CUANDO CERREMOS LA PANTALLA ListaIconos
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            //COMPROBAMOS QUE VIENE DE ListaIconos
            case LISTA_ICONOS:

                //SI LOS DATOS NO SON NULOS
                if(data != null && resultCode == RESULT_OK){
                    Log.i("OnActivityResult","Numero de icono -> " + data.getIntExtra("ICONOSELECCIONADO",-1));

                    //GUARDAMOS EN numIcono EL NºDE AVATAR(VISTA) SELECCIONADA
                    int numIcono = data.getIntExtra(Common.ICONOSELECCIONADO,-1);
                    if(numIcono==-1){
                        //SI NO HA SELECCIONADO NINGÚN AVATAR PONEMOS EN PANTALLA EL AVATAR SELECCIONADO EN UNA HIPOTÉTICA ANTERIOR VEZ
                        avatarIcono.setImageResource(iconos[iconoSeleccionado]);
                    }else{
                        //SI SE HA SELECCIONADO UN AVATAR, ACTUALIZA LA VARIABLE iconoSeleccionado Y LA PONEMOS EN PANTALLA
                        iconoSeleccionado=numIcono;
                        avatarIcono.setImageResource(iconos[iconoSeleccionado]);
                    }

                }else{
                    avatarIcono.setImageResource(iconos[0]);
                }
                break;
        }
    }
    private class HiloRegistro extends AsyncTask<Void,Void,Void>{

        Animation animation;

        //PREPARAMOS A ANIMACIÓN
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
            animation.setDuration(1000);

        }

        //INICIAMOS LA ANIMACIÓN DEL BOTÓN Y LE DAMOS UNA PAUSA DE 1 SEG PARA QUE SE PRODUZCA LA ANIMACIÓN ANTES DE QUE VAYA A LA SIGUIENTE ACTIVITY
        @Override
        protected Void doInBackground(Void... voids) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Button boton = findViewById(R.id.botonRegistro);
                    boton.startAnimation(animation);
                }
            });
            try {
                sleep(1000);
            } catch (InterruptedException e) {

            }
            return null;
        }

        //MÉTODO QUE COMPRUEBA TODO LO REFERENTE AL REGISTRO
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //SI EL USUARIO NO SELECCIONA NINGUN ICONO SELECCIONAMOS EL DE POR DEFECTO
             if(iconoSeleccionado == -1){
            iconoSeleccionado=0;
        }

        //HACEMOS TODAS LAS COMPROBACIONES DE NICK Y EMAIL Y LES QUITAMOS POSIBLES ESPACIOS
        textViewError.setText("");
         nick=editTextNick.getText().toString().trim();
         correo=editTextEmail.getText().toString().trim();
         if(nick.equals("") || correo.equals("")){
             textViewError.setText(R.string.error_registro_campos_vacios);
         }else if(nick.length()>15) {
             textViewError.setText(R.string.error_registro_nick);
         }else if(!validateEmail(correo)) {
             textViewError.setText(R.string.error_email_format);
         }else{

             FirebaseDatabase database=FirebaseDatabase.getInstance();

             //CREAMOS UNA REFERENCIA AL NODO USUARIOS DE FIREBASE E INICIAMOS SU LISTENER
             database.getReference(FirebaseReferences.USUARIOS_REFERENCE).addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {
                     for(DataSnapshot snapshot :
                             dataSnapshot.getChildren()){

                         //POR CADA VUELTA DEL FOR CONSEGUIMOS UN USUARIO DE LA BB.DD
                         Usuario usuario = snapshot.getValue(Usuario.class);


                         if(usuario.getNick().equals(nick)){
                             textViewError.setText(R.string.msg_registro_uso_email);
                             error=true;
                         }

                     }

                     //SI NO HAY ERRORES EN EL REGISTRO PASAMOS A DAR DE ALTA AL USUARIO EN LA BB.DD
                     if(!error){
                         progressBar.setVisibility(View.VISIBLE);
                         botonAvatar.setClickable(false);
                         botonRegistro.setClickable(false);
                         //CREAMOS UN NUEVO OBJETO DE TIPO USUARIO
                         Usuario usuario = new Usuario(nick,phoneNumber,correo,iconoSeleccionado,FirebaseReferences.ONLINE);
                         //CONSEGUIIMOS UNA REFERENCIA AL NODO ROOT DE LA BB.DD
                         DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                         //LE AÑADIMOS UN NODO HIJO A LA REFERENCIA ANTERIOR CON CLAVE GENERADA AUTOMÁTICA (MÉTODO PUSH)
                         ref.child(FirebaseReferences.USUARIOS_REFERENCE).push().setValue(usuario);
                         Toast.makeText(registroActivity.this, R.string.toast_usu_reg,Toast.LENGTH_SHORT).show();
                         //VAMOS A LA PANTALLA PRINCIPAL
                         irAPrincipal();
                         finish();
                     }
                 }

                 @Override
                 public void onCancelled(DatabaseError databaseError) {

                 }
             });
         }
        }


    }


}
