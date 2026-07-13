package com.neoapps.skijahorina.features.skicenter.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ResortApiService {

    @GET("v1/resorts/{slug}")
    suspend fun getResort(@Path("slug") slug: String): Response<ResortApiResponse>

    @GET("v1/ads-config/{appId}")
    suspend fun getAdsConfig(@Path("appId") appId: String): Response<com.neoapps.skijahorina.common.ads.AdsConfigResponse>
}
