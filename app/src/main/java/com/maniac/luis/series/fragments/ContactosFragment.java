package com.maniac.luis.series.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maniac.luis.series.Adapters.AdaptadorContactos;
import com.maniac.luis.series.Objetos.Suscripcion;
import com.maniac.luis.series.Objetos.Usuario;
import com.maniac.luis.series.R;
import com.maniac.luis.series.actividades.TabActivity;
import com.maniac.luis.series.references.FirebaseReferences;
import com.maniac.luis.series.utilidades.Common;
import com.maniac.luis.series.utilidades.ComunicarClaveUsuarioActual;
import com.maniac.luis.series.utilidades.ComunicarContactosPhoneNumber;
import com.maniac.luis.series.utilidades.ComunicarCurrentUser;
import com.maniac.luis.series.utilidades.CustomViewTarget;
import com.maniac.luis.series.utilidades.ListaNumerosAgendaTelefonos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactosFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactosFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    RecyclerView rv;
    List<Usuario> usuarios;
    AdaptadorContactos adapter;
    Hashtable<String,String> contactos;
    String phoneNumberUser;
    FirebaseUser user;
    TextView mensajeSinContactos;
    SearchView searchView = null;
    ShowcaseView showcaseView;
    boolean sinContactos;
    ViewPager vp;
    SharedPreferences sharedPref;
    Usuario usuarioMateo;

    public ContactosFragment() {
        // Required empty public constructor
        Log.i("actividades","public ContactosFragment()");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactosFragment newInstance(String param1, String param2) {
        Log.i("actividades","public static ContactosFragment newInstance(String param1, String param2)");
        ContactosFragment fragment = new ContactosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("actividades","public void onCreate(Bundle savedInstanceState)");

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("actividades"," public View onCreateView(LayoutInflater inflater, ViewGroup container,\n" +
                "                             Bundle savedInstanceState) ");

        View vista=inflater.inflate(R.layout.fragment_contactos, container, false);
        vp = getActivity().findViewById(R.id.container);
        rv=vista.findViewById(R.id.recycler);
        contactos = new Hashtable<String, String>();
        usuarios=new ArrayList<>();
        mensajeSinContactos=vista.findViewById(R.id.mensajeSinContactos);
        //LE APLICAMOS UN LAYOUT A EL RECYCLERVIEW
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        //GUARDAMOS EL USUARIO ACTUAL
        //user= ComunicarCurrentUser.getUser();---------------------------------------------
        //GUARDAMOS EL TELEÉFONO DEL USUARIO ACTUAL
        phoneNumberUser = ComunicarCurrentUser.getPhoneNumberUser();
        phoneNumberUser.replaceAll("\\s","");
        if(phoneNumberUser.substring(0,3).equals("+34")){
            phoneNumberUser=phoneNumberUser.substring(3,phoneNumberUser.length());
        }
        //MÉTODO QUE CARGA EL HASHTABLE LOS TELEFONOS DE LA AGENDA CON SU CORRESPONDIENTE NOMBRE DE CONTACTO
        loadContactFromTlf();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //INSTANCIAMOS NUESTRO ADAPTADOR PASÁNDOLE EL ARRAYLIST DE USUARIOS Y LA PROPIA CLASE
        adapter=new AdaptadorContactos(usuarios,this.getContext(),vp);
        //LE APLICAMOS EL ADAPTADOR AL RECYCLERVIEW
        rv.setAdapter(adapter);

        sharedPref = getActivity().getSharedPreferences(Common.TUTORIAL_PREF,Context.MODE_PRIVATE);

        //LISTENER QUE COTEJA QUE NÚMEROS DE LA AGENDA DEL USUARIO COINCIDEN CON LOS DE NUESTRA BB.DD Y LOS AÑASE AL ARRAYLIST USUARIOS
        database.getReference(FirebaseReferences.USUARIOS_REFERENCE).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sinContactos=true;
                if(usuarios.size()!=0){
                    if(!usuarios.get(0).getTelefono().equals("000000000")){
                        usuarios.removeAll(usuarios);
                    }
                }

                ComunicarContactosPhoneNumber.removeAllPhoneNumbers();
                Log.i("Carga","empieza la carga");
                for (DataSnapshot snapshot:
                        dataSnapshot.getChildren() ){
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    String phoneNumber = usuario.getTelefono();
                    if(contactos.containsKey(phoneNumber)){
                        if(!phoneNumber.equals(phoneNumberUser)){
                            usuario.setNick(contactos.get(phoneNumber));
                            usuarios.add(usuario);
                            sinContactos=false;
                            ComunicarContactosPhoneNumber.addPhoneNumber(usuario.getTelefono());
                        }
                    }
                    if(sharedPref.getBoolean(Common.TUTORIAL_CONTACTOS,true) && usuario.getTelefono().equals("000000000")){
                        Log.i("mateo","asigna usuario a usuarioMateo");
                        usuarioMateo=usuario;
                    }
                }
                if(sinContactos && sharedPref.getBoolean(Common.TUTORIAL_CONTACTOS,true)){
                    usuarios.add(usuarioMateo);
                    Log.i("mateo","añade usuarioMateo a la lista de usuarios");
                }
                if(sharedPref.getBoolean(Common.TUTORIAL_CONTACTOS,true)){
                    Log.i("mateo","mostrar el tutorial es true");
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(Common.TUTORIAL_CONTACTOS,false);
                    editor.commit();
                    if(usuarios.get(0).getTelefono().equals("000000000")){
                        Log.i("mateo","si el primer usuario es Mateo");
                            showcaseView = new ShowcaseView.Builder(getActivity())
                                    .setTarget(new CustomViewTarget(R.id.showcase_button,-75,0,getActivity()))
                                    .setContentTitle(getString(R.string.contactos_showcase))
                                    .setStyle(R.style.CustomShowcaseTheme3)
                                    .setContentText(getString(R.string.showcaseTextoCont))
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            showcaseView.hide();
                                            usuarios.remove(0);
                                            vp.setCurrentItem(1);
                                        }
                                    })
                                    .build();

                    }else{
                            showcaseView = new ShowcaseView.Builder(getActivity())
                                    .setTarget(new CustomViewTarget(R.id.showcase_button,-75,0,getActivity()))
                                    .setContentTitle(getString(R.string.contactos_showcase))
                                    .setStyle(R.style.CustomShowcaseTheme3)
                                    .setContentText(getString(R.string.showcaseTextoCont))
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            showcaseView.hide();
                                            vp.setCurrentItem(1);
                                        }
                                    })
                                    .build();

                    }
                }
                if(usuarios.size()==0){
                    mensajeSinContactos.setVisibility(View.VISIBLE);
                }else{
                    mensajeSinContactos.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //GUARDAMOS EL TELÉFONO DEL USUARIO
        //FirebaseUser user = ComunicarCurrentUser.getUser();--------------------------------------
        String phoneNumber=ComunicarCurrentUser.getPhoneNumberUser();
        phoneNumber.replaceAll("\\s","");
        if(phoneNumber.substring(0,3).equals("+34")){
            phoneNumber=phoneNumber.substring(3,phoneNumber.length());
        }

        //LLEGAMOS HASTA EL NODO EN LA BB.DD DONDE ESTÁ EL USUARIO ACTUAL , COGEMOS SU CLAVE Y LA GUARDAMOS EN LA CLASE ComunicarClaveUsuarioActual
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        DatabaseReference root = data.getReference();
        root.child(FirebaseReferences.USUARIOS_REFERENCE).orderByChild(FirebaseReferences.PHONE_REFERENCE).equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    String claveUsuarioActual = childSnapshot.getKey();
                    ComunicarClaveUsuarioActual.setClave(claveUsuarioActual);


                }

                //LLEGAMOS HASTA EL NODO EN LA BB.DD DONDE ESTÁ EL USUARIO ACTUAL Y ACCEDEMOS A SU NODO HIJO 'conectado'
                final FirebaseDatabase dt = FirebaseDatabase.getInstance();
                final  DatabaseReference myConnectionsRef = dt.getReference().child(FirebaseReferences.USUARIOS_REFERENCE).child(ComunicarClaveUsuarioActual.getClave()).child(FirebaseReferences.CONECTADO);
                //final DatabaseReference lastOnlineRef = dt.getReference().child(FirebaseReferences.USUARIOS_REFERENCE).child(ComunicarClaveUsuarioActual.getClave()).child("ultimaconexion");

                //A TRAVÉS DE LA REFERENCIA .info/connected SABEMOS ACCEDEMOS A LA CONEXIÓN DE CADA USUARIO A FIREBASE
                final DatabaseReference connectedRef = dt.getReference(FirebaseReferences.INFO_CONNECTED);
                connectedRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean connected = dataSnapshot.getValue(Boolean.class);
                        //SI SE CONECTA ESTABLECEMOS EL NODO 'conectado' A ONLINE,CUANDO SE DESCONECTE LO PASAMOS A OFFLINE
                        if(connected){
                            myConnectionsRef.setValue(FirebaseReferences.ONLINE);
                           // DatabaseReference con = myConnectionsRef;
                            myConnectionsRef.onDisconnect().setValue(getFecha());
                            //lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);

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



        return  vista;
    }

    private String getFecha(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf= new SimpleDateFormat("dd/MM HH:mm");
        return sdf.format(date);
    }

    public void loadContactFromTlf() {
        ContentResolver contentResolver=getContext().getContentResolver();
        String [] projeccion=new String[]{ContactsContract.Data.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER};
        //String [] projeccion=new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.Contacts.DISPLAY_NAME};
        String selectionClause=ContactsContract.Data.MIMETYPE + "='" +
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND " +
                ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL";
        //String sortOrder = ContactsContract.Data.DISPLAY_NAME + " ASC";
        Cursor cursor=getContext().getContentResolver().query(ContactsContract.Data.CONTENT_URI,projeccion,selectionClause,null,null);
        while(cursor.moveToNext()){
            String name=cursor.getString(0);
            String phoneNumber=cursor.getString(1);
            if(phoneNumber.length()>=9){
                phoneNumber=phoneNumber.replaceAll("\\s","");
                if(phoneNumber.substring(0,3).equals("+34")){
                    phoneNumber=phoneNumber.substring(3,phoneNumber.length());
                    //Log.i("contactosTlf","numSin+34 -> " + phoneNumber);

                }
                //nombreContactosTelefono.add(name);
               // contactosTelefono.add(phoneNumber);
                Log.i("contactos","Nombre: " + name);
                Log.i("contactos","Numero: " + phoneNumber);
                contactos.put(phoneNumber,name);
            }

            //Log.i("contactos","Identificador: " + cursor.getString(0));


            //Log.i("contactos","Tipo: " + cursor.getString(3));
        }
        ListaNumerosAgendaTelefonos.setContactos(contactos);
        cursor.close();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        Log.i("actividades","onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_tab, menu);
        MenuItem item = menu.findItem(R.id.search);
        searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Busca");
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("fragmenttt","query submit -> " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i("fragmenttt","query change -> " + newText);
                try{
                    List<Usuario>listaFiltrada=filter(usuarios,newText);
                    adapter.setFilter(listaFiltrada);
                }catch (Exception e){

                }
                return false;
            }
        });
        final EditText searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                ((TabActivity)getActivity()).collapseAppBarLayout(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                adapter.setFilter(usuarios);
                ((TabActivity)getActivity()).collapseAppBarLayout(true);
                return true;
            }
        });

    }

    private List<Usuario> filter(List<Usuario> usuarios,String texto){
        List<Usuario> listaFiltrada=new ArrayList<>();
        try{
            texto=texto.toLowerCase();

            for(Usuario usuario: usuarios){
                String usuario2=usuario.getNick().toLowerCase();
                if(usuario2.contains(texto)){
                    listaFiltrada.add(usuario);
                }
            }
        }catch (Exception e){

        }
        return listaFiltrada;
    }
}
