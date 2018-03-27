package com.maniac.luis.series.actividades;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.maniac.luis.series.Objetos.Comentario;
import com.maniac.luis.series.Objetos.Series;
import com.maniac.luis.series.R;
import com.maniac.luis.series.references.FirebaseReferences;
import com.maniac.luis.series.Objetos.Usuario;
import com.maniac.luis.series.utilidades.Common;
import com.maniac.luis.series.utilidades.ComunicarCurrentUser;
import com.maniac.luis.series.utilidades.ListaNumerosAgendaTelefonos;
import com.maniac.luis.series.utilidades.Utilities;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class registroActivity extends AppCompatActivity  implements TextView.OnEditorActionListener{

 private EditText editTextNick,editTextEmail;
 private TextView textViewError;
 private ProgressBar progressBar;
 private Button botonAvatar,botonRegistro;
    private ImageView avatarIcono;
    private String nick,correo,phoneNumber;
    private boolean error=false;
    private StorageReference refStorage;
    private static final int GALLERY_INTENT=4;
    private static final int CAMERA_INTENT=5;
    private static final int CAMARA=0;
    private static final int GALERIA=1;
    private static final int CANCELAR=2;
    StorageReference filePath;
    boolean camara=false;
    boolean galeria=false;
    Uri imagenSeleccionada;
    Uri enlaceFotoFirebasde;
    private  final String CARPETA_RAIZ="Series/";
    private final String RUTA_IMAGEN=CARPETA_RAIZ+"series";
    String miPath;
    List<String> phoneNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_activity);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        refStorage= FirebaseStorage.getInstance().getReference();
        phoneNumbers=new ArrayList();
        loadContactFromTlf();
        avatarIcono=findViewById(R.id.avatarIcono);
        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/series-15075.appspot.com/o/foto_perfil%2Fseries_ic.png?alt=media&token=feb3ff8f-bd8a-4848-8a42-2f4f6b72cb88")
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

        final CharSequence[] items = {getString(R.string.camara),getString(R.string.galeria),getString(R.string.cancelar)};
        AlertDialog.Builder builder = new AlertDialog.Builder(registroActivity.this);
        builder.setTitle(R.string.selec_accion);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int opcion= i;
                switch (opcion){

                    case CAMARA:
                        Log.i("perfil","Selecciona camara");
                        File fileImagen = new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
                        boolean creada=fileImagen.exists();
                        String nombreImagen="";
                        if(!creada){
                            creada=fileImagen.mkdirs();
                        }
                        if(creada){
                             nombreImagen=(System.currentTimeMillis()/1000)+".jpg";
                        }
                         miPath=Environment.getExternalStorageDirectory()+File.separator+RUTA_IMAGEN+File.separator+nombreImagen;
                        File imagen = new File(miPath);

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(imagen));
                        startActivityForResult(intent,CAMERA_INTENT);
                        break;

                    case GALERIA:
                        Log.i("perfil","Selecciona galeria");
                        Intent intent1=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent1.setType("image/*");
                        startActivityForResult(intent1.createChooser(intent1,getString(R.string.selec_app)),GALLERY_INTENT);

                        break;

                    case CANCELAR:
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
               /* Bundle bundle = data.getExtras();
                final Bitmap bmp = (Bitmap) bundle.get("data");
                Log.i("perfil","requestCode==CAMERA_INTENT");
                avatarIcono.setImageBitmap(bmp);
                stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);*/
                MediaScannerConnection.scanFile(this, new String[]{miPath}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String s, Uri uri) {
                                Log.i("Ruta de almacenamiento","Path -> " + miPath);
                            }
                        });
                //Bitmap bitmap = BitmapFactory.decodeFile(miPath);
                //avatarIcono.setImageBitmap(bitmap);
                Glide.with(this)
                        .load(miPath)
                        .centerCrop()
                        .fitCenter()
                        .into(avatarIcono);
                //stream = new ByteArrayOutputStream();
                //bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

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

                         if(usuario!=null){
                             if (usuario.getNick().equals(nick)) {
                                 textViewError.setText(R.string.msg_registro_uso_email);
                                 error = true;
                             }
                         }


                     }

                     //SI NO HAY ERRORES EN EL REGISTRO PASAMOS A DAR DE ALTA AL USUARIO EN LA BB.DD
                     if (!error) {
                         filePath = refStorage.child(FirebaseReferences.FOTO_PERFIL_USUARIO + "/" + correo + "_" + getFecha());
                         Log.i("fecha -> ", new Date().toString());
                         if (camara) {
                             Log.i("perfil ", "entra camara");
                             //byte[] data = stream.toByteArray();
                             avatarIcono.setDrawingCacheEnabled(true);
                             avatarIcono.buildDrawingCache();
                             Bitmap bitmap = avatarIcono.getDrawingCache();
                             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                             bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                             byte[] data = byteArrayOutputStream.toByteArray();
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
                             enlaceFotoFirebasde= Uri.parse("https://firebasestorage.googleapis.com/v0/b/series-15075.appspot.com/o/foto_perfil%2Fseries_ic.png?alt=media&token=feb3ff8f-bd8a-4848-8a42-2f4f6b72cb88");
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS);
        databaseReference.orderByChild(FirebaseReferences.COMENTARIO_PHONE_NUMBER).equalTo(ComunicarCurrentUser.getPhoneNumberUser()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    String claveComentario=childSnapshot.getKey();
                    Comentario comentario=childSnapshot.getValue(Comentario.class);
                    if(phoneNumbers.contains(comentario.getPhoneNumberUsuario())){
                        DatabaseReference dfr=FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS).child(claveComentario)
                                .child(FirebaseReferences.COMENTARIO_AVATAR_USUARIO);
                        dfr.setValue(enlaceFotoFirebasde.toString());
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS);
        dbreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot :
                        dataSnapshot.getChildren()){
                    String key = snapshot.getKey();
                   final DatabaseReference dataref = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS)
                            .child(key).child("liked");
                   dataref.addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {

                           Map<String,Boolean> liked = (Map<String, Boolean>) dataSnapshot.getValue();
                           liked.put(ComunicarCurrentUser.getPhoneNumberUser(),false);
                           dataref.setValue(liked);

                       }

                       @Override
                       public void onCancelled(DatabaseError databaseError) {

                       }
                   });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        progressBar.setVisibility(View.VISIBLE);
        botonAvatar.setClickable(false);
        botonRegistro.setClickable(false);
        //CREAMOS UN NUEVO OBJETO DE TIPO USUARIO
        Usuario usuario = new Usuario(nick, phoneNumber, correo, enlaceFotoFirebasde.toString(), FirebaseReferences.ONLINE,Common.PRINCIPIANTE, (long) 0, FirebaseInstanceId.getInstance().getToken());
        //CONSEGUIIMOS UNA REFERENCIA AL NODO ROOT DE LA BB.DD
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        //LE AÑADIMOS UN NODO HIJO A LA REFERENCIA ANTERIOR CON CLAVE GENERADA AUTOMÁTICA (MÉTODO PUSH)
        ref.child(FirebaseReferences.USUARIOS_REFERENCE).push().setValue(usuario);
        Toast.makeText(registroActivity.this, R.string.toast_usu_reg, Toast.LENGTH_SHORT).show();
        ref.child(FirebaseReferences.SERIES_REFERENCE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:
                        dataSnapshot.getChildren()){
                    Series serie = snapshot.getValue(Series.class);
                    Log.i("ComentariosLeidosSerie","Serie -> " + serie.getNombre());
                    DatabaseReference fiRef = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS_LEIDOS_SERIE).child(phoneNumber)
                            .child(serie.getNombre()).child(FirebaseReferences.COM_LEIDOS);
                    fiRef.setValue(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //VAMOS A LA PANTALLA PRINCIPAL
        irAPrincipal();
        finish();
    }

    private String getFecha(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf= new SimpleDateFormat("dd_MM_yyyy_HH:mm:ss");
        return sdf.format(date);
    }

    public void loadContactFromTlf() {
        ContentResolver contentResolver=this.getContentResolver();
        String [] projeccion=new String[]{ContactsContract.Data.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER};
        //String [] projeccion=new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.Contacts.DISPLAY_NAME};
        String selectionClause=ContactsContract.Data.MIMETYPE + "='" +
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND " +
                ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL";
        //String sortOrder = ContactsContract.Data.DISPLAY_NAME + " ASC";
        Cursor cursor=this.getContentResolver().query(ContactsContract.Data.CONTENT_URI,projeccion,selectionClause,null,null);
        while(cursor.moveToNext()){
            String name=cursor.getString(0);
            String phoneNumber=cursor.getString(1);
            if(phoneNumber.length()>=9){
                phoneNumber=phoneNumber.replaceAll("\\s","");
                if(phoneNumber.substring(0,3).equals("+34")){
                    phoneNumber=phoneNumber.substring(3,phoneNumber.length());
                    //Log.i("contactosTlf","numSin+34 -> " + phoneNumber);

                }
                //nombreContactosTelefono.add(name);
                // contactosTelefono.add(phoneNumber);
                Log.i("contactos","Nombre: " + name);
                Log.i("contactos","Numero: " + phoneNumber);
                phoneNumbers.add(phoneNumber);
            }

            //Log.i("contactos","Identificador: " + cursor.getString(0));


            //Log.i("contactos","Tipo: " + cursor.getString(3));
        }
        cursor.close();
    }




}
