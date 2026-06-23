package com.neoapps.skijahorina.common

import com.neoapps.skijahorina.features.skicenter.weather.WeatherService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://api.openweathermap.org/"

    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val weatherApiService: WeatherService by lazy {
        retrofit.create(WeatherService::class.java)
    }
}