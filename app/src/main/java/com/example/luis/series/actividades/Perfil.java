package com.example.luis.series.actividades;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.luis.series.Objetos.Usuario;
import com.example.luis.series.R;
import com.example.luis.series.references.FirebaseReferences;
import com.example.luis.series.utilidades.ComunicarClaveUsuarioActual;
import com.example.luis.series.utilidades.ComunicarCurrentUser;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Perfil extends AppCompatActivity {

    ImageView imagenUsuario;
    EditText editTextEmailUsuario;
    private static final int GALLERY_INTENT=4;
    private static final int CAMERA_INTENT=5;
    private static final int CAMARA=0;
    private static final int GALERIA=1;
    private static final int CANCELAR=2;
    private boolean camara = false, galeria = false, cambios = false;
    private Uri imagenSeleccionada, enlaceFotoFirebase;
    private StorageReference filePath;
    private StorageReference storageReference;
    private Usuario user;
    Button botonModificar;
    private  final String CARPETA_RAIZ="Series/";
    private final String RUTA_IMAGEN=CARPETA_RAIZ+"series";
    String miPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        botonModificar = findViewById(R.id.botonModificar);
        storageReference = FirebaseStorage.getInstance().getReference();
        imagenUsuario = findViewById(R.id.fotoPerfil);
        editTextEmailUsuario = findViewById(R.id.editTextEmail);
        user = (Usuario) getIntent().getSerializableExtra("usuario");
        Log.i("avatar", "-> " + user.getAvatar());
        Glide.with(this)
                .load(user.getAvatar())
                .fitCenter()
                .centerCrop()
                .into(imagenUsuario);
        editTextEmailUsuario.setText(user.getCorreo());
        editTextEmailUsuario.setSelection(editTextEmailUsuario.getText().length());
        imagenUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final CharSequence[] items = {getString(R.string.camara),getString(R.string.galeria),getString(R.string.cancelar)};
                AlertDialog.Builder builder = new AlertDialog.Builder(Perfil.this);
                builder.setTitle(R.string.selec_accion);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int opcion= i;
                        switch (opcion) {

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
                                Log.i("perfil", "Selecciona galeria");
                                Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == CAMERA_INTENT) {
                camara = true;
                galeria = false;
                cambios = true;
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
                        .into(imagenUsuario);
               // stream = new ByteArrayOutputStream();
               // bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);


            } else if (requestCode == GALLERY_INTENT) {
                galeria = true;
                camara = false;
                cambios = true;
                Log.i("perfil", "requestCode==GALLERY_INTENT");
                imagenSeleccionada = data.getData();
                Log.i("perfil", "Uri -> " + imagenSeleccionada.toString());
                Glide.with(Perfil.this)
                        .load(data.getData())
                        .centerCrop()
                        .fitCenter()
                        .into(imagenUsuario);
            }
        }
    }


    public void modificarPerfil(View view) {
        botonModificar.setClickable(false);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
        animation.setDuration(1000);
        botonModificar.setAnimation(animation);
        filePath = storageReference.child(FirebaseReferences.FOTO_PERFIL_USUARIO + "/" + editTextEmailUsuario.getText().toString() + "_" + getFecha());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child(FirebaseReferences.USUARIOS_REFERENCE).child(ComunicarClaveUsuarioActual.getClave())
                .child(FirebaseReferences.CORREO_USUARIO);
        ref.setValue(editTextEmailUsuario.getText().toString());

        if (camara) {
            Log.i("perfil ", "entra camara");
           // byte[] data = stream.toByteArray();
            imagenUsuario.setDrawingCacheEnabled(true);
            imagenUsuario.buildDrawingCache();
            Bitmap bitmap = imagenUsuario.getDrawingCache();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    enlaceFotoFirebase = taskSnapshot.getDownloadUrl();
                    Log.i("Perfil", "foto subida de camara con el enlace " + enlaceFotoFirebase.toString());
                    modificarFotoPerfil();
                }
            });

        } else if (galeria) {
            Log.i("perfil ", "entra galeria");
            imagenUsuario.setDrawingCacheEnabled(true);
            imagenUsuario.buildDrawingCache();
            Bitmap bitmap = imagenUsuario.getDrawingCache();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    enlaceFotoFirebase = taskSnapshot.getDownloadUrl();
                    Log.i("Perfil", "foto subida de la galeria con el enlace " + enlaceFotoFirebase.toString());
                    modificarFotoPerfil();
                }
            });
        } else {
            Toast.makeText(this, R.string.perfil_mod, Toast.LENGTH_SHORT).show();
            cambios = false;
            botonModificar.setClickable(true);
            //finish();
        }
    }

    private void modificarFotoPerfil() {

        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getAvatar());
        Log.i("NoBorrar"," " + photoRef.getName());
        Log.i("NoBorrar"," " + photoRef.toString());
        if(!photoRef.getName().equals("series_ic.png")){
            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Log.i("Perfil", "foto borrada correctamente");

                }
            });
        }
        FirebaseDatabase d = FirebaseDatabase.getInstance();
        DatabaseReference refef = d.getReference().child(FirebaseReferences.USUARIOS_REFERENCE).child(ComunicarClaveUsuarioActual.getClave())
                .child(FirebaseReferences.AVATAR);
        refef.setValue(enlaceFotoFirebase.toString());
        Toast.makeText(Perfil.this,  R.string.perfil_mod, Toast.LENGTH_SHORT).show();
        cambios = false;
        botonModificar.setClickable(true);
        //finish();





    }

    private String getFecha() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH:mm:ss");
        return sdf.format(date);
    }

    @Override
    public void onBackPressed() {
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        final DatabaseReference root = data.getReference();
        root.child(FirebaseReferences.USUARIOS_REFERENCE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot :
                                dataSnapshot.getChildren()) {
                            Usuario usu = snapshot.getValue(Usuario.class);
                            if (usu.getTelefono().equals(ComunicarCurrentUser.getPhoneNumberUser())) {
                                user = usu;
                            }

                        }
                        if (!user.getCorreo().equals(editTextEmailUsuario.getText().toString())) {
                            cambios = true;
                        }
                        if (cambios) {
                            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Perfil.this);
                            dialogo1.setTitle(R.string.importante);
                            dialogo1.setMessage(R.string.guardarcambios);
                            dialogo1.setCancelable(false);
                            dialogo1.setPositiveButton(R.string.confirmar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    modificarPerfil(null);
                                    finish();
                                }
                            });
                            dialogo1.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    finish();
                                }
                            });
                            dialogo1.show();
                        } else {
                            finish();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }


    public void volver(View view) {
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        final DatabaseReference root = data.getReference();
        root.child(FirebaseReferences.USUARIOS_REFERENCE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot :
                                dataSnapshot.getChildren()) {
                            Usuario usu = snapshot.getValue(Usuario.class);
                            if (usu.getTelefono().equals(ComunicarCurrentUser.getPhoneNumberUser())) {
                                user = usu;
                            }

                        }
                        if (!user.getCorreo().equals(editTextEmailUsuario.getText().toString())) {
                            cambios = true;
                        }
                        if (cambios) {
                            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Perfil.this);
                            dialogo1.setTitle(R.string.importante);
                            dialogo1.setMessage(R.string.guardarcambios);
                            dialogo1.setCancelable(false);
                            dialogo1.setPositiveButton(R.string.confirmar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    modificarPerfil(null);
                                    finish();
                                }
                            });
                            dialogo1.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    finish();
                                }
                            });
                            dialogo1.show();
                        } else {
                            finish();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
