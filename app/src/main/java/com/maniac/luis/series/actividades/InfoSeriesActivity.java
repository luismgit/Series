package com.maniac.luis.series.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.maniac.luis.series.MovieDbInterface.ApiInterfaceDetailsSerie;
import com.maniac.luis.series.MovieDbInterface.ApiInterfaceVideos;
import com.maniac.luis.series.MovieDbInterface.SeriesDetailsResult;
import com.maniac.luis.series.MovieDbInterface.SeriesVideoResult;
import com.maniac.luis.series.Objetos.Series;
import com.maniac.luis.series.R;
import com.maniac.luis.series.utilidades.Common;

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
    Series serie;
    String idYoutube;
    TextView tituloSerie,tituloOriginal,fechaEmision,paisOrigen,lenguaOriginal,sinopsis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_series);

        tituloSerie=findViewById(R.id.tituloSerie);
        tituloOriginal=findViewById(R.id.tituloOriginal);
        fechaEmision=findViewById(R.id.fechaEmision);
        paisOrigen=findViewById(R.id.paisOrigen);
        lenguaOriginal=findViewById(R.id.lenguaOriginal);
        sinopsis=findViewById(R.id.sinopsis);
        serie = (Series) getIntent().getExtras().getSerializable(Common.SERIE_OBJETO);
        tituloSerie.setText(serie.getNombre());
        if(serie.getNombreOriginal().equals("") || serie.getNombreOriginal()==null){
            tituloOriginal.setText(R.string.desconocido);
        }else{
            tituloOriginal.setText(serie.getNombre());
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
                String pais = paises.get(i).toUpperCase();
                if(pais.equals("US")){
                    paisOrigen.append("United States ");
                }else if(pais.equals("GB")){
                    paisOrigen.append("Gran Bretaña ");
                }else if(pais.equals("PT")){
                    paisOrigen.append("Portugal ");
                }else if(pais.equals("BR")){
                    paisOrigen.append("Brasil ");
                }else if(pais.equals("CN")){
                    paisOrigen.append("China ");
                }else if(pais.equals("ES")){
                    paisOrigen.append("España ");
                }else if(pais.equals("CA")){
                    paisOrigen.append("Canadá ");
                }else if(pais.equals("MX")){
                    paisOrigen.append("México ");
                }else if(pais.equals("FR")){
                    paisOrigen.append("Francia ");
                }else if(pais.equals("TR")){
                    paisOrigen.append("Turquía ");
                }else if(pais.equals("SE")){
                    paisOrigen.append("Suecia ");
                }else if(pais.equals("IT")){
                    paisOrigen.append("Italia ");
                }else{
                    paisOrigen.append(pais);
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

            }

            @Override
            public void onFailure(Call<SeriesDetailsResult> call, Throwable t) {

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
            Toast.makeText(this,"Error al inicializar Youtube"+youTubeInitializationResult.toString(),Toast.LENGTH_SHORT).show();
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
