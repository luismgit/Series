package com.maniac.luis.series.MovieDbInterface;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterfaceInfoActores {

    @GET("/3/person/{id}")
    Call<SeriesActoresInfoResult> listOfSeriesActoresInfo(
            @Path("id") int id,
            @Query("api_key") String api_Key,
            @Query("language") String language

    );
}
