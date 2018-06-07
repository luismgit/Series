package com.maniac.luis.series.actividades;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.maniac.luis.series.Adapters.AdaptadorCreditosActores;
import com.maniac.luis.series.Adapters.AdaptadorCreditosProduccionActores;
import com.maniac.luis.series.MovieDbInterface.ApiInterfaceActores;
import com.maniac.luis.series.MovieDbInterface.ApiInterfaceCreditosActores;
import com.maniac.luis.series.MovieDbInterface.ApiInterfaceInfoActores;
import com.maniac.luis.series.MovieDbInterface.ApiInterfaceSocialActor;
import com.maniac.luis.series.MovieDbInterface.SeriesActoresInfoResult;
import com.maniac.luis.series.MovieDbInterface.SeriesActoresResult;
import com.maniac.luis.series.MovieDbInterface.SeriesCreditosActorResult;
import com.maniac.luis.series.MovieDbInterface.SeriesSocialActorResult;
import com.maniac.luis.series.R;
import com.maniac.luis.series.utilidades.Common;
import com.maniac.luis.series.utilidades.CustomViewTarget;

import java.util.ArrayList;
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
    TextView nombreActor,nacimentoActor,biografiaActor,lugarNacimiento,sinCreditos,textViewInterpretacion,textViewProduccion;
    Retrofit retrofit;
    List<SeriesCreditosActorResult.CastBean> papelesActor;
    List<SeriesCreditosActorResult.CrewBean> papelesProduccion;
    RecyclerView rvPapelesActor,rvPapelesProduccion;
    AdaptadorCreditosActores adaptadorCreditosActores;
    AdaptadorCreditosProduccionActores adaptadorCreditosProduccionActores;
    List<String> papelesRepetidos;
    ApiInterfaceActores interface1;
    ImageView iconoFacebook,iconoInstagram;
    ApiInterfaceSocialActor apiInterfaceSocialActor;
    String facebookPageID,instagramId;
    ShowcaseView showcaseViewRedes,showcaseViewPapeles;
    ScrollView scrollView;
    SharedPreferences sharedPref;
    boolean entraShowCasePapeles =false;
    boolean entraShowCaseProduccion =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_actor);
        imagenActor=findViewById(R.id.imagenActor);
        scrollView=findViewById(R.id.scrollViewSerie);
        nombreActor=findViewById(R.id.nombreActor);
        nacimentoActor=findViewById(R.id.nacimientoActor);
        biografiaActor=findViewById(R.id.biografia);
        lugarNacimiento=findViewById(R.id.lugarNacimiento);
        rvPapelesActor=findViewById(R.id.recyclerPapelesActor);
        rvPapelesProduccion=findViewById(R.id.recyclerProduccionActor);
        sinCreditos=findViewById(R.id.sinCreditos);
        textViewInterpretacion=findViewById(R.id.comoInterpretacion);
        textViewProduccion=findViewById(R.id.comoProduccion);
        papelesRepetidos=new ArrayList<>();
        sharedPref = getSharedPreferences(Common.TUTORIAL_PREF, Context.MODE_PRIVATE);
        iconoFacebook=findViewById(R.id.iconoFacebook);
        iconoFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              OpenFacebookPage();

            }
        });
        iconoInstagram=findViewById(R.id.iconoInstagram);
        iconoInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OpenInstagram();
            }
        });
        actor= (SeriesActoresResult.CastBean) getIntent().getExtras().getSerializable(Common.SERIE_OBJETO);
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
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)biografiaActor.getLayoutParams();
                    params.setMargins(0,0,0,500);
                    biografiaActor.setLayoutParams(params);
                }
            }

            @Override
            public void onFailure(Call<SeriesActoresInfoResult> call, Throwable t) {

            }
        });

        ApiInterfaceSocialActor interfaceSocialActor = retrofit.create(ApiInterfaceSocialActor.class);
        Call<SeriesSocialActorResult> callSocial = interfaceSocialActor.listOfSocialActor(actor.getId(),Common.API_KEY_MOVIE_DB,"es");
        callSocial.enqueue(new Callback<SeriesSocialActorResult>() {
            @Override
            public void onResponse(Call<SeriesSocialActorResult> call, Response<SeriesSocialActorResult> response) {
                SeriesSocialActorResult redesSociales = response.body();
                boolean tieneFaceBook=false;
                if(redesSociales==null){
                    iconoFacebook.setVisibility(View.INVISIBLE);
                    iconoInstagram.setVisibility(View.INVISIBLE);
                }else{
                    if(redesSociales.getFacebook_id()!=null && !redesSociales.getFacebook_id().equals("")){
                        iconoFacebook.setVisibility(View.VISIBLE);
                        facebookPageID=redesSociales.getFacebook_id();
                        tieneFaceBook=true;
                        if(sharedPref.getBoolean(Common.TUTORIAL_ACTOR_REDES_SOCIALES,true)){
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean(Common.TUTORIAL_ACTOR_REDES_SOCIALES,false);
                            editor.commit();
                            showcaseViewRedes = new ShowcaseView.Builder(InfoActorActivity.this)
                                    .setTarget(new CustomViewTarget(R.id.iconoFacebook,0,0,InfoActorActivity.this))
                                    .setContentTitle(getString(R.string.title_showcase_redes))
                                    .hideOnTouchOutside()
                                    .setStyle(R.style.CustomShowcaseTheme2)
                                    .setContentText(getString(R.string.content_showcase_redes))
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            showcaseViewRedes.hide();
                                        }
                                    })
                                    .build();
                        }
                    }else{
                        iconoFacebook.setVisibility(View.INVISIBLE);
                    }
                    if(redesSociales.getInstagram_id()!=null && !redesSociales.getInstagram_id().equals("")){
                        if(sharedPref.getBoolean(Common.TUTORIAL_ACTOR_REDES_SOCIALES,true) && !tieneFaceBook){
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean(Common.TUTORIAL_ACTOR_REDES_SOCIALES,false);
                            editor.commit();
                            showcaseViewRedes = new ShowcaseView.Builder(InfoActorActivity.this)
                                    .setTarget(new CustomViewTarget(R.id.iconoInstagram,0,0,InfoActorActivity.this))
                                    .setContentTitle(getString(R.string.title_showcase_redes))
                                    .hideOnTouchOutside()
                                    .setStyle(R.style.CustomShowcaseTheme2)
                                    .setContentText(getString(R.string.content_showcase_redes))
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            showcaseViewRedes.hide();
                                        }
                                    })
                                    .build();
                        }
                        iconoInstagram.setVisibility(View.VISIBLE);
                        instagramId=redesSociales.getInstagram_id();
                    }else{
                        iconoInstagram.setVisibility(View.INVISIBLE);
                    }

                }
            }

            @Override
            public void onFailure(Call<SeriesSocialActorResult> call, Throwable t) {

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
                boolean papeles=false;
                boolean produccion=false;

                if(creditos==null){
                    sinCreditos.setVisibility(View.VISIBLE);
                    textViewInterpretacion.setVisibility(View.GONE);
                }else{
                    sinCreditos.setVisibility(View.GONE);

                    if( papelesActor==null || papelesActor.size()==0){
                        textViewInterpretacion.setVisibility(View.GONE);
                    }else{
                        papeles=true;
                        textViewInterpretacion.setVisibility(View.VISIBLE);
                        adaptadorCreditosActores=new AdaptadorCreditosActores(papelesActor,InfoActorActivity.this,actor.getProfile_path());
                        rvPapelesActor.setAdapter(adaptadorCreditosActores);
                        rvPapelesActor.setLayoutManager(new GridLayoutManager(InfoActorActivity.this,2));
                        if(sharedPref.getBoolean(Common.TUTORIAL_ACTOR_PAPELES,true)){

                            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                                @Override
                                public void onScrollChanged() {
                                    Rect rectf = new Rect();
                                    rvPapelesActor.getLocalVisibleRect(rectf);
                                    rvPapelesActor.getGlobalVisibleRect(rectf);
                                    if(rectf.top<1400 && !entraShowCasePapeles){
                                        entraShowCasePapeles=true;
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putBoolean(Common.TUTORIAL_ACTOR_PAPELES,false);
                                        editor.commit();
                                        showcaseViewPapeles = new ShowcaseView.Builder(InfoActorActivity.this)
                                                .setTarget(Target.NONE)
                                                .setContentTitle(getString(R.string.title_showcase_creditos))
                                                .setStyle(R.style.CustomShowcaseTheme2Actores)
                                                .setContentText(getString(R.string.content_showcase_creditos))
                                                .setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        showcaseViewPapeles.hide();
                                                    }
                                                })
                                                .build();
                                    }
                                }
                            });
                        }
                    }

                    if( papelesProduccion==null || papelesProduccion.size()==0 ){
                       textViewProduccion.setVisibility(View.GONE);
                    }else{
                        textViewProduccion.setVisibility(View.VISIBLE);
                        adaptadorCreditosProduccionActores=new AdaptadorCreditosProduccionActores(papelesProduccion,InfoActorActivity.this);
                        rvPapelesProduccion.setAdapter(adaptadorCreditosProduccionActores);
                        rvPapelesProduccion.setLayoutManager(new GridLayoutManager(InfoActorActivity.this,2));
                        if(sharedPref.getBoolean(Common.TUTORIAL_ACTOR_PAPELES,true) && !papeles){

                            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                                @Override
                                public void onScrollChanged() {
                                    Rect rectf = new Rect();
                                    rvPapelesProduccion.getLocalVisibleRect(rectf);
                                    rvPapelesProduccion.getGlobalVisibleRect(rectf);
                                    if(rectf.top<1400 && !entraShowCaseProduccion){
                                        entraShowCaseProduccion=true;
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putBoolean(Common.TUTORIAL_ACTOR_PAPELES,false);
                                        editor.commit();
                                        showcaseViewPapeles = new ShowcaseView.Builder(InfoActorActivity.this)
                                                .setTarget(Target.NONE)
                                                .setContentTitle(getString(R.string.title_showcase_creditos))
                                                .setStyle(R.style.CustomShowcaseTheme2Actores)
                                                .setContentText(getString(R.string.content_showcase_creditos))
                                                .setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        showcaseViewPapeles.hide();
                                                    }
                                                })
                                                .build();
                                    }
                                }
                            });
                        }
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

    protected void OpenFacebookPage(){

        String facebookUrl = "https://www.facebook.com/" + facebookPageID;

        String facebookUrlScheme = "fb://page/" + facebookPageID;

        try {
            int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;

            if (versionCode >= 3002850) {
                Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrlScheme)));
            }
        } catch (PackageManager.NameNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));

        }

    }

    protected void OpenInstagram(){
        Uri uri = Uri.parse("http://instagram.com/_u/" + instagramId);
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

        likeIng.setPackage("com.instagram.android");

        try {
            startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/xxx")));
        }
    }




}
