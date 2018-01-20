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
import android.widget.ImageView;
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
    private ImageView avatarIcono;
    int iconoSeleccionado;
    String nick,correo,phoneNumber;
    private int [] iconos = new int[]
            {       R.drawable.icono0,
                    R.drawable.icono5,
                    R.drawable.icono6,
                    R.drawable.icono7,
                    R.drawable.icono8,
                    R.drawable.icono9,
                    R.drawable.icono10,
                    R.drawable.icono11,
                    R.drawable.icono12,
                    R.drawable.icono13,
                    R.drawable.icono14,
                    R.drawable.icono15,
                    R.drawable.icono16,
                    R.drawable.icono17};
    private static final int LISTA_ICONOS=1;
    boolean error=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_activity);
        avatarIcono=findViewById(R.id.avatarIcono);
        avatarIcono.setImageResource(iconos[0]);
        iconoSeleccionado=0;
        editTextNick=findViewById(R.id.editTextNick);
        editTextEmail=findViewById(R.id.editTextCorreo);
        editTextEmail.setOnEditorActionListener(this);
        textViewError=findViewById(R.id.textViewError);
        textViewError.setText("");
        Intent intent=getIntent();
        phoneNumber=intent.getStringExtra("phoneNumber");
    }

    public void registro(View view) {
        if(iconoSeleccionado == -1){
            iconoSeleccionado=0;
        }
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
                         Usuario usuario = new Usuario(nick,phoneNumber,correo,iconoSeleccionado);
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
            //registro(null);

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

    public void seleccionarAvatar(View view) {
        //MÉTODO QUE ABRIRÁ LA PANTALLA DE SELECCIÓN DE AVATARES DE ListaIconos ESPERANDO UN RESULTADO
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

                //SI LOS DATOS NO SON NULOS Y SE HA HECHO UNA MODIFICACIÓN DEL PASSWORD DEL ADIMISTRADOR
                if(data != null && resultCode == RESULT_OK){
                    Log.i("OnActivityResult","Numero de icono -> " + data.getIntExtra("ICONOSELECCIONADO",-1));

                    //GUARDAMOS EN numIcono EL NºDE AVATAR(VISTA) SELECCIONADA
                    int numIcono = data.getIntExtra("ICONOSELECCIONADO",-1);
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


}
