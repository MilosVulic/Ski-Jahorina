package com.neoapps.skijahorina.features.skicenter.weather

data class WeatherData(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val windWeather: WindWeather,
    val rainWeather: RainWeather?,
    val snowWeather: SnowWeather?,
    val cloudsWeather: CloudsWeather,
    val dt: Long,
    val sysWeather: SysWeather,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)

data class Coord(
    val lon: Double,
    val lat: Double
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int,
    val sea_level: Int,
    val grnd_level: Int
)

data class WindWeather(
    val speed: Double,
    val deg: Int,
    val gust: Double
)

data class RainWeather(
    val `1h`: Double
)

data class SnowWeather(
    val `1h`: Double
)

data class CloudsWeather(
    val all: Int
)

data class SysWeather(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)