package com.example.luis.series.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.luis.series.Adapters.AdaptadorContactos;
import com.example.luis.series.Objetos.Usuario;
import com.example.luis.series.R;
import com.example.luis.series.references.FirebaseReferences;
import com.example.luis.series.utilidades.ComunicarClaveUsuarioActual;
import com.example.luis.series.utilidades.ComunicarCurrentUser;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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
    //List<String> contactosTelefono;
    //List<String> nombreContactosTelefono;
    Hashtable<String,String> contactos;
    String phoneNumberUser;
    FirebaseUser user;


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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("actividades"," public View onCreateView(LayoutInflater inflater, ViewGroup container,\n" +
                "                             Bundle savedInstanceState) ");
        View vista=inflater.inflate(R.layout.fragment_contactos, container, false);
        rv=vista.findViewById(R.id.recycler);
       // contactosTelefono=new ArrayList<>();
        //nombreContactosTelefono=new ArrayList<>();
        contactos = new Hashtable<String, String>();
        usuarios=new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        user= ComunicarCurrentUser.getUser();
        phoneNumberUser = user.getPhoneNumber();
        phoneNumberUser.replaceAll("\\s","");
        if(phoneNumberUser.substring(0,3).equals("+34")){
            phoneNumberUser=phoneNumberUser.substring(3,phoneNumberUser.length());
        }
        loadContactFromTlf();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        adapter=new AdaptadorContactos(usuarios,this.getContext());
        rv.setAdapter(adapter);
        database.getReference(FirebaseReferences.USUARIOS_REFERENCE).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuarios.removeAll(usuarios);
                Log.i("Carga","empieza la carga");
                for (DataSnapshot snapshot:
                        dataSnapshot.getChildren() ){
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    String phoneNumber = usuario.getTelefono();
                    if(contactos.containsKey(phoneNumber)){
                        if(!phoneNumber.equals(phoneNumberUser)){
                            //Log.i("CONTACTOSS","user -> " + "phoneNumberUser" + phoneNumberUser);
                            //Log.i("CONTACTOSS","finales -> " + phoneNumber);
                            Log.i("CONTACTOSS","finales -> " + contactos.get(phoneNumber));
                            usuario.setNick(contactos.get(phoneNumber));
                            usuarios.add(usuario);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseUser user = ComunicarCurrentUser.getUser();
        String phoneNumber=user.getPhoneNumber();
        phoneNumber.replaceAll("\\s","");
        if(phoneNumber.substring(0,3).equals("+34")){
            phoneNumber=phoneNumber.substring(3,phoneNumber.length());
        }

        FirebaseDatabase data = FirebaseDatabase.getInstance();
        DatabaseReference root = data.getReference();
        root.child(FirebaseReferences.USUARIOS_REFERENCE).orderByChild("telefono").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    String claveUsuarioActual = childSnapshot.getKey();
                    ComunicarClaveUsuarioActual.setClave(claveUsuarioActual);

                }

                final FirebaseDatabase dt = FirebaseDatabase.getInstance();
                final  DatabaseReference myConnectionsRef = dt.getReference().child(FirebaseReferences.USUARIOS_REFERENCE).child(ComunicarClaveUsuarioActual.getClave()).child("conectado");
                //final DatabaseReference lastOnlineRef = dt.getReference().child(FirebaseReferences.USUARIOS_REFERENCE).child(ComunicarClaveUsuarioActual.getClave()).child("ultimaconexion");
                final DatabaseReference connectedRef = dt.getReference(".info/connected");
                connectedRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean connected = dataSnapshot.getValue(Boolean.class);
                        if(connected){
                            myConnectionsRef.setValue("online");
                            DatabaseReference con = myConnectionsRef;
                            con.onDisconnect().setValue("offline");
                            //lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                            //con.setValue(Boolean.TRUE);
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

    private void loadContactFromTlf() {
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
}
