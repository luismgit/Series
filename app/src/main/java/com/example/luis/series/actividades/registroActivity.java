package com.example.luis.series.actividades;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.transcode.BitmapBytesTranscoder;
import com.example.luis.series.R;
import com.example.luis.series.references.FirebaseReferences;
import com.example.luis.series.Objetos.Usuario;
import com.example.luis.series.utilidades.Common;
import com.example.luis.series.utilidades.Imagenes;
import com.example.luis.series.utilidades.Utilities;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.like.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.Timestamp;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class registroActivity extends AppCompatActivity  implements TextView.OnEditorActionListener{

 private EditText editTextNick,editTextEmail;
 private TextView textViewError;
 private ProgressBar progressBar;
 private Button botonAvatar,botonRegistro;
    private ImageView avatarIcono;
    private String claveUsuarioActual;
    private String nick,correo,phoneNumber;
    private static final int LISTA_ICONOS=1;
    private boolean error=false;
    private StorageReference refStorage;
    private static final int GALLERY_INTENT=1;
    private static final int CAMERA_INTENT=2;
    StorageReference filePath;
    boolean camara=false;
    boolean galeria=false;
    ByteArrayOutputStream stream;
    Uri imagenSeleccionada;
    Uri enlaceFotoFirebasde;
    Uri file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_activity);
        refStorage= FirebaseStorage.getInstance().getReference();
        avatarIcono=findViewById(R.id.avatarIcono);
        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/series-15075.appspot.com/o/foto_perfil%2Fseries.png?alt=media&token=1e9f324b-fd5d-4f8e-97a4-bca0fffe92b5")
                .into(avatarIcono);
        avatarIcono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarAvatar(null);
            }
        });
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

    //MÉTODO QUE ABRIRÁ LA UN DIÁLOGO PARA SELECCIONAR DE DONDE QUEREMOS TOMAR LA FOTO
    public void seleccionarAvatar(View view) {

        final CharSequence[] items = {"Cámara","Galería","Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(registroActivity.this);
        builder.setTitle("Selecciona un foto de perfil");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String opcion= (String) items[i];
                switch (opcion){

                    case "Cámara":
                        Log.i("perfil","Selecciona camara");

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent,CAMERA_INTENT);
                        break;

                    case "Galería":
                        Log.i("perfil","Selecciona galeria");
                        Intent intent1=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent1.setType("image/*");
                        startActivityForResult(intent1.createChooser(intent1,"Selecciona foto"),GALLERY_INTENT);

                        break;

                    case "Cancelar":
                        dialogInterface.dismiss();
                        break;
                }

            }
        });
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK){

            if(requestCode==CAMERA_INTENT){
                camara=true;
                galeria=false;
                Bundle bundle = data.getExtras();
                final Bitmap bmp = (Bitmap) bundle.get("data");
                Log.i("perfil","requestCode==CAMERA_INTENT");
                avatarIcono.setImageBitmap(bmp);
                stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);


            }else if(requestCode==GALLERY_INTENT){
                galeria=true;
                camara=false;
                Log.i("perfil","requestCode==GALLERY_INTENT");
                 imagenSeleccionada = data.getData();
                Log.i("perfil","Uri -> " + imagenSeleccionada.toString());
                //avatarIcono.setImageURI(imagenSeleccionada);
                //avatarIcono.setRotation(270);
                Glide.with(registroActivity.this)
                        .load(data.getData())
                        .centerCrop()
                        .fitCenter()
                        .into(avatarIcono);
            }
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


        //HACEMOS TODAS LAS COMPROBACIONES DE NICK Y EMAIL Y LES QUITAMOS POSIBLES ESPACIOS
         textViewError.setText("");
         nick=editTextNick.getText().toString().trim();
         correo=editTextEmail.getText().toString().trim();
         if(nick.equals("") || correo.equals("")){
             textViewError.setText(R.string.error_registro_campos_vacios);
         }else if(nick.length()>15) {
             textViewError.setText(R.string.error_registro_nick);
         }else if(!Utilities.validateEmail(correo)) {
             textViewError.setText(R.string.error_email_format);
         }else{

             FirebaseDatabase database=FirebaseDatabase.getInstance();

             //CREAMOS UNA REFERENCIA AL NODO USUARIOS DE FIREBASE E INICIAMOS SU LISTENER
             database.getReference(FirebaseReferences.USUARIOS_REFERENCE).addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {
                     for (DataSnapshot snapshot :
                             dataSnapshot.getChildren()) {

                         //POR CADA VUELTA DEL FOR CONSEGUIMOS UN USUARIO DE LA BB.DD
                         Usuario usuario = snapshot.getValue(Usuario.class);


                         if (usuario.getNick().equals(nick)) {
                             textViewError.setText(R.string.msg_registro_uso_email);
                             error = true;
                         }

                     }

                     //SI NO HAY ERRORES EN EL REGISTRO PASAMOS A DAR DE ALTA AL USUARIO EN LA BB.DD
                     if (!error) {
                         filePath = refStorage.child(FirebaseReferences.FOTO_PERFIL_USUARIO + "/" + correo + "_" + getFecha());
                         Log.i("fecha -> ", new Date().toString());
                         if (camara) {
                             Log.i("perfil ", "entra camara");
                             byte[] data = stream.toByteArray();
                             UploadTask uploadTask = filePath.putBytes(data);
                             uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                 @Override
                                 public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                     enlaceFotoFirebasde=taskSnapshot.getDownloadUrl();
                                     Log.i("Perfil", "foto subida de camara con el enlace " + enlaceFotoFirebasde.toString());
                                     registro();
                                 }
                             });

                         } else if (galeria) {
                             Log.i("perfil ", "entra galeria");
                           avatarIcono.setDrawingCacheEnabled(true);
                           avatarIcono.buildDrawingCache();
                           Bitmap bitmap = avatarIcono.getDrawingCache();
                           ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                           bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
                           byte [] data = byteArrayOutputStream.toByteArray();
                           UploadTask uploadTask = filePath.putBytes(data);
                           uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                   enlaceFotoFirebasde=taskSnapshot.getDownloadUrl();
                                   registro();
                               }
                           });

                         }else{
                             enlaceFotoFirebasde= Uri.parse("https://firebasestorage.googleapis.com/v0/b/series-15075.appspot.com/o/foto_perfil%2Fseries.png?alt=media&token=1e9f324b-fd5d-4f8e-97a4-bca0fffe92b5");
                             registro();
                         }

                     }

                 }
                 @Override
                 public void onCancelled(DatabaseError databaseError) {

                 }
             });
         }
        }


    }

    private void registro(){
        progressBar.setVisibility(View.VISIBLE);
        botonAvatar.setClickable(false);
        botonRegistro.setClickable(false);
        //CREAMOS UN NUEVO OBJETO DE TIPO USUARIO
        Usuario usuario = new Usuario(nick, phoneNumber, correo, enlaceFotoFirebasde.toString(), FirebaseReferences.ONLINE);
        //CONSEGUIIMOS UNA REFERENCIA AL NODO ROOT DE LA BB.DD
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        //LE AÑADIMOS UN NODO HIJO A LA REFERENCIA ANTERIOR CON CLAVE GENERADA AUTOMÁTICA (MÉTODO PUSH)
        ref.child(FirebaseReferences.USUARIOS_REFERENCE).push().setValue(usuario);
        Toast.makeText(registroActivity.this, R.string.toast_usu_reg, Toast.LENGTH_SHORT).show();
        //VAMOS A LA PANTALLA PRINCIPAL
        irAPrincipal();
        finish();
    }

    private String getFecha(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf= new SimpleDateFormat("dd_MM_yyyy_HH:mm:ss");
        return sdf.format(date);
    }




}
