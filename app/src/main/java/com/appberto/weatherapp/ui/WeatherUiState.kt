package com.appberto.weatherapp.ui

sealed class WeatherUiState {
    data object Loading : WeatherUiState()
    data class Success(val weather: com.appberto.weatherapp.domain.model.Weather) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}
