package com.example.luis.series.actividades;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.luis.series.R;
import com.example.luis.series.fragments.ContactosFragment;
import com.example.luis.series.fragments.FavoritosFragment;
import com.example.luis.series.fragments.SeriesFragment;
import com.example.luis.series.references.FirebaseReferences;
import com.example.luis.series.utilidades.Common;
import com.example.luis.series.utilidades.ComunicarClaveUsuarioActual;
import com.example.luis.series.utilidades.ComunicarCurrentUser;
import com.example.luis.series.utilidades.Imagenes;
import com.example.luis.series.utilidades.Utilities;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

public class TabActivity extends AppCompatActivity implements ContactosFragment.OnFragmentInteractionListener,
SeriesFragment.OnFragmentInteractionListener,FavoritosFragment.OnFragmentInteractionListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private int [] fondos;
    ImageView fondo;
    Session session;
    EditText editTextMensajeAyuda;
    EditText correoUsuAyuda;
    String emailAyuda;
    String passwordAyuda;
    List<String> listaFondos;
    private static final int LISTA_ICONOS=1;
    private static final int LISTA_FONDOS=2;
     int contador=0;
     int aleatorio=0;
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
        fondo=findViewById(R.id.header);
        fondo.setImageResource(R.drawable.series_back);


        listaFondos=new ArrayList<>();
        //CONTAMOS CUANTOS FONDOS DE PANTALLA HAY EN LA BB.DD Y LOS CARGAMOS LOS ENLACES EN UNA LISTA, COGEMOS UNO DE ELLOS ALEATORIO.
        FirebaseDatabase datab = FirebaseDatabase.getInstance();
        datab.getReference(FirebaseReferences.COMMON).child(FirebaseReferences.FONDOS_SERIES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:
                        dataSnapshot.getChildren()){
                    contador++;
                    String uriFondo= (String) snapshot.getValue();
                    listaFondos.add(uriFondo);
                }
                aleatorio=(int)Math.floor(Math.random()*((listaFondos.size())-0)+0);

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


        //SELECCIONAMOS UN FONDO DE PANTALLA ALEATORIO Y LO APLICAMOS
        //int fondoAleatorio=(int)Math.floor(Math.random()*((fondos.length-1)-0)+0);
        //fondo.setImageResource(fondos[fondoAleatorio]);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        Log.i("actividades","OnCreate TabActivity");

        //GUARDAMOS EN PREFERENCIAS QUE EL USUARIO HA LLEGADO A ESTA ACTIVITY.
        SharedPreferences sharedPref = getSharedPreferences(Common.PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(Common.REGISTRO_CERRADO,false);
        editor.commit();

        //CARGAMOS EN LA VARIABLE user EL NÚMERO DE TELÉFONO DEL USUARIO Y LE QUITAMOS SI TUVIERA ESPACIOS Y EL +34
        FirebaseUser user = ComunicarCurrentUser.getUser();
        String phoneNumber=user.getPhoneNumber();
        phoneNumber.replaceAll("\\s","");
        if(phoneNumber.substring(0,3).equals("+34")){
            phoneNumber=phoneNumber.substring(3,phoneNumber.length());
        }

        //LISTENER QUE GUARDA EN NUESTRA CLASE ComunicarClaveUsuarioActual EL TELÉFONO DEL USUARIO ACTUAL PARA ACCEDER A ÉL EN LAS DEMÁS ACTIVIDADES
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        DatabaseReference root = data.getReference();
        root.child(FirebaseReferences.USUARIOS_REFERENCE).orderByChild(FirebaseReferences.PHONE_REFERENCE).equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    String claveUsuarioActual = childSnapshot.getKey();
                    ComunicarClaveUsuarioActual.setClave(claveUsuarioActual);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //SACAMOS DE LA BB.DD EL EMAIL Y PASSWORD DE LA CUENTA DE CORREO PARA ENVIAR UN CORREO SI EL USUARIO PULSA EN EL MENÚ DE CONTACTO
        FirebaseDatabase fdb=FirebaseDatabase.getInstance();
        DatabaseReference dr=fdb.getReference();
        dr.child(FirebaseReferences.COMMON).child(FirebaseReferences.EMAIL_AYUDA).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                emailAyuda= (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseDatabase f=FirebaseDatabase.getInstance();
        DatabaseReference r=f.getReference();
        r.child(FirebaseReferences.COMMON).child(FirebaseReferences.PASSWORD_AYUDA).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                passwordAyuda= (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("actividades","onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("actividades","onOptionsItemSelected");
        int id = item.getItemId();

        //SI EL USUARIO PULSA EL EL MENÚ DE CONTACTO
        if (id == R.id.contacto) {
            Log.i("onOptionsItemSelected","correo ->" + emailAyuda);
            Log.i("onOptionsItemSelected","password ->" + passwordAyuda);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.ayuda);
            builder.setMessage(R.string.msg_ayuda);

            //CONSEGUIMOS LAS VISTAS DE NUESTRO layout dialogo_view INFLÁNDOLO Y SE LO APLICAMOS AL BUILDER
            LayoutInflater inflater = getLayoutInflater();
            View miVista = inflater.inflate(R.layout.dialogo_view,null);
            builder.setView(miVista);
            editTextMensajeAyuda=miVista.findViewById(R.id.editTextAyuda);
            correoUsuAyuda=miVista.findViewById(R.id.userEmail);


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
           // return true;
        }

        //SI EL USUARIO PULSA EL EL MENÚ DE CAMBIAR FONDO PANTALLA
        if(id==R.id.item_cambiar_fondo){
            seleccionarFondo();
        }

        //SI EL USUARIO PULSA EL EL MENÚ DE CAMBIAR AVATAR
        if(id==R.id.item_cambiar_avatar){
            seleccionarAvatar();
        }


        return super.onOptionsItemSelected(item);
    }
    //MÉTODO QUE ABRIRÁ LA PANTALLA DE SELECCIÓN DE FONDOS DE ListaFondos ESPERANDO UN RESULTADO
    public void seleccionarFondo() {

        Intent intent = new Intent(this,ListaFondos.class);
        startActivityForResult(intent,LISTA_FONDOS);
    }


    //MÉTODO QUE ABRIRÁ LA PANTALLA DE SELECCIÓN DE AVATARES DE ListaIconos ESPERANDO UN RESULTADO
    public void seleccionarAvatar() {

        Intent intent = new Intent(this,ListaIconos.class);
        startActivityForResult(intent,LISTA_ICONOS);
    }

    //MÉTODO QUE SE EJECUTARÁ CUANDO CERREMOS LA PANTALLA ListaIconos o de ListaFondos
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            //SI VIENE DE LISTA ICONOS
            case LISTA_ICONOS:

                //SI LOS DATOS NO SON NULOS
                if(data != null && resultCode == RESULT_OK){

                    //GUARDAMOS EN numIcono EL NºDE AVATAR(VISTA) SELECCIONADA
                    int numIcono = data.getIntExtra(Common.ICONOSELECCIONADO,-1);
                    if(numIcono == -1){
                        //SI NO HA SELECCIONADO NINGÚN AVATAR ,NADA
                    }else{
                        //ACTUALIZAMOS LA REFERENCIA AVATAR DE FIREBASE DEL USUARIO ACTUAL
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference rootRef = firebaseDatabase.getReference();
                        rootRef.child(FirebaseReferences.USUARIOS_REFERENCE).child(ComunicarClaveUsuarioActual.getClave()).child(FirebaseReferences.AVATAR).setValue(numIcono);
                        Toast.makeText(this.getApplicationContext(), R.string.mod_avatar,Toast.LENGTH_SHORT).show();
                    }

                }
                break;

            //SI VIENE DE LISTA FONDOS CAMBIAMOS LA IMAGEN DE FONDO QUE NOS VIENE DEVUELTA.
            case LISTA_FONDOS:
                if(data != null && resultCode == RESULT_OK){
                    int numFondo = data.getIntExtra(Common.FONDO_SELECCIONADO,-1);
                    Log.i("OnActivityResult","Numero de icono -> " + numFondo);
                    if(numFondo != -1){
                        //fondo.setImageResource(fondos[numFondo]);
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
            Log.i("actividades","PlaceholderFragment()");
        }

        //DEVUELVE UNA INSTANCIA DEL FRAGMENT
        public static Fragment newInstance(int sectionNumber) {
            Log.i("actividades","public static Fragment newInstance");
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
            Log.i("actividades","public View onCreateView(LayoutInflater inflater, ViewGroup container,\n" +
                    "                                 Bundle savedInstanceState)");
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
            Log.i("actividades","public Fragment getItem(int position)");
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
            Log.i("actividades","public CharSequence getPageTitle(int position)");
            switch (position){
                case 0:
                    return Common.CONTACTOS;
                case 1:
                    return Common.SERIES;
                case 2:
                    return Common.FAVORITOS;

            }
            return null;
        }

    }
}
