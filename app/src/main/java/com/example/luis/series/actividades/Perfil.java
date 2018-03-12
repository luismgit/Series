package com.example.luis.series.actividades;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.text.SimpleDateFormat;
import java.util.Date;

public class Perfil extends AppCompatActivity {

    ImageView imagenUsuario;
    EditText editTextEmailUsuario;
    private static final int GALLERY_INTENT = 1;
    private static final int CAMERA_INTENT = 2;
    private boolean camara = false, galeria = false, cambios = false;
    private ByteArrayOutputStream stream;
    private Uri imagenSeleccionada, enlaceFotoFirebase;
    private StorageReference filePath;
    private StorageReference storageReference;
    private Usuario user;
    Button botonModificar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        botonModificar = findViewById(R.id.botonModificar);
        storageReference = FirebaseStorage.getInstance().getReference();
        imagenUsuario = findViewById(R.id.fotoPerfil);
        editTextEmailUsuario = findViewById(R.id.editTextEmail);
        //String emailUsuario= getIntent().getStringExtra(TabActivity.EMAIL_USUARIO);
        //String fotoUsuario=getIntent().getStringExtra(TabActivity.FOTO_USUARIO);
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


                final CharSequence[] items = {"Cámara", "Galería", "Cancelar"};
                AlertDialog.Builder builder = new AlertDialog.Builder(Perfil.this);
                builder.setTitle("Selecciona un foto de perfil");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String opcion = (String) items[i];
                        switch (opcion) {

                            case "Cámara":
                                Log.i("perfil", "Selecciona camara");

                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, CAMERA_INTENT);
                                break;

                            case "Galería":
                                Log.i("perfil", "Selecciona galeria");
                                Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent1.setType("image/*");
                                startActivityForResult(intent1.createChooser(intent1, "Selecciona foto"), GALLERY_INTENT);

                                break;

                            case "Cancelar":
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
                Bundle bundle = data.getExtras();
                final Bitmap bmp = (Bitmap) bundle.get("data");
                Log.i("perfil", "requestCode==CAMERA_INTENT");
                imagenUsuario.setImageBitmap(bmp);
                stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);


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

        filePath = storageReference.child(FirebaseReferences.FOTO_PERFIL_USUARIO + "/" + editTextEmailUsuario.getText().toString() + "_" + getFecha());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child(FirebaseReferences.USUARIOS_REFERENCE).child(ComunicarClaveUsuarioActual.getClave())
                .child(FirebaseReferences.CORREO_USUARIO);
        ref.setValue(editTextEmailUsuario.getText().toString());

        if (camara) {
            Log.i("perfil ", "entra camara");
            byte[] data = stream.toByteArray();
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
            Toast.makeText(this, "Perfil modificado correctamente", Toast.LENGTH_SHORT).show();
            cambios = false;
            botonModificar.setClickable(true);
            //finish();
        }
    }

    private void modificarFotoPerfil() {

        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getAvatar());
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Log.i("Perfil", "foto borrada correctamente");
                FirebaseDatabase d = FirebaseDatabase.getInstance();
                DatabaseReference refef = d.getReference().child(FirebaseReferences.USUARIOS_REFERENCE).child(ComunicarClaveUsuarioActual.getClave())
                        .child(FirebaseReferences.AVATAR);
                refef.setValue(enlaceFotoFirebase.toString());
                Toast.makeText(Perfil.this, "Perfil modificado correctamente", Toast.LENGTH_SHORT).show();
                cambios = false;
                botonModificar.setClickable(true);
                //finish();
            }
        });


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
                            dialogo1.setTitle("Importante");
                            dialogo1.setMessage("¿Desea guardar los cambios?");
                            dialogo1.setCancelable(false);
                            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    modificarPerfil(null);
                                    finish();
                                }
                            });
                            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
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
                            dialogo1.setTitle("Importante");
                            dialogo1.setMessage("¿Desea guardar los cambios?");
                            dialogo1.setCancelable(false);
                            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    modificarPerfil(null);
                                    finish();
                                }
                            });
                            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
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
