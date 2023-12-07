package com.neoapps.skiserbia.common

import com.neoapps.skiserbia.web.WebScrapingService
import retrofit2.Retrofit

object WebScarpingServiceImpl {

    fun getService(): WebScrapingService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.skijalistasrbije.rs")
            .build()

        return retrofit.create(WebScrapingService::class.java)
    }

    fun getService(url: String): WebScrapingService {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .build()

        return retrofit.create(WebScrapingService::class.java)
    }


}