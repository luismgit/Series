package com.maniac.luis.series.MovieDbInterface;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Luis on 06/06/2018.
 */

public interface ApiInterfaceSocialActor {

    @GET("/3/person/{id}/external_ids")
    Call<SeriesSocialActorResult> listOfSocialActor(
            @Path("id") int id,
            @Query("api_key") String api_Key,
            @Query("language") String language

    );
}
