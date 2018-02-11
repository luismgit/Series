package com.example.luis.series.actividades;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.TabLayout;
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

import android.widget.ImageView;
import android.widget.TextView;

import com.example.luis.series.R;
import com.example.luis.series.fragments.ContactosFragment;
import com.example.luis.series.fragments.FavoritosFragment;
import com.example.luis.series.fragments.SeriesFragment;
import com.example.luis.series.references.FirebaseReferences;
import com.example.luis.series.utilidades.Common;
import com.example.luis.series.utilidades.ComunicarClaveUsuarioActual;
import com.example.luis.series.utilidades.ComunicarCurrentUser;
import com.example.luis.series.utilidades.Imagenes;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        //CARGAMOS EN EL ARRAY DE FONDOS TODOS LOS FONDOS DE PANTALLA DE DRAWABLE
        fondos= Imagenes.getFondosPantalla();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        fondo=findViewById(R.id.header);

        //SELECCIONAMOS UN FONDO DE PANTALLA ALEATORIO Y LO APLICAMOS
        int fondoAleatorio=(int)Math.floor(Math.random()*((fondos.length-1)-0)+0);
        fondo.setImageResource(fondos[fondoAleatorio]);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
