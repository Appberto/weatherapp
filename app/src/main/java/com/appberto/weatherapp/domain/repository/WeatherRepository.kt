package com.appberto.weatherapp.domain.repository

import com.appberto.weatherapp.domain.model.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getWeatherForCity(cityName: String): Result<Weather>
    fun getWeatherUpdates(cityName: String): Flow<Weather>
}
