package com.maniac.luis.series.actividades;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.maniac.luis.series.Objetos.Usuario;
import com.maniac.luis.series.R;
import com.maniac.luis.series.references.FirebaseReferences;
import com.maniac.luis.series.utilidades.Common;
import com.maniac.luis.series.utilidades.ComunicarAvatarUsuario;
import com.maniac.luis.series.utilidades.ComunicarClaveUsuarioActual;
import com.maniac.luis.series.utilidades.ComunicarCurrentUser;
import com.maniac.luis.series.utilidades.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Thread.sleep;

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
    ProgressBar cargaFotoPerfil;
    ProgressBar cargaPerfil;
    TextView textoEmailIncorrecto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        FirebaseDatabase.getInstance().goOnline();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        botonModificar = findViewById(R.id.botonModificar);
        storageReference = FirebaseStorage.getInstance().getReference();
        imagenUsuario = findViewById(R.id.fotoPerfil);
        editTextEmailUsuario = findViewById(R.id.editTextEmail);
        textoEmailIncorrecto=findViewById(R.id.textoEmailIncorrecto);
        cargaFotoPerfil=findViewById(R.id.cargaFotoPerfil);
        cargaPerfil=findViewById(R.id.cargaPerfil);
        user = (Usuario) getIntent().getSerializableExtra("usuario");
        Glide.with(this)
                .load(user.getAvatar())
                .fitCenter()
                .centerCrop()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        cargaFotoPerfil.setVisibility(View.GONE);
                        Glide.with(Perfil.this)
                                .load(user.getAvatar())
                                .fitCenter()
                                .centerCrop()
                                .into(imagenUsuario);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        cargaFotoPerfil.setVisibility(View.GONE);
                        return false;
                    }
                })
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
                            }
                        });
                Glide.with(this)
                        .load(miPath)
                        .centerCrop()
                        .fitCenter()
                        .into(imagenUsuario);

            } else if (requestCode == GALLERY_INTENT) {
                galeria = true;
                camara = false;
                cambios = true;
                imagenSeleccionada = data.getData();
                Glide.with(Perfil.this)
                        .load(data.getData())
                        .centerCrop()
                        .fitCenter()
                        .into(imagenUsuario);
            }
        }
    }


    public void modificarPerfil(View view) {

        if(Utilities.validateEmail(editTextEmailUsuario.getText().toString())){
            textoEmailIncorrecto.setVisibility(View.GONE);
            botonModificar.setClickable(false);
            cargaPerfil.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
            animation.setDuration(1000);
            botonModificar.setAnimation(animation);
            filePath = storageReference.child(FirebaseReferences.FOTO_PERFIL_USUARIO + "/" + editTextEmailUsuario.getText().toString() + "_" + getFecha());

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference().child(FirebaseReferences.USUARIOS_REFERENCE).child(ComunicarClaveUsuarioActual.getClave())
                    .child(FirebaseReferences.CORREO_USUARIO);
            ref.setValue(editTextEmailUsuario.getText().toString());

            if (camara) {
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
                        modificarFotoPerfil();
                    }
                });

            } else if (galeria) {
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
                        modificarFotoPerfil();
                    }
                });
            } else {
                cargaPerfil.setVisibility(View.GONE);
                Toast.makeText(this, R.string.perfil_mod, Toast.LENGTH_SHORT).show();
                cambios = false;
                botonModificar.setClickable(true);
            }
        }else{
            textoEmailIncorrecto.setVisibility(View.VISIBLE);
        }


    }

    private void modificarFotoPerfil() {

        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getAvatar());
        if(!photoRef.getName().equals(Common.NOMBRE_AVATAR_DEFECTO)){
            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }
        FirebaseDatabase d = FirebaseDatabase.getInstance();
        DatabaseReference refef = d.getReference().child(FirebaseReferences.USUARIOS_REFERENCE).child(ComunicarClaveUsuarioActual.getClave())
                .child(FirebaseReferences.AVATAR);
        refef.setValue(enlaceFotoFirebase.toString());
        ComunicarAvatarUsuario.setAvatarUsuario(enlaceFotoFirebase.toString());
        cargaPerfil.setVisibility(View.GONE);
        Toast.makeText(Perfil.this,  R.string.perfil_mod, Toast.LENGTH_SHORT).show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS);
                databaseReference.orderByChild(FirebaseReferences.COMENTARIO_PHONE_NUMBER).equalTo(ComunicarCurrentUser.getPhoneNumberUser()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                            String claveComentario=childSnapshot.getKey();
                            DatabaseReference dfr=FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS).child(claveComentario)
                                    .child(FirebaseReferences.COMENTARIO_AVATAR_USUARIO);
                                    dfr.setValue(enlaceFotoFirebase.toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        cambios = false;
        botonModificar.setClickable(true);

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
                                    if(Utilities.validateEmail(editTextEmailUsuario.getText().toString())){
                                        textoEmailIncorrecto.setVisibility(View.GONE);
                                        modificarPerfil(null);
                                        new Esperar().execute();
                                    }else{
                                        textoEmailIncorrecto.setVisibility(View.VISIBLE);
                                    }
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
                                    if(Utilities.validateEmail(editTextEmailUsuario.getText().toString())){
                                        textoEmailIncorrecto.setVisibility(View.GONE);
                                        modificarPerfil(null);
                                        new Esperar().execute();
                                    }else{
                                        textoEmailIncorrecto.setVisibility(View.VISIBLE);
                                    }

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

    private class Esperar extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            cargaPerfil.setVisibility(View.VISIBLE);
            try {
                sleep(2000);
            } catch (InterruptedException e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
        }
    }
}
