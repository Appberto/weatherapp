package com.appberto.weatherapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appberto.weatherapp.domain.usecase.GetWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun getWeather(cityName: String) {
        if (cityName.isBlank()) {
            _uiState.value = WeatherUiState.Error("Por favor, introduce una ciudad")
            return
        }

        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            getWeatherUseCase(cityName)
                .onSuccess { weather ->
                    _uiState.value = WeatherUiState.Success(weather)
                }
                .onFailure { exception ->
                    _uiState.value = WeatherUiState.Error(exception.message ?: "Error desconocido")
                }
        }
    }

    fun startWeatherUpdates(cityName: String) {
        if (cityName.isBlank()) return

        viewModelScope.launch {
            getWeatherUseCase.getWeatherUpdates(cityName)
                .catch { e ->
                    _uiState.value = WeatherUiState.Error(e.message ?: "Error en la actualización")
                }
                .collect { weather ->
                    _uiState.value = WeatherUiState.Success(weather)
                }
        }
    }

    fun retryLastQuery() {
        val currentState = uiState.value
        if (currentState is WeatherUiState.Error) {
            // Intentamos obtener el último cityName conocido del estado Success anterior
            val lastKnownCity = (uiState.value as? WeatherUiState.Success)?.weather?.cityName ?: "Madrid"
            getWeather(lastKnownCity)
        }
    }
}
