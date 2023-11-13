package com.example.skiserbia.web

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Url

interface WebScrapingService {

    @GET
    fun scrapeWebPage(@Url url: String): Call<ResponseBody>

    @GET("xml/weather2.php")
    @Headers("User-Agent: \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36\"\n")
    fun scrapeKopaonikWeatherWebPage(): Call<ResponseBody>

    @GET("xml/weather2.php?code=stp")
    @Headers("User-Agent: \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36\"\n")
    fun scrapeStaraPlaninaWeatherWebPage(): Call<ResponseBody>

    @GET("xml/weather2.php?code=zla")
    @Headers("User-Agent: \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36\"\n")
    fun scrapeTornikWeatherWebPage(): Call<ResponseBody>
}