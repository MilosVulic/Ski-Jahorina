package com.neoapps.skijahorina.common.ads

import android.util.Log
import com.neoapps.skijahorina.BuildConfig
import com.neoapps.skijahorina.common.AppAnalytics
import com.neoapps.skijahorina.common.ads.AdsAppIds
import com.neoapps.skijahorina.features.skicenter.data.api.ResortBackendConfig
import com.neoapps.skijahorina.features.skicenter.data.api.ResortApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object AdsConfigFetcher {

    const val CACHE_TTL_MS: Long = 3L * 24L * 60L * 60L * 1000L // 3 days

    private const val TAG = "AdsConfig"

    private val api: ResortApiService by lazy {
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(
                HttpLoggingInterceptor { message -> Log.d(TAG, message) }
                    .setLevel(HttpLoggingInterceptor.Level.BASIC)
            )
        }

        if (ResortBackendConfig.apiKey.isNotEmpty()) {
            clientBuilder.addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header(ResortBackendConfig.API_KEY_HEADER, ResortBackendConfig.apiKey)
                    .build()
                chain.proceed(request)
            }
        }

        Retrofit.Builder()
            .baseUrl(ResortBackendConfig.baseUrl)
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ResortApiService::class.java)
    }

    suspend fun refreshIfNeeded(force: Boolean = false): Boolean = withContext(Dispatchers.IO) {
        if (!ResortBackendConfig.isEnabled) return@withContext false
        if (!force && !AdsPolicyStore.isCacheStale()) return@withContext true

        try {
            val response = api.getAdsConfig(AdsAppIds.SKI_JAHORINA)
            if (!response.isSuccessful) {
                logDebug("Ads config HTTP ${response.code()}")
                AppAnalytics.logDataFetchFailed("ads_config", response.code())
                return@withContext false
            }
            val body = response.body() ?: return@withContext false
            if (body.screens.isEmpty()) return@withContext false
            AdsPolicyStore.save(body)
            logDebug("Ads config cached (${body.screens.size} screens)")
            true
        } catch (e: Exception) {
            logDebug("Ads config error: ${e.message}")
            AppAnalytics.recordException(e, "Ads config fetch failed")
            AppAnalytics.logDataFetchFailed("ads_config")
            false
        }
    }

    private fun logDebug(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message)
        }
    }
}
