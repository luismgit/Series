package com.maniac.luis.series.MovieDbInterface;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiInterfaceActores {
    @GET("/3/tv/{id}/credits")
    Call<SeriesActoresResult> listOfSeriesActores(
            @Path("id") int id,
            @Query("api_key") String api_Key,
            @Query("language") String language

    );
}
