package com.appberto.weatherapp.data.api

import com.appberto.weatherapp.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/forecast.json")
    suspend fun getWeather(
        @Query("key") apiKey: String = "3ac26800d043446d973112930250407",
        @Query("q") cityName: String,
        @Query("days") days: Int = 1,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"
    ): WeatherResponse
}
