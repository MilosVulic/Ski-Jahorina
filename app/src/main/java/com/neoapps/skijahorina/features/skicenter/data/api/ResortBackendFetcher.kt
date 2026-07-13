package com.neoapps.skijahorina.features.skicenter.data.api

import android.util.Log
import com.neoapps.skijahorina.BuildConfig
import com.neoapps.skijahorina.common.AppAnalytics
import com.neoapps.skijahorina.features.skicenter.JahorinaWeatherData
import com.neoapps.skijahorina.features.skicenter.applyLiftsToCache
import com.neoapps.skijahorina.features.skicenter.applyWeatherToCache
import com.neoapps.skijahorina.features.skicenter.data.ResortRefreshPolicy
import com.neoapps.skijahorina.features.skicenter.lifts.LiftInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ResortBackendFetcher {

    private const val TAG = "JahorinaApi"

    private val api: ResortApiService by lazy {
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)

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

    suspend fun refreshAll(): Boolean = withContext(Dispatchers.IO) {
        if (!ResortRefreshPolicy.isAnyStale()) {
            return@withContext true
        }
        try {
            val response = api.getResort(ResortBackendConfig.RESORT_SLUG)
            if (!response.isSuccessful) {
                logDebug("Backend failed HTTP ${response.code()}")
                logFailure("resort_api", response.code())
                return@withContext false
            }
            val body = response.body() ?: run {
                logFailure("resort_api_empty_body")
                return@withContext false
            }
            applyToCache(body)
            logDebug("Backend OK — cached Jahorina data on device")
            true
        } catch (e: Exception) {
            logDebug("Backend error: ${e.message}")
            AppAnalytics.recordException(e, "Resort API failed", mapOf("resort" to "jahorina"))
            AppAnalytics.logDataFetchFailed("resort_api")
            false
        }
    }

    suspend fun refreshWeather(): JahorinaWeatherData? = withContext(Dispatchers.IO) {
        if (!ResortRefreshPolicy.isWeatherStale()) {
            return@withContext JahorinaWeatherData.fromCache()
        }
        if (!refreshAll()) return@withContext null
        JahorinaWeatherData.fromCache()
    }

    suspend fun refreshLifts(): List<LiftInfo>? = withContext(Dispatchers.IO) {
        if (!ResortRefreshPolicy.isLiftsStale()) {
            return@withContext com.neoapps.skijahorina.features.skicenter.weather.parseCachedLifts()
        }
        if (!refreshAll()) return@withContext null
        com.neoapps.skijahorina.features.skicenter.weather.parseCachedLifts()
    }

    private fun applyToCache(data: ResortApiResponse) {
        val weather = JahorinaWeatherData(
            temperature = data.temperature,
            wind = data.wind,
            snow = data.snow,
            weatherImage = data.weatherImage,
            forecast = com.neoapps.skijahorina.features.skicenter.weather.parseForecastDays(data.forecast)
        )
        applyWeatherToCache(weather, data.forecast)

        val lifts = data.lifts.split('|').mapNotNull { entry ->
            val values = entry.split(',')
            if (values.size < 4) return@mapNotNull null
            LiftInfo(values[0], values[1], values[2], values[3])
        }
        if (lifts.isNotEmpty()) {
            applyLiftsToCache(lifts)
        }

        data.updatedAt?.takeIf { it.isNotBlank() }?.let {
            com.neoapps.skijahorina.common.PreferenceProvider.resortApiUpdatedAt = it
        }
    }

    private fun logFailure(source: String, httpCode: Int? = null) {
        AppAnalytics.logDataFetchFailed(source, httpCode)
        AppAnalytics.recordFailure(
            "Resort API failure ($source)",
            buildMap {
                put("source", source)
                put("resort", "jahorina")
                httpCode?.let { put("http_code", it.toString()) }
            }
        )
    }

    private fun logDebug(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message)
        }
    }
}
