package com.neoapps.skijahorina.features.skicenter.weather

data class ForecastDay(
    val day: String,
    val date: String,
    val maxTemp: String,
    val minTemp: String,
    val windSpeed: String,
    val image: String
)
