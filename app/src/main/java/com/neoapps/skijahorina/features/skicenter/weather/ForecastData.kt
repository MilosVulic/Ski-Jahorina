package com.neoapps.skijahorina.features.skicenter.weather


data class ForecastData(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherDataForecast>,
    val city: City
)
data class WeatherDataForecast(
    val dt: Long,
    val main: MainWeather,
    val weather: List<WeatherDescription>,
    val clouds: CloudsWeather,
    val wind: WindWeather,
    val visibility: Int,
    val pop: Double,
    val rain: RainWeather? = null,
    val snow: SnowWeather? = null,
    val sys: SysWeather,
    val dt_txt: String
)

data class MainWeather(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Int,
    val temp_kf: Double
)

data class WeatherDescription(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Clouds(
    val all: Int
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
)

data class Rain(
    val `3h`: Double
)

data class Snow(
    val `3h`: Double
)

data class Sys(
    val pod: String
)

data class City(
    val id: Int,
    val name: String,
    val coord: Coordinates,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

data class Coordinates(
    val lat: Double,
    val lon: Double
)
