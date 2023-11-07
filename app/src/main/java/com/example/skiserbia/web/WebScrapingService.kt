package com.example.skiserbia.web

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface WebScrapingService {

    @GET
    fun scrapeWebPage(@Url url: String): Call<ResponseBody>
}