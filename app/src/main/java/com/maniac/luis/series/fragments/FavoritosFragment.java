package com.maniac.luis.series.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.maniac.luis.series.Adapters.AdaptadorFavoritos;
import com.maniac.luis.series.Objetos.Series;
import com.maniac.luis.series.Objetos.Suscripcion;
import com.maniac.luis.series.R;
import com.maniac.luis.series.actividades.TabActivity;
import com.maniac.luis.series.references.FirebaseReferences;
import com.maniac.luis.series.utilidades.ComunicarClaveUsuarioActual;
import com.maniac.luis.series.utilidades.ComunicarCurrentUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoritosFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FavoritosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritosFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    RecyclerView rv;
    AdaptadorFavoritos adaptadorFavoritos;
    List<Suscripcion> suscripciones;
    SearchView searchView = null;

    public FavoritosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoritosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritosFragment newInstance(String param1, String param2) {
        FavoritosFragment fragment = new FavoritosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //INFLA EL LAYOUT PARA ESTE FRAGMENT
        View vista = inflater.inflate(R.layout.fragment_favoritos, container, false);
        rv=vista.findViewById(R.id.recyclerSeriesFavoritos);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        suscripciones=new ArrayList<>();
        adaptadorFavoritos=new AdaptadorFavoritos(suscripciones,this.getContext());
        rv.setAdapter(adaptadorFavoritos);
        FirebaseDatabase database=FirebaseDatabase.getInstance();

        //CREAMOS UN LISTENER AL NODO SUSCRIPCIONES DE LA BB.DD PARA QUE CUANDO HAYA ALGÚN CAMBIO EN LA BB.DD CAMBIE NUESTRA VISTA , SOLO SE AÑADEN LAS
        //SUSCRIPCIONES CUYO USUARIO COINCIDE CON EL USUARIO ACTUAL
        database.getReference(FirebaseReferences.SUSCRIPCIONES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                suscripciones.removeAll(suscripciones);
                for(DataSnapshot snapshot:
                        dataSnapshot.getChildren()){
                    Suscripcion suscripcion = snapshot.getValue(Suscripcion.class);
                    Log.i("hoy","suscripcion.getIdUsuario() -> " + suscripcion.getIdUsuario());
                    Log.i("hoy","ComunicarClaveUsuarioActual.getClave() -> " + ComunicarClaveUsuarioActual.getClave());
                    if(suscripcion.getTelefono().equals(ComunicarCurrentUser.getPhoneNumberUser())){
                        Log.i("suscripcion"," estrellas -> " + suscripcion.getEstrellasUsuario());
                        suscripciones.add(suscripcion);
                    }

                }
                adaptadorFavoritos.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
                return vista;
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
                    List<Suscripcion>listaFiltrada=filter(suscripciones,newText);
                    adaptadorFavoritos.setFilter(listaFiltrada);
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
                adaptadorFavoritos.setFilter(suscripciones);
                ((TabActivity)getActivity()).collapseAppBarLayout(true);
                return true;
            }
        });

    }

    private List<Suscripcion> filter(List<Suscripcion> suscripciones,String texto){
        List<Suscripcion> listaFiltrada=new ArrayList<>();
        try{
            texto=texto.toLowerCase();

            for(Suscripcion suscripcion: suscripciones){
                String suscripciones2=suscripcion.getSerie().toLowerCase();
                if(suscripciones2.contains(texto)){
                    listaFiltrada.add(suscripcion);
                }
            }
        }catch (Exception e){

        }
        return listaFiltrada;
    }
}
