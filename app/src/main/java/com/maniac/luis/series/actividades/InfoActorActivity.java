package com.maniac.luis.series.actividades;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.maniac.luis.series.Adapters.AdaptadorCreditosActores;
import com.maniac.luis.series.MovieDbInterface.ApiInterfaceCreditosActores;
import com.maniac.luis.series.MovieDbInterface.ApiInterfaceInfoActores;
import com.maniac.luis.series.MovieDbInterface.SeriesActoresInfoResult;
import com.maniac.luis.series.MovieDbInterface.SeriesActoresResult;
import com.maniac.luis.series.MovieDbInterface.SeriesCreditosActorResult;
import com.maniac.luis.series.R;
import com.maniac.luis.series.utilidades.Common;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InfoActorActivity extends AppCompatActivity {

    SeriesActoresResult.CastBean actor;
    ImageView imagenActor;
    TextView nombreActor,nacimentoActor,biografiaActor,lugarNacimiento,sinCreditos;
    Retrofit retrofit;
    List<SeriesCreditosActorResult.CastBean> papelesActor;
    List<SeriesCreditosActorResult.CrewBean> papelesProduccion;
    RecyclerView rvPapelesActor,rvPapelesProduccion;
    AdaptadorCreditosActores adaptadorCreditosActores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_actor);
        imagenActor=findViewById(R.id.imagenActor);
        nombreActor=findViewById(R.id.nombreActor);
        nacimentoActor=findViewById(R.id.nacimientoActor);
        biografiaActor=findViewById(R.id.biografia);
        lugarNacimiento=findViewById(R.id.lugarNacimiento);
        rvPapelesActor=findViewById(R.id.recyclerPapelesActor);
        sinCreditos=findViewById(R.id.sinCreditos);
        actor= (SeriesActoresResult.CastBean) getIntent().getExtras().getSerializable(Common.SERIE_OBJETO);
        Log.i("actor",actor.getId()+"");
        if(actor.getProfile_path()!=null){
            Glide.with(InfoActorActivity.this)
                    .load(Common.BASE_URL_POSTER+actor.getProfile_path())
                    .centerCrop()
                    .fitCenter()
                    .into(imagenActor);
        }else{
            imagenActor.setImageResource(R.drawable.series_back);
        }

        if(actor.getName()!=null && !actor.getName().equals("")){
            nombreActor.setText(actor.getName());
        }else{
            nombreActor.setText(getString(R.string.desconocido));
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(Common.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterfaceInfoActores interfaceActores = retrofit.create(ApiInterfaceInfoActores.class);
        Call<SeriesActoresInfoResult> call = interfaceActores.listOfSeriesActoresInfo(actor.getId(),Common.API_KEY_MOVIE_DB,"es");
        call.enqueue(new Callback<SeriesActoresInfoResult>() {
            @Override
            public void onResponse(Call<SeriesActoresInfoResult> call, Response<SeriesActoresInfoResult> response) {
                SeriesActoresInfoResult detallesActor = response.body();
                if(detallesActor.getBirthday()!=null && !detallesActor.getBirthday().equals("")){
                    String anho=detallesActor.getBirthday().substring(0,4);
                    String mes=detallesActor.getBirthday().substring(5,7);
                    String dia =detallesActor.getBirthday().substring(8,10);
                    int edad=getAge(Integer.parseInt(anho),Integer.parseInt(mes),Integer.parseInt(dia));
                    nacimentoActor.setText(edad+"");
                }else{
                    nacimentoActor.setText(getString(R.string.desconocido));
                }

                if(detallesActor.getPlace_of_birth()!=null && !detallesActor.getPlace_of_birth().equals("")){
                    lugarNacimiento.setText(detallesActor.getPlace_of_birth());
                }else{
                    lugarNacimiento.setText(getString(R.string.desconocido));
                }

                if(detallesActor.getBiography()!=null && !detallesActor.getBiography().equals("")){
                    biografiaActor.setText(detallesActor.getBiography());
                }else{
                    biografiaActor.setText(getString(R.string.desconocido));
                }
            }

            @Override
            public void onFailure(Call<SeriesActoresInfoResult> call, Throwable t) {

            }
        });



        ApiInterfaceCreditosActores interfaceCreditos = retrofit.create(ApiInterfaceCreditosActores.class);
        Call<SeriesCreditosActorResult> call2 = interfaceCreditos.listOfSeriesCreditosActores(actor.getId(),Common.API_KEY_MOVIE_DB,"es");
        call2.enqueue(new Callback<SeriesCreditosActorResult>() {
            @Override
            public void onResponse(Call<SeriesCreditosActorResult> call, Response<SeriesCreditosActorResult> response) {
                SeriesCreditosActorResult creditos=response.body();
                papelesActor=creditos.getCast();
                papelesProduccion=creditos.getCrew();
                if(creditos==null){
                    sinCreditos.setVisibility(View.VISIBLE);
                }else{

                    if(papelesActor.size()==0 || papelesActor==null){
                        sinCreditos.setVisibility(View.GONE);
                    }else{
                        adaptadorCreditosActores=new AdaptadorCreditosActores(papelesActor,InfoActorActivity.this,actor.getProfile_path());
                        rvPapelesActor.setAdapter(adaptadorCreditosActores);
                        rvPapelesActor.setLayoutManager(new GridLayoutManager(InfoActorActivity.this,2));
                    }
                }


            }

            @Override
            public void onFailure(Call<SeriesCreditosActorResult> call, Throwable t) {

            }
        });


    }



    private int getAge(int year, int month, int day) {

        Date now = new Date();
        int nowMonth = now.getMonth()+1;
        int nowYear = now.getYear()+1900;
        int result = nowYear - year;

        if (month > nowMonth) {
            result--;
        }
        else if (month == nowMonth) {
            int nowDay = now.getDate();

            if (day > nowDay) {
                result--;
            }
        }
        return result;
    }


}
