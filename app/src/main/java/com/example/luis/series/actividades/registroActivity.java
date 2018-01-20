package com.example.luis.series.actividades;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luis.series.R;
import com.example.luis.series.references.FirebaseReferences;
import com.example.luis.series.Objetos.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class registroActivity extends AppCompatActivity  implements TextView.OnEditorActionListener{

 EditText editTextNick,editTextEmail;
 TextView textViewError;
    String nick,correo,phoneNumber;
    boolean error=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_activity);
        editTextNick=findViewById(R.id.editTextNick);
        editTextEmail=findViewById(R.id.editTextCorreo);
        editTextEmail.setOnEditorActionListener(this);
        textViewError=findViewById(R.id.textViewError);
        textViewError.setText("");
        Intent intent=getIntent();
        phoneNumber=intent.getStringExtra("phoneNumber");
    }

    public void registro(View view) {
        textViewError.setText("");
         nick=editTextNick.getText().toString().trim();
         correo=editTextEmail.getText().toString().trim();
         if(nick.equals("") || correo.equals("")){
             textViewError.setText("Debe rellenar todos los campos!!");
         }else if(nick.length()>15) {
             textViewError.setText("El nick no puede contener más de 15 caracteres");
         }else if(!validateEmail(correo)) {
             textViewError.setText("Formato Email incorrecto!!");
         }else{
             FirebaseDatabase database=FirebaseDatabase.getInstance();
             database.getReference(FirebaseReferences.USUARIOS_REFERENCE).addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {
                     for(DataSnapshot snapshot :
                             dataSnapshot.getChildren()){
                         Usuario usuario = snapshot.getValue(Usuario.class);
                         if(usuario.getNick().equals(nick)){
                             textViewError.setText("El nick introducido ya está en uso!!");
                             error=true;
                         }

                     }

                     if(!error){
                         Usuario usuario = new Usuario(nick,phoneNumber,correo);
                         DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                         ref.child(FirebaseReferences.USUARIOS_REFERENCE).push().setValue(usuario);
                         Toast.makeText(registroActivity.this,"Usuario registrado",Toast.LENGTH_SHORT).show();
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

    private void irAPrincipal() {
        Intent intent = new Intent(this, PrincipalActivity.class);
        startActivity(intent);
    }


    public boolean validateEmail(String email) {

        Pattern pattern;
        Matcher matcher;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();

    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

        if(i== EditorInfo.IME_ACTION_DONE){
            registro(null);

        }
        return false;
    }

    public void onBackPressed(){
        SharedPreferences sharedPref = getSharedPreferences("Preferencias",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("registroCerrado",true);
        editor.commit();
        Log.i("REGISTRO","Hemos cerrado la pantalla de registro");
        super.onBackPressed();
    }
}
