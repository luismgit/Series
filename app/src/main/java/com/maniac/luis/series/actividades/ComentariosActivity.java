package com.maniac.luis.series.actividades;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maniac.luis.series.Adapters.AdaptadorComentarios;
import com.maniac.luis.series.Objetos.Comentario;
import com.maniac.luis.series.Objetos.Suscripcion;
import com.maniac.luis.series.Objetos.Usuario;
import com.maniac.luis.series.R;
import com.maniac.luis.series.references.FirebaseReferences;
import com.maniac.luis.series.utilidades.Common;
import com.maniac.luis.series.utilidades.ComunicarAvatarUsuario;
import com.maniac.luis.series.utilidades.ComunicarClaveUsuarioActual;
import com.maniac.luis.series.utilidades.ComunicarContactosPhoneNumber;
import com.maniac.luis.series.utilidades.ComunicarCurrentUser;
import com.maniac.luis.series.utilidades.ComunicarFondoComentarios;
import com.maniac.luis.series.utilidades.FondosGaleriaComentarios;
import com.maniac.luis.series.utilidades.ImagenesColoresSolidos;
import com.maniac.luis.series.utilidades.LinearLayoutTarget;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComentariosActivity extends AppCompatActivity {

    private static final int COLORES_SOLIDOS = 1;
    private static final int FONDOS_GALERIA = 2;
    RecyclerView rv;
    List<Comentario> comentarios;
    List<String> contactosPhoneNumber;
    AdaptadorComentarios adaptadorComentarios;
    EditText nuevoComentario;
    String nombreSerie;
    List<String> contactos;
    TextView txtSinComentarios;
    ImageView imagenSerieComentarios;
    TextView textoSerieComentarios;
    Map<String,String> agenda;
    DatabaseReference referenceABorrar;
    ValueEventListener listener;
    TextView textViewOptionsComent;
    LinearLayout linearLayout;
    Dialog myDialog;
    String [] coloresSolidos;
    List<String> listaFondosGaleria;
    String url;
    LinearLayoutManager manager;
    List<String> serie_telefono_suscripciones=new ArrayList<>();
    boolean primeraCargaRv=true;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);
        FirebaseDatabase.getInstance().goOnline();
        linearLayout=findViewById(R.id.linearLayoutComentarios);
        String fondoComentario=ComunicarFondoComentarios.getFondo();


            if(fondoComentario.length()>10){
                Glide.with(this)
                        .load(fondoComentario)
                        .asBitmap()
                        .into(new LinearLayoutTarget(this.getApplicationContext(),linearLayout));
            }else{
                linearLayout.setBackgroundColor(Color.parseColor(ComunicarFondoComentarios.getFondo()));
            }

        nombreSerie=getIntent().getStringExtra(Common.NOMBRE_SERIE_COMENTARIOS);
        contactos=new ArrayList<>();
        agenda=new HashMap<>();
        loadContactFromTlf();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        coloresSolidos=ImagenesColoresSolidos.getListaColores();
        listaFondosGaleria= FondosGaleriaComentarios.getFondos();
        myDialog=new Dialog(this);
        contactos=ComunicarContactosPhoneNumber.getPhoneNumbers();
        txtSinComentarios=findViewById(R.id.mensajeSinComentarios);
        textViewOptionsComent=findViewById(R.id.menuFondosComent);
        textoSerieComentarios=findViewById(R.id.textoSerieComentarios);
        txtSinComentarios.setVisibility(View.GONE);
        imagenSerieComentarios=findViewById(R.id.imagenSerieComentarios);
        referenceABorrar = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS_LEIDOS_SERIE)
                .child(ComunicarCurrentUser.getPhoneNumberUser()).child(nombreSerie).child(FirebaseReferences.COM_LEIDOS);
         listener=referenceABorrar.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                referenceABorrar.setValue(0);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference r = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.SERIES_REFERENCE).child(nombreSerie);
        r.child(FirebaseReferences.IMAGEN_SERIE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 url= (String) dataSnapshot.getValue();
                Glide.with(ComentariosActivity.this)
                        .load(url)
                        .centerCrop()
                        .fitCenter()
                        .into(imagenSerieComentarios);
                imagenSerieComentarios.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog miDialogo=new Dialog(ComentariosActivity.this);
                        ImageView imagen;
                        miDialogo.setContentView(R.layout.image_pop_up);
                        imagen=miDialogo.findViewById(R.id.imagenAmpliada);
                        Glide.with(ComentariosActivity.this)
                                .load(url)
                                .fitCenter()
                                .centerCrop()
                                .into(imagen);
                        miDialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        miDialogo.show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        r.child(FirebaseReferences.NOMBRE_SERIE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nombreSerie= (String) dataSnapshot.getValue();
                textoSerieComentarios.setText(nombreSerie);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        nuevoComentario=findViewById(R.id.nuevoComentario);
        rv=findViewById(R.id.recyclerComentarios);
        comentarios=new ArrayList<>();
        contactosPhoneNumber=new ArrayList<>();
        contactosPhoneNumber= ComunicarContactosPhoneNumber.getPhoneNumbers();
        DatabaseReference fbref=FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS_LEIDOS_SERIE).child(ComunicarCurrentUser.getPhoneNumberUser())
                .child(nombreSerie).child(FirebaseReferences.COM_LEIDOS);
        fbref.setValue(0);
        manager = new LinearLayoutManager(this);
        rv.setLayoutManager(manager);
        adaptadorComentarios=new AdaptadorComentarios(comentarios,this,agenda);
        rv.setAdapter(adaptadorComentarios);
        rv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) scrollToBottom();
            }
        });


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comentarios.removeAll(comentarios);
                for(DataSnapshot snapshot:
                        dataSnapshot.getChildren()){
                    Comentario com = snapshot.getValue(Comentario.class);
                    if(contactosPhoneNumber.contains(com.getPhoneNumberUsuario()) && com.getSerie().equals(nombreSerie)
                            && com.getLiked().containsKey(ComunicarCurrentUser.getPhoneNumberUser())){
                        comentarios.add(com);
                        com.setTipo(Comentario.ComentarioType.OTHER_USERS);
                        }


                    if(com.getPhoneNumberUsuario().equals(ComunicarCurrentUser.getPhoneNumberUser()) && com.getSerie().equals(nombreSerie)){
                        com.setTipo(Comentario.ComentarioType.USER_PROP);
                        comentarios.add(com);
                    }


                }
                if(comentarios.size()==0){
                    txtSinComentarios.setVisibility(View.VISIBLE);
                }else{
                    txtSinComentarios.setVisibility(View.GONE);
                }

                adaptadorComentarios.notifyDataSetChanged();
                if(primeraCargaRv){
                    rv.scrollToPosition(comentarios.size()-1);
                    primeraCargaRv=false;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        referenceABorrar.removeEventListener(listener);
    }

    public void enviarNuevoComentario(View view) {
        DatabaseReference drefF= FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.SUSCRIPCIONES);
        drefF.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:
                        dataSnapshot.getChildren()){
                    Suscripcion suscripcion = snapshot.getValue(Suscripcion.class);
                    serie_telefono_suscripciones.add(suscripcion.getTlf_serie());
                }
                String coment=nuevoComentario.getText().toString().trim();
                if(coment.equals("")){
                    Toast.makeText(ComentariosActivity.this, R.string.debe_comentar,Toast.LENGTH_SHORT).show();
                }else{
                    Map<String,Boolean> liked = new HashMap<>();
                    List<String> numeroContactos=new ArrayList<>();
                    numeroContactos=ComunicarContactosPhoneNumber.getPhoneNumbers();
                    for (int i = 0; i < numeroContactos.size(); i++) {
                        liked.put(numeroContactos.get(i),false);
                    }
                    liked.put("prueba",false);
                    Comentario comentario = new Comentario(nuevoComentario.getText().toString(), ComunicarAvatarUsuario.getAvatarUsuario()
                            ,nombreSerie, ComunicarCurrentUser.getPhoneNumberUser(),liked,Comentario.ComentarioType.OTHER_USERS,"", (long) 0,getFecha());
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS);
                    String key = databaseReference.push().getKey();
                    databaseReference.child(key).setValue(comentario);
                    DatabaseReference refdata=FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS).child(key).child("keyFB");
                    refdata.setValue(key);
                    nuevoComentario.setText("");
                    rv.scrollToPosition(comentarios.size()-1);
                    esconderTeclado();
                    final DatabaseReference dtRef=FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS_LEIDOS_SERIE);
                    dtRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            contactos=ComunicarContactosPhoneNumber.getPhoneNumbers();
                            for(DataSnapshot snapshot:
                                    dataSnapshot.getChildren()){
                                final String phoneNumber = snapshot.getKey();

                                if(!phoneNumber.equals(ComunicarCurrentUser.getPhoneNumberUser()) && contactos.contains(phoneNumber) &&
                                        serie_telefono_suscripciones.contains(phoneNumber+"_"+nombreSerie)){

                                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS_LEIDOS_SERIE)
                                            .child(phoneNumber).child(nombreSerie).child(FirebaseReferences.COM_LEIDOS);
                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Long sinLeer= (Long) dataSnapshot.getValue();
                                            sinLeer++;
                                            ref.setValue(sinLeer);
                                            DatabaseReference refDarta = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.USUARIOS_REFERENCE);
                                                   refDarta .orderByChild(FirebaseReferences.TELEFONO).equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                                                       @Override
                                                       public void onDataChange(DataSnapshot dataSnapshot) {
                                                           for(DataSnapshot snapshot1:
                                                                   dataSnapshot.getChildren()){
                                                               Usuario user = snapshot1.getValue(Usuario.class);
                                                           }
                                                       }

                                                       @Override
                                                       public void onCancelled(DatabaseError databaseError) {

                                                       }
                                                   });
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }

                                    });
                                }
                            }
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




    }

    private String getFecha(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf= new SimpleDateFormat("dd/MM HH:mm");
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
                }
                agenda.put(phoneNumber,name);
            }

        }
        cursor.close();
    }


    public void menuCambiaFondo(View view) {
        @SuppressLint("RestrictedApi") Context wrapper = new ContextThemeWrapper(this, R.style.ThemeOverlay_MyTheme);
        PopupMenu popupMenu = new PopupMenu(wrapper,textViewOptionsComent);
        popupMenu.inflate(R.menu.menu_comentarios);
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_item_fondo_coment:
                        abrirPopMenuElegirFondo();
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void abrirPopMenuElegirFondo() {
        ImageView iconoColorSolido,iconoGaleria;
        myDialog.setContentView(R.layout.elegir_fondo_pop_up);
        iconoColorSolido=(ImageView) myDialog.findViewById(R.id.iconoColorSolido);
        iconoGaleria=(ImageView)myDialog.findViewById(R.id.iconoGaleriaFondos);
        iconoColorSolido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ComentariosActivity.this,ListaFondosColorSolido.class);
                startActivityForResult(intent,COLORES_SOLIDOS);
            }
        });
        iconoGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ComentariosActivity.this,ListaFondosGaleria.class);
                startActivityForResult(intent,FONDOS_GALERIA);
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){

            case COLORES_SOLIDOS:
                myDialog.dismiss();
                if(data != null && resultCode == RESULT_OK){
                    int numFondo = data.getIntExtra(Common.FONDO_SELECCIONADO,-1);

                    if(numFondo != -1){
                        linearLayout.setBackgroundColor(Color.parseColor(coloresSolidos[numFondo]));
                        ComunicarFondoComentarios.setFondo(coloresSolidos[numFondo]);
                        DatabaseReference referenceBBDD=FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.USUARIOS_REFERENCE).child(ComunicarClaveUsuarioActual.getClave())
                                .child(FirebaseReferences.FONDO_COMENTARIO);
                        referenceBBDD.setValue(coloresSolidos[numFondo]);
                    }
                }
                break;

            case FONDOS_GALERIA:
                myDialog.dismiss();
                if(data != null && resultCode == RESULT_OK){
                    int numFondo = data.getIntExtra(Common.FONDO_SELECCIONADO,-1);

                    if(numFondo != -1){
                        Glide.with(this)
                                .load(listaFondosGaleria.get(numFondo))
                                .asBitmap()
                                .into(new LinearLayoutTarget(this.getApplicationContext(),linearLayout));
                        ComunicarFondoComentarios.setFondo(listaFondosGaleria.get(numFondo));
                        DatabaseReference referenceBBDD=FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.USUARIOS_REFERENCE).child(ComunicarClaveUsuarioActual.getClave())
                                .child(FirebaseReferences.FONDO_COMENTARIO);
                        referenceBBDD.setValue(listaFondosGaleria.get(numFondo));
                    }
                }
                break;

        }
    }


    //MÉTODO QUE ESCONDE EL TECLADO
    private void esconderTeclado(){

        InputMethodManager teclado = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        teclado.hideSoftInputFromWindow(nuevoComentario.getWindowToken(), 0);
    }

    private void scrollToBottom() {
        manager.smoothScrollToPosition(rv, null, adaptadorComentarios.getItemCount());
    }
}
