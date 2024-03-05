package com.example.proba.API;


import retrofit2.Call;
import retrofit2.http.GET;

public interface IncivismeAPI {
    @GET("valenbisi")
    Call<CitibikesResult> status();
}
