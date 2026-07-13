package com.neoapps.skijahorina.features.skicenter.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neoapps.skijahorina.features.skicenter.JahorinaDataFetcher
import com.neoapps.skijahorina.features.skicenter.JahorinaWeatherData
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val _weatherData = MutableLiveData(JahorinaWeatherData.fromCache())
    val weatherData: LiveData<JahorinaWeatherData> = _weatherData

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    fun loadWeather() {
        if (_isRefreshing.value == true) return
        viewModelScope.launch {
            _isRefreshing.value = true
            _weatherData.value = JahorinaWeatherData.fromCache()
            _weatherData.value = JahorinaDataFetcher.refreshWeather()
            _isRefreshing.value = false
        }
    }
}
