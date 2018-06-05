package com.maniac.luis.series.MovieDbInterface;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterfaceCreditosActores {

    @GET("/3/person/{id}/tv_credits")
    Call<SeriesCreditosActorResult> listOfSeriesCreditosActores(
            @Path("id") int id,
            @Query("api_key") String api_Key,
            @Query("language") String language

    );
}
