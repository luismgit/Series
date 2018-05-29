package com.maniac.luis.series.actividades;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.FirebaseDatabase;
import com.maniac.luis.series.Adapters.AdaptadorActores;
import com.maniac.luis.series.MovieDbInterface.ApiInterfaceActores;
import com.maniac.luis.series.MovieDbInterface.ApiInterfaceDetailsSerie;
import com.maniac.luis.series.MovieDbInterface.ApiInterfaceVideos;
import com.maniac.luis.series.MovieDbInterface.SeriesActoresResult;
import com.maniac.luis.series.MovieDbInterface.SeriesDetailsResult;
import com.maniac.luis.series.MovieDbInterface.SeriesVideoResult;
import com.maniac.luis.series.Objetos.Series;
import com.maniac.luis.series.R;
import com.maniac.luis.series.utilidades.Common;
import com.maniac.luis.series.utilidades.NetworkStatus;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InfoSeriesActivity extends YouTubeBaseActivity  implements YouTubePlayer.OnInitializedListener{


    YouTubePlayerView player;
    Retrofit retrofit;
    ApiInterfaceVideos interfaceVideos;
    ApiInterfaceDetailsSerie interfaceDetailsSerie;
    ApiInterfaceActores interfaceActores;
    Series serie;
    String idYoutube;
    TextView tituloSerie,tituloOriginal,fechaEmision,paisOrigen,lenguaOriginal,sinopsis;
    ImageView imagenDirector;
    TextView nombreDirector,duracionEpisodio,generos,numTemporadas,numEpisodios,estado;
    RecyclerView recyclerView;
    AdaptadorActores adaptadorActores;
    TextView sinActores;
    Dialog miDialogo;
    String urlImagenDirector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_series);
        if(!NetworkStatus.isConnected(InfoSeriesActivity.this)) NetworkStatus.buildDialog(InfoSeriesActivity.this).show();
        FirebaseDatabase.getInstance().goOnline();
        tituloSerie=findViewById(R.id.tituloSerie);
        tituloOriginal=findViewById(R.id.tituloOriginal);
        fechaEmision=findViewById(R.id.fechaEmision);
        paisOrigen=findViewById(R.id.paisOrigen);
        lenguaOriginal=findViewById(R.id.lenguaOriginal);
        imagenDirector=findViewById(R.id.imagenDirector);
        nombreDirector=findViewById(R.id.nombreDirector);
        duracionEpisodio=findViewById(R.id.duracionEpisodio);
        generos=findViewById(R.id.generos);
        numEpisodios=findViewById(R.id.numEpisodios);
        numTemporadas=findViewById(R.id.numTemporadas);
        estado=findViewById(R.id.estado);
        sinopsis=findViewById(R.id.sinopsis);
        recyclerView=findViewById(R.id.recyclerActores);
        sinActores=findViewById(R.id.sinActores);
        serie = (Series) getIntent().getExtras().getSerializable(Common.SERIE_OBJETO);
        tituloSerie.setText(serie.getNombre());
        if(serie.getNombreOriginal().equals("") || serie.getNombreOriginal()==null){
            tituloOriginal.setText(R.string.desconocido);
        }else{
            tituloOriginal.setText(serie.getNombreOriginal());
        }
        if(serie.getFechaEmision().equals("") || serie.getFechaEmision()==null){
            fechaEmision.setText(R.string.desconocido);
        }else{
            fechaEmision.setText(serie.getFechaEmision());
        }
        List<String> paises = serie.getPaises();
        if(paises==null){
            paisOrigen.setText(R.string.desconocido);
        }else{
            for (int i = 0; i <paises.size() ; i++) {
                boolean quitarComa=false;
                if(i==paises.size()-1){
                    quitarComa=true;
                }
                String pais = paises.get(i).toUpperCase();
                if(!quitarComa){
                    if(pais.equals("US")){
                        paisOrigen.append("\uD83C\uDDFA\uD83C\uDDF8  United States, ");
                    }else if(pais.equals("GB")){
                        paisOrigen.append("\uD83C\uDDEC\uD83C\uDDE7  Gran Bretaña, ");
                    }else if(pais.equals("PT")){
                        paisOrigen.append("\uD83C\uDDF5\uD83C\uDDF9  Portugal, ");
                    }else if(pais.equals("BR")){
                        paisOrigen.append("\uD83C\uDDE7\uD83C\uDDF7  Brasil, ");
                    }else if(pais.equals("CN")){
                        paisOrigen.append("\uD83C\uDDE8\uD83C\uDDF3  China, ");
                    }else if(pais.equals("ES")){
                        paisOrigen.append("\uD83C\uDDEA\uD83C\uDDF8  España, ");
                    }else if(pais.equals("CA")){
                        paisOrigen.append("\uD83C\uDDE8\uD83C\uDDE6  Canadá, ");
                    }else if(pais.equals("MX")){
                        paisOrigen.append("\uD83C\uDDF2\uD83C\uDDFD  México, ");
                    }else if(pais.equals("FR")){
                        paisOrigen.append("\uD83C\uDDEB\uD83C\uDDF7  Francia, ");
                    }else if(pais.equals("TR")){
                        paisOrigen.append("\uD83C\uDDF9\uD83C\uDDF7  Turquía, ");
                    }else if(pais.equals("SE")){
                        paisOrigen.append("\uD83C\uDDF8\uD83C\uDDEA  Suecia, ");
                    }else if(pais.equals("IT")){
                        paisOrigen.append("\uD83C\uDDEE\uD83C\uDDF9  Italia, ");
                    }else{
                        paisOrigen.append(pais+", ");
                    }
                }else{
                    if(pais.equals("US")){
                        paisOrigen.append("\uD83C\uDDFA\uD83C\uDDF8  United States");
                    }else if(pais.equals("GB")){
                        paisOrigen.append("\uD83C\uDDEC\uD83C\uDDE7  Gran Bretaña");
                    }else if(pais.equals("PT")){
                        paisOrigen.append("\uD83C\uDDF5\uD83C\uDDF9  Portugal");
                    }else if(pais.equals("BR")){
                        paisOrigen.append("\uD83C\uDDE7\uD83C\uDDF7  Brasil");
                    }else if(pais.equals("CN")){
                        paisOrigen.append("\uD83C\uDDE8\uD83C\uDDF3  China");
                    }else if(pais.equals("ES")){
                        paisOrigen.append("\uD83C\uDDEA\uD83C\uDDF8  España");
                    }else if(pais.equals("CA")){
                        paisOrigen.append("\uD83C\uDDE8\uD83C\uDDE6  Canadá");
                    }else if(pais.equals("MX")){
                        paisOrigen.append("\uD83C\uDDF2\uD83C\uDDFD  México");
                    }else if(pais.equals("FR")){
                        paisOrigen.append("\uD83C\uDDEB\uD83C\uDDF7  Francia");
                    }else if(pais.equals("TR")){
                        paisOrigen.append("\uD83C\uDDF9\uD83C\uDDF7  Turquía");
                    }else if(pais.equals("SE")){
                        paisOrigen.append("\uD83C\uDDF8\uD83C\uDDEA  Suecia");
                    }else if(pais.equals("IT")){
                        paisOrigen.append("\uD83C\uDDEE\uD83C\uDDF9  Italia");
                    }else{
                        paisOrigen.append(pais);
                    }
                }


            }
        }
        if(serie.getLenguajeOriginal().equals("") || serie.getLenguajeOriginal()==null){
            lenguaOriginal.setText(R.string.desconocido);
        }else{
            String lenguajeOriginal=serie.getLenguajeOriginal().toUpperCase();
            if(lenguajeOriginal.equals("EN")){
                lenguaOriginal.setText("English");
            }else if(lenguajeOriginal.equals("PT")){
                lenguaOriginal.setText("Portugués");
            }else if(lenguajeOriginal.equals("KO")){
                lenguaOriginal.setText("Koreano");
            }else if(lenguajeOriginal.equals("ZH")){
                lenguaOriginal.setText("Chino");
            }else if(lenguajeOriginal.equals("ES")){
                lenguaOriginal.setText("Español");
            }else if(lenguajeOriginal.equals("TR")){
                lenguaOriginal.setText("Turco");
            }else if(lenguajeOriginal.equals("SV")){
                lenguaOriginal.setText("Sueco");
            }else if(lenguajeOriginal.equals("FR")){
                lenguaOriginal.setText("Francés");
            }else{
                lenguaOriginal.setText(lenguajeOriginal);
            }
        }

        if(serie.getSinopsis().equals("") || serie.getSinopsis()==null){
            sinopsis.setText(R.string.desconocido);
        }else{
            sinopsis.setText(serie.getSinopsis());
        }


        retrofit = new Retrofit.Builder()
                .baseUrl(Common.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        interfaceVideos = retrofit.create(ApiInterfaceVideos.class);
        player = findViewById(R.id.player);
              Call<SeriesVideoResult> call = interfaceVideos.listOfSeriesVideos(serie.getIdMovieDb(),Common.API_KEY_MOVIE_DB,"en");
                        call.enqueue(new Callback<SeriesVideoResult>() {
                            @Override
                            public void onResponse(Call<SeriesVideoResult> call, Response<SeriesVideoResult> response) {
                                SeriesVideoResult results = response.body();
                                List<SeriesVideoResult.ResultsBean> listaVideos = results.getResults();
                                if(listaVideos.size()!=0){
                                    player.setVisibility(View.VISIBLE);
                                    idYoutube=listaVideos.get(0).getKey();
                                    player.initialize(Common.API_KEY_YOUTUBE, InfoSeriesActivity.this);
                                }
                            }

                            @Override
                            public void onFailure(Call<SeriesVideoResult> call, Throwable t) {

                            }
                        });

        interfaceDetailsSerie = retrofit.create(ApiInterfaceDetailsSerie.class);
        Call<SeriesDetailsResult> call2 = interfaceDetailsSerie.listOfSeriesDetails(serie.getIdMovieDb(),Common.API_KEY_MOVIE_DB,"en");
        call2.enqueue(new Callback<SeriesDetailsResult>() {
            @Override
            public void onResponse(Call<SeriesDetailsResult> call, Response<SeriesDetailsResult> response) {
                SeriesDetailsResult result = response.body();
                if(result!=null){
                    List<SeriesDetailsResult.CreatedByBean> creadores = result.getCreated_by();
                    if(creadores.size()!=0){
                        if(creadores.get(0).getName()!=null){
                            nombreDirector.setText(creadores.get(0).getName());
                        }else{
                            nombreDirector.setText(R.string.desconocido);
                        }
                        if(creadores.get(0).getProfile_path()!=null){
                            imagenDirector.setVisibility(View.VISIBLE);
                            urlImagenDirector=creadores.get(0).getProfile_path();
                            Glide.with(InfoSeriesActivity.this)
                                    .load(Common.BASE_URL_POSTER+creadores.get(0).getProfile_path())
                                    .fitCenter()
                                    .centerCrop()
                                    .into(imagenDirector);
                            imagenDirector.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    miDialogo=new Dialog(InfoSeriesActivity.this);
                                    ImageView imagen;
                                    miDialogo.setContentView(R.layout.image_pop_up);
                                    imagen=miDialogo.findViewById(R.id.imagenAmpliada);
                                    Glide.with(InfoSeriesActivity.this)
                                            .load(Common.BASE_URL_POSTER+urlImagenDirector)
                                            .fitCenter()
                                            .centerCrop()
                                            .into(imagen);
                                    miDialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    miDialogo.show();
                                }
                            });
                        }

                    }else{
                        nombreDirector.setText(R.string.desconocido);
                    }
                    List<Integer> minutosEpisodio = result.getEpisode_run_time();
                    if(minutosEpisodio!=null && minutosEpisodio.size()!=0){
                        duracionEpisodio.setText(minutosEpisodio.get(0)+ " " + "minutos");
                    }else{
                        duracionEpisodio.setText(R.string.desconocido);
                    }
                    List<SeriesDetailsResult.GenresBean> generosLista = result.getGenres();
                    if(generosLista!=null || generosLista.size()!=0){
                        for (int i = 0; i <generosLista.size() ; i++) {
                            if(i!=generosLista.size()-1){
                                generos.append(generosLista.get(i).getName() + ", ");
                            }else{
                                generos.append(generosLista.get(i).getName());
                            }

                        }
                    }else{
                        generos.setText(R.string.desconocido);
                    }
                    if(result.getNumber_of_episodes() != 0){
                        numEpisodios.setText(result.getNumber_of_episodes()+"");
                    }else{
                        numEpisodios.setText(R.string.desconocido);
                    }
                    if(result.getNumber_of_seasons() != 0){
                        numTemporadas.setText(result.getNumber_of_seasons()+"");
                    }else{
                        numTemporadas.setText(R.string.desconocido);
                    }
                    if(result.getStatus()!=null && !result.getStatus().equals("")){
                        if(result.getStatus().toUpperCase().equals("ENDED")){
                            estado.setText(R.string.terminada);
                        }else if(result.getStatus().toUpperCase().equals("RETURNING SERIES")){
                            estado.setText(R.string.emitiendo_o_prev);
                        }else if(result.getStatus().toUpperCase().equals("CANCELED")){
                            estado.setText(R.string.cancelada);
                        }else if(result.getStatus().toUpperCase().equals("IN PRODUCTION")){
                            estado.setText(R.string.en_produccion);
                        }else{
                            estado.setText(result.getStatus());
                        }
                    }else{
                        estado.setText(R.string.desconocido);
                    }

                }else{
                    nombreDirector.setText(R.string.desconocido);
                    duracionEpisodio.setText(R.string.desconocido);
                    generos.setText(R.string.desconocido);
                    numEpisodios.setText(R.string.desconocido);
                    numTemporadas.setText(R.string.desconocido);
                    estado.setText(R.string.desconocido);
                }
            }

            @Override
            public void onFailure(Call<SeriesDetailsResult> call, Throwable t) {

            }
        });


        interfaceActores = retrofit.create(ApiInterfaceActores.class);
        Call<SeriesActoresResult> callActores = interfaceActores.listOfSeriesActores(serie.getIdMovieDb(),Common.API_KEY_MOVIE_DB,"en");
        callActores.enqueue(new Callback<SeriesActoresResult>() {
            @Override
            public void onResponse(Call<SeriesActoresResult> call, Response<SeriesActoresResult> response) {
                SeriesActoresResult result = response.body();
                List<SeriesActoresResult.CastBean> listaActores = result.getCast();

                if(result==null || listaActores.size()==0 || listaActores==null){
                    sinActores.setVisibility(View.VISIBLE);
                }else{
                    adaptadorActores=new AdaptadorActores(listaActores,serie.getImagen(),InfoSeriesActivity.this);
                    recyclerView.setAdapter(adaptadorActores);
                    recyclerView.setLayoutManager(new GridLayoutManager(InfoSeriesActivity.this,2));
                }





            }

            @Override
            public void onFailure(Call<SeriesActoresResult> call, Throwable t) {

            }
        });




        }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if(!b){
            youTubePlayer.cueVideo(idYoutube);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        if(youTubeInitializationResult.isUserRecoverableError()){
            youTubeInitializationResult.getErrorDialog(this,1).show();
        }else{
            Toast.makeText(this,getString(R.string.error_init_youtube)+youTubeInitializationResult.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1){
            getYoutubePlayerProvider().initialize(Common.API_KEY_YOUTUBE,this);
        }
    }

    protected YouTubePlayer.Provider getYoutubePlayerProvider(){
        return player;
    }
}
