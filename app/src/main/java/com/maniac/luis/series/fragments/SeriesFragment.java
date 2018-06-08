package com.maniac.luis.series.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maniac.luis.series.Adapters.AdaptadorSeries;
import com.maniac.luis.series.Objetos.Series;
import com.maniac.luis.series.R;
import com.maniac.luis.series.actividades.TabActivity;
import com.maniac.luis.series.references.FirebaseReferences;
import com.maniac.luis.series.utilidades.Common;
import com.maniac.luis.series.utilidades.CustomViewTarget;
import com.maniac.luis.series.utilidades.EliminaAcentos;
import com.maniac.luis.series.utilidades.SerieComparatorPorNombre;

import java.util.ArrayList;
import java.util.Collections;
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
    ViewPager vp;;
    ShowcaseView showcaseView;
    ShowcaseView showcaseView2;
    boolean isShowedTuturial;
    SharedPreferences sharedPref;
    EditText searchEditText;
    boolean pasadoTutorialShowcaseBusca,pasadoTutorialShowcaseBusca2;
    FirebaseDatabase database;
    Button ordenarBoton;
    Spinner spinnerOrdenar,spinnerPaises;
    String parametroOrdenacion,metodoOrdenacion;
    RadioButton radioNombre,radioFecha,radioLikes;
    LinearLayoutManager lm;
    List<Series>listaFiltrada;
    Button botonOk,botonCancelar;
    AlertDialog dialog;


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
        database = FirebaseDatabase.getInstance();
        rv=vista.findViewById(R.id.recyclerSeries);
        ordenarBoton=vista.findViewById(R.id.botonOrdenar);


        ordenarBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mbuilder = new AlertDialog.Builder(getActivity());
                View vista = getLayoutInflater().inflate(R.layout.dialog_ordenar,null);
                spinnerOrdenar=vista.findViewById(R.id.spinnerOrdena);
                //ArrayAdapter<String> adapter  = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.spinner_ordena));
                ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
                        R.array.spinner_ordena, R.layout.spinner_item_orden);
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinnerOrdenar.setAdapter(adapter);

                ArrayAdapter adapterPaises = ArrayAdapter.createFromResource(getActivity(),
                        R.array.spinner_paises, R.layout.spinner_item_orden);
                spinnerPaises=vista.findViewById(R.id.spinnerPaises);
                //ArrayAdapter<String> adapterPaises  = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.spinner_paises));
                adapterPaises.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinnerPaises.setAdapter(adapterPaises);
                 radioNombre=vista.findViewById(R.id.ordenaNombre);
                 radioNombre.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         if(spinnerOrdenar.getSelectedItem().equals("Descendente")){
                             spinnerOrdenar.setSelection(0);
                         }
                     }
                 });
                 radioFecha=vista.findViewById(R.id.ordenaFecha);
                radioFecha.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(spinnerOrdenar.getSelectedItem().equals("Ascendente")){
                            spinnerOrdenar.setSelection(1);
                        }
                    }
                });
               radioLikes=vista.findViewById(R.id.ordenaLikes);
                radioLikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(spinnerOrdenar.getSelectedItem().equals("Ascendente")){
                            spinnerOrdenar.setSelection(1);
                        }
                    }
                });
                if(parametroOrdenacion==null || parametroOrdenacion.equals(Common.NOMBRE) ){
                    radioNombre.setChecked(true);
                }else if(parametroOrdenacion.equals(Common.FECHA_EMISION)){
                    radioFecha.setChecked(true);
                }else if(parametroOrdenacion.equals(Common.LIKES)){
                    radioLikes.setChecked(true);
                }

               botonOk=vista.findViewById(R.id.botonOk);
               botonOk.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       if(radioNombre.isChecked()){
                           parametroOrdenacion=Common.NOMBRE;
                           if(listaFiltrada!=null){
                               Collections.sort(listaFiltrada, new SerieComparatorPorNombre(true,parametroOrdenacion));
                               adaptadorSeries.setFilter(listaFiltrada);
                           }else{
                               Collections.sort(series, new SerieComparatorPorNombre(true,parametroOrdenacion));
                               adaptadorSeries.setFilter(series);
                           }
                       }else if(radioFecha.isChecked()){
                           parametroOrdenacion=Common.FECHA_EMISION;
                           if(listaFiltrada!=null){
                               Collections.sort(listaFiltrada, new SerieComparatorPorNombre(true,parametroOrdenacion));
                               adaptadorSeries.setFilter(listaFiltrada);
                           }else{
                               Collections.sort(series, new SerieComparatorPorNombre(true,parametroOrdenacion));
                               adaptadorSeries.setFilter(series);
                           }
                       }else if(radioLikes.isChecked()){
                           parametroOrdenacion=Common.LIKES;
                           if(listaFiltrada!=null){
                               Collections.sort(listaFiltrada, new SerieComparatorPorNombre(true,parametroOrdenacion));
                               adaptadorSeries.setFilter(listaFiltrada);
                           }else{
                               Collections.sort(series, new SerieComparatorPorNombre(true,parametroOrdenacion));
                               adaptadorSeries.setFilter(series);
                           }

                       }
                       if(listaFiltrada!=null){
                           adaptadorSeries.setFilter(ordenarSeriesPaises(listaFiltrada, (String) spinnerPaises.getSelectedItem()));
                       }else{
                           adaptadorSeries.setFilter(ordenarSeriesPaises(series, (String) spinnerPaises.getSelectedItem()));
                       }



                       if(spinnerOrdenar.getSelectedItem().equals(Common.ASCENDENTE)){
                           lm.setReverseLayout(false);
                           lm.setStackFromEnd(false);
                       }else if(spinnerOrdenar.getSelectedItem().equals(Common.DESCENDENTE)){
                           lm.setReverseLayout(true);
                           lm.setStackFromEnd(true);
                       }
                       dialog.dismiss();
                   }
               });

               botonCancelar=vista.findViewById(R.id.botonCancelar);
               botonCancelar.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       dialog.dismiss();
                   }
               });

                mbuilder.setView(vista);
                dialog = mbuilder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
         lm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(lm);
         pasadoTutorialShowcaseBusca=false;
        pasadoTutorialShowcaseBusca2=false;
        vp = getActivity().findViewById(R.id.container);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==1 && isShowedTuturial){
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(Common.TUTORIAL_SERIES,false);
                    editor.commit();
                    isShowedTuturial=false;
                    showcaseView2=new ShowcaseView.Builder(getActivity())
                            .setTarget(new CustomViewTarget(R.id.showcase_button_series,-75,100,getActivity()))
                            .setContentTitle(getString(R.string.series_showcase))
                            .setStyle(R.style.CustomShowcaseTheme3)
                            .setContentText(getString(R.string.showcase_pulsa))
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showcaseView2.hide();
                                    showcaseView2=null;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                                        showcaseView = new ShowcaseView.Builder(getActivity())
                                                .setTarget(new CustomViewTarget(R.id.toolbar, 280, -100, getActivity()))
                                                .setContentTitle(getString(R.string.series_showcase))
                                                .setStyle(R.style.CustomShowcaseTheme2)
                                                .setContentText(getString(R.string.texto_showcase_series))
                                                .setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        showcaseView.hide();
                                                        showcaseView=null;
                                                        SharedPreferences.Editor editor = sharedPref.edit();
                                                        editor.putBoolean(Common.TUTORIAL_FAVORITOS, false);
                                                        editor.commit();
                                                    }
                                                })
                                                .build();
                                    }else{
                                            vp.setCurrentItem(2);
                                    }

                                }
                            })
                            .build();


                }
                if(position==2 && showcaseView!=null){
                    showcaseView.hide();
                    if(showcaseView2!=null){
                        showcaseView2.hide();
                    }

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        series=new ArrayList<>();
        adaptadorSeries=new AdaptadorSeries(series,this.getContext(),vp,vista);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_tab, menu);
        final MenuItem item = menu.findItem(R.id.search);
        searchView = (SearchView) item.getActionView();
        searchView.setQueryHint(getString(R.string.busca));
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try{
                    listaFiltrada=filter(series,newText);
                    adaptadorSeries.setFilter(listaFiltrada);
                }catch (Exception e){

                }
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showcaseView!=null && !pasadoTutorialShowcaseBusca){
                    pasadoTutorialShowcaseBusca=true;
                    showcaseView.hide();
                    showcaseView = new ShowcaseView.Builder(getActivity())
                            .setTarget(new CustomViewTarget(R.id.toolbar, -200, -100, getActivity()))
                            .setContentTitle(getString(R.string.series_showcase))
                            .setStyle(R.style.CustomShowcaseTheme2)
                            .setContentText(getString(R.string.texto_showcase_series))
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
                serie2= EliminaAcentos.eliminarAcentos(serie2);
                if(serie2.contains(texto)){
                    listaFiltrada.add(serie);
                }
            }
        }catch (Exception e){

        }
        if(listaFiltrada.size()==1 && showcaseView!=null && !pasadoTutorialShowcaseBusca2 ){
            ((TabActivity)getActivity()).esconderTeclado(searchEditText);
            pasadoTutorialShowcaseBusca2=true;
            showcaseView.hide();
            showcaseView = new ShowcaseView.Builder(getActivity())
                    .setTarget(new CustomViewTarget(R.id.textViewOptionsDigit, 0, 0, getActivity()))
                    .setContentTitle(getString(R.string.favoritos_showcase))
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .setContentText(getString(R.string.anade_serie_fav))
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showcaseView.hide();
                            showcaseView=null;
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean(Common.TUTORIAL_FAVORITOS, false);
                            editor.commit();
                        }
                    })
                    .build();
        }
        return listaFiltrada;
    }

    private List<Series> ordenarSeriesPaises(List<Series> lista,String pais){
        List<Series> listaFiltro=new ArrayList<>();
        String paisSeleccionado="";
        if(pais.equals("Todos los paises")){
            return lista;
        }

        switch (pais){
            case "\uD83C\uDDFA\uD83C\uDDF8 United States":
                paisSeleccionado="US";
                break;
            case "\uD83C\uDDEA\uD83C\uDDF8 España":
                paisSeleccionado="ES";
                break;
            case "\uD83C\uDDEC\uD83C\uDDE7 Gran Bretaña":
                paisSeleccionado="GB";
                break;
            case "\uD83C\uDDF2\uD83C\uDDFD México":
                paisSeleccionado="MX";
                break;
            case "\uD83C\uDDEB\uD83C\uDDF7 Francia":
                paisSeleccionado="FR";
                break;
            case "\uD83C\uDDE8\uD83C\uDDE6 Canadá":
                paisSeleccionado="CA";
                break;
            default:
                paisSeleccionado="desconocido";
                break;

        }

        for (int i = 0; i <lista.size() ; i++) {
            Series serie = lista.get(i);
            if(!paisSeleccionado.equals("desconocido")){
                if(serie.getPaises()!=null){
                    if(serie.getPaises().get(0).equals(paisSeleccionado)){
                        listaFiltro.add(serie);
                    }

                }
            }else{
                if(serie.getPaises()!=null){
                    if(!serie.getPaises().get(0).equals("US") && !serie.getPaises().get(0).equals("ES") && !serie.getPaises().get(0).equals("GB")
                            && !serie.getPaises().get(0).equals("MX") && !serie.getPaises().get(0).equals("FR") && !serie.getPaises().get(0).equals("CA")){
                        listaFiltro.add(serie);
                    }
                }
            }

        }
        return listaFiltro;
    }


}
