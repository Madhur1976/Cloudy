package com.example.cloudy.api


import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface WeatherAPI {
    @GET("/v1/current.json")
    suspend fun getWeather(
        @Query("Key") apikey: String,
        @Query("q") city: String
    ): Response<WeatherModel>
}