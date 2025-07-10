package com.appberto.weatherapp.domain.model

data class Weather(
    val temperature: Double,
    val humidity: Int,
    val description: String,
    val cityName: String
)
