package com.appberto.weatherapp.domain.usecase

import com.appberto.weatherapp.domain.model.Weather
import com.appberto.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(cityName: String): Result<Weather> {
        return repository.getWeatherForCity(cityName)
    }

    fun getWeatherUpdates(cityName: String) = repository.getWeatherUpdates(cityName)
}
