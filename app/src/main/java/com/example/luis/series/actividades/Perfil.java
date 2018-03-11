package com.example.luis.series.actividades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.luis.series.R;

public class Perfil extends AppCompatActivity {

    ImageView imagenUsuario;
    EditText editTextEmailUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        imagenUsuario=findViewById(R.id.fotoPerfil);
        editTextEmailUsuario=findViewById(R.id.editTextEmail);
        String emailUsuario= getIntent().getStringExtra(TabActivity.EMAIL_USUARIO);
        String fotoUsuario=getIntent().getStringExtra(TabActivity.FOTO_USUARIO);
        Glide.with(this)
                .load(fotoUsuario)
                .fitCenter()
                .centerCrop()
                .into(imagenUsuario);
        editTextEmailUsuario.setText(emailUsuario);
        editTextEmailUsuario.setSelection(editTextEmailUsuario.getText().length());

    }
}
