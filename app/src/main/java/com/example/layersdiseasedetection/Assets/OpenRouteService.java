package com.example.layersdiseasedetection.Assets;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface OpenRouteService {
    @GET("v2/directions/driving-car")
    Call<RouteResponse> getDirections(
            @Header("Authorization") String apiKey,  // Pass API key as header
            @Query("start") String start,
            @Query("end") String end
    );
}
