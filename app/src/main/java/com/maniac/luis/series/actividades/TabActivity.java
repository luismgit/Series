package com.maniac.luis.series.actividades;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.maniac.luis.series.Objetos.Series;
import com.maniac.luis.series.Objetos.Suscripcion;
import com.maniac.luis.series.Objetos.Usuario;
import com.maniac.luis.series.R;
import com.maniac.luis.series.fragments.ContactosFragment;
import com.maniac.luis.series.fragments.FavoritosFragment;
import com.maniac.luis.series.fragments.SeriesFragment;
import com.maniac.luis.series.references.FirebaseReferences;
import com.maniac.luis.series.utilidades.Common;
import com.maniac.luis.series.utilidades.ComunicarAvatarUsuario;
import com.maniac.luis.series.utilidades.ComunicarClaveUsuarioActual;
import com.maniac.luis.series.utilidades.ComunicarCorreoUsuario;
import com.maniac.luis.series.utilidades.ComunicarCurrentUser;
import com.maniac.luis.series.utilidades.ComunicarFondoComentarios;
import com.maniac.luis.series.utilidades.FondosGaleriaComentarios;
import com.maniac.luis.series.utilidades.Imagenes;
import com.maniac.luis.series.utilidades.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import me.leolin.shortcutbadger.ShortcutBadger;

