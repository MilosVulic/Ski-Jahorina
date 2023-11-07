package com.example.skiserbia.common

import com.example.skiserbia.web.WebScrapingService
import retrofit2.Retrofit

object WebScarpingServiceImpl {

    fun getService(url: String): WebScrapingService {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .build()

        return retrofit.create(WebScrapingService::class.java)
    }
}