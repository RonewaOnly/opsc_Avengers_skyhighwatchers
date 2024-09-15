package com.example.skyhigh_prototype.Intent

import retrofit2.http.GET
import retrofit2.http.Query

interface MapboxApiService {
    @GET("geocoding/v5/mapbox.places/{query}.json")
    suspend fun searchLocation(
        @Query("key") accessToken: String,
        @Query("query") query: String
    )
}