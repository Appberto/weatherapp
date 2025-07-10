package com.appberto.weatherapp.data.repository

import com.appberto.weatherapp.data.api.WeatherApiService
import com.appberto.weatherapp.domain.model.Weather
import com.appberto.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApiService
) : WeatherRepository {
    override suspend fun getWeatherForCity(cityName: String): Result<Weather> {
        return try {
            val response = api.getWeather(cityName = cityName)
            Result.success(
                Weather(
                    temperature = response.current.tempC,
                    humidity = response.current.humidity,
                    description = response.current.condition.text,
                    cityName = response.location.name
                )
            )
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is UnknownHostException -> "No hay conexión a internet"
                is HttpException -> when (e.code()) {
                    400 -> "Ciudad no encontrada"
                    401 -> "Error de autenticación con la API"
                    429 -> "Demasiadas peticiones a la API"
                    else -> "Error del servidor: ${e.code()}"
                }
                else -> "Error desconocido: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    override fun getWeatherUpdates(cityName: String): Flow<Weather> = flow {
        while (true) {
            try {
                val response = api.getWeather(cityName = cityName)
                emit(
                    Weather(
                        temperature = response.current.tempC,
                        humidity = response.current.humidity,
                        description = response.current.condition.text,
                        cityName = response.location.name
                    )
                )
            } catch (e: Exception) {
                // En caso de error en las actualizaciones automáticas, podemos emitir el error
                // pero seguimos el flujo para intentar la siguiente actualización
                val errorMessage = when (e) {
                    is UnknownHostException -> "No hay conexión a internet"
                    is HttpException -> when (e.code()) {
                        400 -> "Ciudad no encontrada"
                        401 -> "Error de autenticación con la API"
                        429 -> "Demasiadas peticiones a la API"
                        else -> "Error del servidor: ${e.code()}"
                    }
                    else -> "Error desconocido: ${e.message}"
                }
                throw Exception(errorMessage)
            }
            kotlinx.coroutines.delay(300000) // Actualizar cada 5 minutos
        }
    }
}