public class TabActivity extends AppCompatActivity implements ContactosFragment.OnFragmentInteractionListener,
SeriesFragment.OnFragmentInteractionListener,FavoritosFragment.OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;


    private ViewPager mViewPager;
    private int [] fondos;
    ImageView fondo;
    Session session;
    EditText editTextMensajeAyuda;
    EditText correoUsuAyuda;
    String emailAyuda;
    String passwordAyuda;
    List<String> listaFondos;
    List<String> listaFondosGaleriaComentarios;
    private static final int LISTA_FONDOS=2;
    public static final String EMAIL_USUARIO="emailUsuario";
    public static final String FOTO_USUARIO="fotoUsuario";
     int contador=0;
     int aleatorio=0;
     Long comentariosTotales;
     List<String> suscripcionesUsuario;
    SearchView searchView = null;
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        collapsingToolbarLayout=findViewById(R.id.collapse_toolbar);
        appBarLayout=findViewById(R.id.appBarLayout);
        comentariosTotales= Long.valueOf(0);
        suscripcionesUsuario=new ArrayList<>();
        fondo = findViewById(R.id.header);
        fondo.setImageResource(R.drawable.series_back);
        listaFondos = new ArrayList<>();

        //CONTAMOS CUANTOS FONDOS DE PANTALLA HAY EN LA BB.DD Y LOS CARGAMOS LOS ENLACES EN UNA LISTA, COGEMOS UNO DE ELLOS ALEATORIO.
        FirebaseDatabase datab = FirebaseDatabase.getInstance();
        datab.getReference(FirebaseReferences.COMMON).child(FirebaseReferences.FONDOS_SERIES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot :
                        dataSnapshot.getChildren()) {
                    contador++;
                    String uriFondo = (String) snapshot.getValue();
                    listaFondos.add(uriFondo);
                }
                aleatorio = (int) Math.floor(Math.random() * ((listaFondos.size()) - 0) + 0);

                //A TRAVÉS DE LA LIBRERIA GLIDE CARGAMOS CARGAMOS EL ENLACE DE FIREBASE STORAGE EN EL IMAGEVIEW
                Glide.with(TabActivity.this)
                        .load(listaFondos.get(aleatorio))
                        .into(fondo);
                Imagenes.setListaFondos(listaFondos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listaFondosGaleriaComentarios=new ArrayList<>();
        FirebaseDatabase databa = FirebaseDatabase.getInstance();
        databa.getReference(FirebaseReferences.COMMON).child(FirebaseReferences.FONDOS_GALERIA_COMENTARIOS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot :
                        dataSnapshot.getChildren()){
                    String fondo = (String) snapshot.getValue();
                    listaFondosGaleriaComentarios.add(fondo);
                }
                FondosGaleriaComentarios.setFondos(listaFondosGaleriaComentarios);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        //GUARDAMOS EN PREFERENCIAS QUE EL USUARIO HA LLEGADO A ESTA ACTIVITY.
        SharedPreferences sharedPref = getSharedPreferences(Common.PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(Common.REGISTRO_CERRADO, false);
        editor.commit();

        String telefono = "";
        telefono = getIntent().getStringExtra(Common.TELEFONO);
        if (telefono != null) {
            ComunicarCurrentUser.setPhoneNumber(telefono);
        } else {
            telefono = ComunicarCurrentUser.getPhoneNumberUser();
            if (telefono.substring(0, 3).equals("+34")) {
                telefono = telefono.substring(3, telefono.length());
            }
        }

        DatabaseReference re = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.SUSCRIPCIONES);
        re.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:
                        dataSnapshot.getChildren()){
                    Suscripcion suscripcion=  snapshot.getValue(Suscripcion.class);
                    if(suscripcion.getTelefono().equals(ComunicarCurrentUser.getPhoneNumberUser())){

                        suscripcionesUsuario.add(suscripcion.getSerie());
                    }
                }

                final DatabaseReference fiDa = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS_LEIDOS_SERIE).child(ComunicarCurrentUser.getPhoneNumberUser());
                fiDa.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        comentariosTotales= Long.valueOf(0);
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            String key = snapshot.getKey();
                            if(suscripcionesUsuario.contains(key)){

                                Long com = (Long) snapshot.child(FirebaseReferences.COM_LEIDOS).getValue();
                                comentariosTotales=comentariosTotales+com;
                            }

                        }
                        if(comentariosTotales!=0){
                            try{
                                ShortcutBadger.applyCount(TabActivity.this, (int) (long) comentariosTotales); //for 1.1.4+
                            }catch (Exception e){

                            }


                        }else{
                            try{
                                ShortcutBadger.removeCount(TabActivity.this); //for 1.1.4+
                            }catch (Exception e){

                            }

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




            //LISTENER QUE GUARDA EN NUESTRA CLASE ComunicarClaveUsuarioActual EL TELÉFONO DEL USUARIO ACTUAL PARA ACCEDER A ÉL EN LAS DEMÁS ACTIVIDADES

            FirebaseDatabase data = FirebaseDatabase.getInstance();
            DatabaseReference root = data.getReference();
            root.child(FirebaseReferences.USUARIOS_REFERENCE).orderByChild(FirebaseReferences.PHONE_REFERENCE).equalTo(telefono).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (final DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String claveUsuarioActual = childSnapshot.getKey();
                        ComunicarClaveUsuarioActual.setClave(claveUsuarioActual);
                        DatabaseReference referencebbdd=FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.USUARIOS_REFERENCE).child(ComunicarClaveUsuarioActual.getClave())
                                .child(FirebaseReferences.FONDO_COMENTARIO);
                        referencebbdd.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String fondo = (String) dataSnapshot.getValue();
                                ComunicarFondoComentarios.setFondo(fondo);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.USUARIOS_REFERENCE).child(claveUsuarioActual);
                        databaseReference.child("token").setValue(FirebaseInstanceId.getInstance().getToken());
                        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.USUARIOS_REFERENCE).child(claveUsuarioActual)
                                .child(FirebaseReferences.CORREO_USUARIO);
                        dref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String correo = (String) dataSnapshot.getValue();
                                ComunicarCorreoUsuario.setCorreoUsuario(correo);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        DatabaseReference drefer = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.USUARIOS_REFERENCE).child(claveUsuarioActual)
                                .child(FirebaseReferences.AVATAR);
                        drefer.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String avatar = (String) dataSnapshot.getValue();
                                ComunicarAvatarUsuario.setAvatarUsuario(avatar);
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

            //SACAMOS DE LA BB.DD EL EMAIL Y PASSWORD DE LA CUENTA DE CORREO PARA ENVIAR UN CORREO SI EL USUARIO PULSA EN EL MENÚ DE CONTACTO
            FirebaseDatabase fdb = FirebaseDatabase.getInstance();
            DatabaseReference dr = fdb.getReference();
            dr.child(FirebaseReferences.COMMON).child(FirebaseReferences.EMAIL_AYUDA).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    emailAyuda = (String) dataSnapshot.getValue();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            FirebaseDatabase f = FirebaseDatabase.getInstance();
            DatabaseReference r = f.getReference();
            r.child(FirebaseReferences.COMMON).child(FirebaseReferences.PASSWORD_AYUDA).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    passwordAyuda = (String) dataSnapshot.getValue();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            String accion = getIntent().getStringExtra(Common.NOTIFICACION);
            if (accion != null) {
                if (accion.equals(Common.NOTIFICACION)) {
                    mViewPager.setCurrentItem(1);
                    FirebaseDatabase databas = FirebaseDatabase.getInstance();
                    DatabaseReference roote = databas.getReference();
                    roote.child(FirebaseReferences.USUARIOS_REFERENCE).orderByChild(FirebaseReferences.PHONE_REFERENCE).equalTo(telefono).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (final DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                String claveUsuarioActual = childSnapshot.getKey();
                                ComunicarClaveUsuarioActual.setClave(claveUsuarioActual);
                                DatabaseReference referencebbdd=FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.USUARIOS_REFERENCE).child(ComunicarClaveUsuarioActual.getClave())
                                        .child(FirebaseReferences.FONDO_COMENTARIO);
                                referencebbdd.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String fondo = (String) dataSnapshot.getValue();
                                        ComunicarFondoComentarios.setFondo(fondo);
                                        String nombreSerie = getIntent().getStringExtra(Common.NOMBRE_SERIE_COMENTARIOS);
                                        Intent intent = new Intent(TabActivity.this, ComentariosActivity.class);
                                        intent.putExtra(Common.NOMBRE_SERIE_COMENTARIOS, nombreSerie);

                                        startActivity(intent);
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
            }

        DatabaseReference referenc = FirebaseDatabase.getInstance().getReference();
        referenc.child(FirebaseReferences.SERIES_REFERENCE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:
                        dataSnapshot.getChildren()){
                    Series serie = snapshot.getValue(Series.class);
                    final DatabaseReference fiRef = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS_LEIDOS_SERIE).child(ComunicarCurrentUser.getPhoneNumberUser())
                            .child(serie.getNombre()).child(FirebaseReferences.COM_LEIDOS);
                    fiRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                           Long leidos= (Long) dataSnapshot.getValue();
                           if(leidos==null) {
                               fiRef.setValue(0);
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

        DatabaseReference databaseReferences = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COMENTARIOS_LEIDOS_SERIE);
        databaseReferences.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                {
                    //  Collapsed
                       Window window = getWindow();
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.setStatusBarColor(getResources().getColor(R.color.menuBarArriba));
                    //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.menuBar)));

                }
                }
                else
                {
                    //Expanded
                    Window window = getWindow();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
                        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.menuBar)));

                    }

                }
            }
        });



        }


    public void collapseAppBarLayout(boolean contraer){
        CoordinatorLayout.LayoutParams params =(CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        appBarLayout.setLayoutParams(params);
        appBarLayout.setExpanded(contraer);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();



        //SI EL USUARIO PULSA EL EL MENÚ DE CONTACTO
        if (id == R.id.contacto) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.ayuda);
            builder.setMessage(R.string.msg_ayuda);

            //CONSEGUIMOS LAS VISTAS DE NUESTRO layout dialogo_view INFLÁNDOLO Y SE LO APLICAMOS AL BUILDER
            LayoutInflater inflater = getLayoutInflater();
            View miVista = inflater.inflate(R.layout.dialogo_view,null);
            builder.setView(miVista);
            editTextMensajeAyuda=miVista.findViewById(R.id.editTextAyuda);
            correoUsuAyuda=miVista.findViewById(R.id.userEmail);
            correoUsuAyuda.setText(ComunicarCorreoUsuario.getCorreoUsuario());
            builder.setPositiveButton(R.string.btn_enviar_ayuda, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {



                }
            });
            builder.setNegativeButton(R.string.btn_cancelar_ayuda,null);
            builder.setCancelable(false);
            final AlertDialog dialog =builder.create();
            dialog.show();

            //IMPLEMENTA UN LISTENER SOBRE EL BOTON DE ACEPTAR QUE EN CASO DE QUE EL CORREO INTRODUCIDO POR EL USUARIO SEA VALIDO ENVIA UN MENSAJE A UNA CUENTA DE CORREO
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.negro));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.negro));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!Utilities.validateEmail(correoUsuAyuda.getText().toString())){
                        Toast.makeText(TabActivity.this,R.string.error_email_format,Toast.LENGTH_SHORT).show();
                    }else{
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        Properties properties=new Properties();
                        properties.put("mail.smtp.host","smtp.gmail.com");
                        properties.put("mail.smtp.socketFactory,port","465");
                        properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
                        properties.put("mail.smtp.auth","true");
                        properties.put("mail.smtp.port","465");

                        try{

                            session= Session.getDefaultInstance(properties, new Authenticator() {
                                @Override
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication(emailAyuda,passwordAyuda);
                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        if(session!=null){
                            javax.mail.Message message = new MimeMessage(session);
                            try {
                                message.setFrom(new InternetAddress(emailAyuda));

                                //COMO ASUNTO DEL CORREO PONEMOS SU EMAIL,NÚMERO DE TLF Y CLAVE DE SU USUARIO DE FIREBASE
                                message.setSubject(correoUsuAyuda.getText().toString()+"/ Phone number -> "+ComunicarCurrentUser.getPhoneNumberUser()+" /Clave Firebase -> "+ComunicarClaveUsuarioActual.getClave());
                                message.setRecipients(javax.mail.Message.RecipientType.TO,InternetAddress.parse(emailAyuda));
                                message.setContent(editTextMensajeAyuda.getText().toString(),"text/html;charset=utf-8");
                                Transport.send(message);
                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(TabActivity.this, R.string.msg_enviado_ayuda,Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });

        }

        //SI EL USUARIO PULSA EL EL MENÚ DE CAMBIAR FONDO PANTALLA
        if(id==R.id.item_cambiar_fondo){
            seleccionarFondo();
        }

        //SI EL USUARIO PULSA EL EL MENÚ DE CAMBIAR AVATAR
        if(id==R.id.item_cambiar_avatar){
            seleccionarAvatar();
        }

        if(id==R.id.item_tutorial){
            SharedPreferences sharedPref = this.getSharedPreferences(Common.TUTORIAL_PREF,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(Common.PULSADO_TUTORIAL,true);
            editor.commit();
            Toast.makeText(this, R.string.menu_show_tutorial,Toast.LENGTH_SHORT).show();
        }


        return super.onOptionsItemSelected(item);
    }
    //MÉTODO QUE ABRIRÁ LA PANTALLA DE SELECCIÓN DE FONDOS DE ListaFondos ESPERANDO UN RESULTADO
    public void seleccionarFondo() {

        Intent intent = new Intent(this,ListaFondos.class);
        startActivityForResult(intent,LISTA_FONDOS);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
         SharedPreferences sharedPre = getSharedPreferences(Common.NOTIFICACION,Context.MODE_PRIVATE);
        boolean deNotificacion=sharedPre.getBoolean("notify",false);
        if(intent.getStringExtra(Common.NOTIFICACION).equals(Common.NOTIFICACION) && deNotificacion){
            SharedPreferences sharedPref = getSharedPreferences(Common.NOTIFICACION,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("notify",false);
            editor.commit();
            String nombreSerie=intent.getStringExtra(Common.NOMBRE_SERIE_COMENTARIOS);
            Intent i = new Intent(this, ComentariosActivity.class);
            i.putExtra(Common.NOMBRE_SERIE_COMENTARIOS,nombreSerie);
            startActivity(i);

        }else{
            setIntent(intent);
        }


    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (FirebaseDatabase.getInstance() != null)
        {
            FirebaseDatabase.getInstance().goOnline();
        }





    }

    @Override
    public void onPause() {

        super.onPause();
        if(FirebaseDatabase.getInstance()!=null)
        {
            FirebaseDatabase.getInstance().goOffline();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = this.getSharedPreferences(Common.TUTORIAL_PREF,getApplicationContext().MODE_PRIVATE);
        boolean pulsadoTutorial = preferences.getBoolean(Common.PULSADO_TUTORIAL,false);

        if(pulsadoTutorial){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(Common.TUTORIAL_CONTACTOS,true);
            editor.putBoolean(Common.TUTORIAL_SERIES,true);
            editor.putBoolean(Common.TUTORIAL_FAVORITOS,true);
            editor.putBoolean(Common.PULSADO_TUTORIAL,false);
            editor.commit();
        }else{
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(Common.PULSADO_TUTORIAL,false);
            editor.commit();
        }

    }

    //MÉTODO QUE ABRIRÁ LA PANTALLA DE CAMBIO DE PERFIL
    public void seleccionarAvatar() {
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        final DatabaseReference root = data.getReference();
        root.child(FirebaseReferences.USUARIOS_REFERENCE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot:
                                dataSnapshot.getChildren()){
                            Usuario user = snapshot.getValue(Usuario.class);
                            if(user.getTelefono().equals(ComunicarCurrentUser.getPhoneNumberUser())){
                                Intent intent = new Intent(TabActivity.this,Perfil.class);
                                intent.putExtra("usuario",user);
                                startActivity(intent);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }
    //MÉTODO QUE ESCONDE EL TECLADO
    public void esconderTeclado(View vista){

        InputMethodManager teclado = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        teclado.hideSoftInputFromWindow(vista.getWindowToken(), 0);
    }

    //MÉTODO QUE SE EJECUTARÁ CUANDO CERREMOS LA PANTALLA DE ListaFondos
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){

            //SI VIENE DE LISTA FONDOS CAMBIAMOS LA IMAGEN DE FONDO QUE NOS VIENE DEVUELTA.
            case LISTA_FONDOS:
                if(data != null && resultCode == RESULT_OK){
                    int numFondo = data.getIntExtra(Common.FONDO_SELECCIONADO,-1);
                    if(numFondo != -1){
                        Glide.with(TabActivity.this)
                                .load(listaFondos.get(numFondo))
                                .into(fondo);
                    }

                }
                break;

        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }




    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        //DEVUELVE UNA INSTANCIA DEL FRAGMENT
        public static Fragment newInstance(int sectionNumber) {
            Fragment fragment=null;
            switch (sectionNumber){
                case 1:fragment=new ContactosFragment();
                break;
                case 2:fragment=new SeriesFragment();
                    break;
                case 3:fragment=new FavoritosFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tab, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position){
            switch (position){
                case 0:
                    return getString(R.string.contactos_tab_title);
                case 1:
                    return getString(R.string.series_tab_title);
                case 2:
                    return getString(R.string.favoritos_tab_title);

            }
            return null;
        }

    }
}
