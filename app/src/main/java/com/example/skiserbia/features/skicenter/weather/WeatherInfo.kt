package com.example.skiserbia.features.skicenter.weather

data class WeatherInfo(
    val location: String,
    val temperature: String,
    val snowHeight: String,
    val windSpeed: String,
    val forecastDays: List<ForecastDay>
)