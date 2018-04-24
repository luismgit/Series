package com.maniac.luis.series.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maniac.luis.series.Adapters.AdaptadorSeries;
import com.maniac.luis.series.CustomViewTarget;
import com.maniac.luis.series.Objetos.Series;
import com.maniac.luis.series.R;
import com.maniac.luis.series.actividades.TabActivity;
import com.maniac.luis.series.references.FirebaseReferences;
import com.maniac.luis.series.utilidades.Common;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SeriesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SeriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SeriesFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    RecyclerView rv;
    AdaptadorSeries adaptadorSeries;
    List<Series> series;
    SearchView searchView = null;
    ViewPager vp;
    Toolbar toolbar;
    Menu menuToolbar;
    ShowcaseView showcaseView;
    boolean isShowedTuturial;
    SharedPreferences sharedPref;
    EditText searchEditText;
    public SeriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SeriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SeriesFragment newInstance(String param1, String param2) {
        SeriesFragment fragment = new SeriesFragment();
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
        View vista= inflater.inflate(R.layout.fragment_series, container, false);
        sharedPref = getActivity().getSharedPreferences(Common.TUTORIAL_PREF,getActivity().getApplicationContext().MODE_PRIVATE);
        isShowedTuturial=sharedPref.getBoolean(Common.TUTORIAL_SERIES,true);
        vp = getActivity().findViewById(R.id.container);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Log.i("PageChangeListener","onPageScrolled");

            }

            @Override
            public void onPageSelected(int position) {
                Log.i("PageChangeListener","onPageSelected -> " + position);
                if(position==1 && isShowedTuturial){
                    Log.i("isShowedToturial","position==1 && isShowedTuturial" );
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(Common.TUTORIAL_SERIES,false);
                    editor.commit();
                    isShowedTuturial=false;
                     showcaseView = new ShowcaseView.Builder(getActivity())
                            .setTarget(new CustomViewTarget(R.id.toolbar, 300, -100, getActivity()))
                            .setContentTitle("Series")
                            .setStyle(R.style.CustomShowcaseTheme2)
                            .setContentText("Busca una serie")
                             .setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View view) {
                                     showcaseView.hide();
                                     showcaseView=null;
                                 }
                             })
                            .build();
                }
                if(position==2 && showcaseView!=null){
                    showcaseView.hide();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Log.i("PageChangeListener","onPageScrollStateChanged");
            }
        });
        rv=vista.findViewById(R.id.recyclerSeries);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        series=new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        adaptadorSeries=new AdaptadorSeries(series,this.getContext(),vp);
        rv.setAdapter(adaptadorSeries);

        //COGEMOS LA REFERENCIA DEL NODO SERIES A LO AÑADIMOS AL ARRAYLIST SERIES , CUANDO HAYA UN CAMBIO SE NOTIFICA AL AL ADAPTADOR PARA QUE CAMBIE LAS VISTAS
        database.getReference(FirebaseReferences.SERIES_REFERENCE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                series.removeAll(series);
                for (DataSnapshot snapshot:
                    dataSnapshot.getChildren()){
                    Series serie = snapshot.getValue(Series.class);
                    series.add(serie);
                }

                adaptadorSeries.notifyDataSetChanged();
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
        Log.i("onCreateOptionsMenu","onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_tab, menu);
        final MenuItem item = menu.findItem(R.id.search);
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
                    List<Series>listaFiltrada=filter(series,newText);
                    adaptadorSeries.setFilter(listaFiltrada);
                }catch (Exception e){

                }
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showcaseView!=null){
                    showcaseView.hide();
                    showcaseView = new ShowcaseView.Builder(getActivity())
                            .setTarget(new CustomViewTarget(R.id.toolbar, -200, -100, getActivity()))
                            .setContentTitle("Series")
                            .setStyle(R.style.CustomShowcaseTheme2)
                            .setContentText("Busca una serie")
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showcaseView.hide();
                                    showcaseView=null;
                                }
                            })
                            .build();
                }
            }
        });
        searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                ((TabActivity)getActivity()).collapseAppBarLayout(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                adaptadorSeries.setFilter(series);
                ((TabActivity)getActivity()).collapseAppBarLayout(true);
                return true;
            }
        });
    }

    private List<Series> filter(List<Series> series,String texto){
        List<Series> listaFiltrada=new ArrayList<>();
        try{
            texto=texto.toLowerCase();

            for(Series serie: series){
                String serie2=serie.getNombre().toLowerCase();
                if(serie2.contains(texto)){
                    listaFiltrada.add(serie);
                }
            }
        }catch (Exception e){

        }
        if(listaFiltrada.size()==1 && showcaseView!=null){
            ((TabActivity)getActivity()).esconderTeclado(searchEditText);
            showcaseView.hide();
            showcaseView = new ShowcaseView.Builder(getActivity())
                    .setTarget(new CustomViewTarget(R.id.textViewOptionsDigit, 0, 0, getActivity()))
                    .setContentTitle("Favoritos")
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .setContentText("Añade la serie a favoritos")
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showcaseView.hide();
                            showcaseView=null;
                        }
                    })
                    .build();
        }
        return listaFiltrada;
    }


}
