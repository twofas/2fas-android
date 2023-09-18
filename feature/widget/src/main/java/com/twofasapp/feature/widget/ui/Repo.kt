package com.twofasapp.feature.widget.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

object Repo {
    private var _currentWeather = MutableStateFlow<Int>(0)
    val currentWeather: StateFlow<Int> get() = _currentWeather

    suspend fun updateWeatherInfo() {
        _currentWeather.value = Random.nextInt()
    }
}