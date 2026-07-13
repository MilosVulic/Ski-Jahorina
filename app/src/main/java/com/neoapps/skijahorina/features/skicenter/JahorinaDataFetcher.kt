package com.neoapps.skijahorina.features.skicenter

import android.util.Log
import com.neoapps.skijahorina.BuildConfig
import com.neoapps.skijahorina.common.AppAnalytics
import com.neoapps.skijahorina.features.skicenter.data.api.ResortBackendConfig
import com.neoapps.skijahorina.features.skicenter.data.api.ResortBackendFetcher
import com.neoapps.skijahorina.features.skicenter.data.legacy.LegacyJahorinaDataFetcher
import com.neoapps.skijahorina.features.skicenter.lifts.LiftInfo

/**
 * Fetches Jahorina resort data via Railway when configured,
 * with automatic fallback to on-device scraping (same path older APKs always use).
 */
object JahorinaDataFetcher {

    private const val TAG = "JahorinaApi"

    /**
     * Single network pass to warm lifts + weather cache (API or legacy fallback).
     */
    suspend fun refreshAll() {
        if (ResortBackendConfig.isEnabled) {
            if (ResortBackendFetcher.refreshAll()) {
                return
            }
            logDebug("Falling back to on-device refresh for all resort data")
            AppAnalytics.logDataFetchFailed("resort_api_fallback")
        }
        LegacyJahorinaDataFetcher.refreshAll()
    }

    suspend fun refreshLifts(): List<LiftInfo> {
        if (ResortBackendConfig.isEnabled) {
            val fromApi = ResortBackendFetcher.refreshLifts()
            if (fromApi != null && fromApi.isNotEmpty()) {
                return fromApi
            }
            logDebug("Falling back to on-device lift scrape")
            AppAnalytics.logDataFetchFailed("lifts_api_fallback")
        }
        return LegacyJahorinaDataFetcher.refreshLifts()
    }

    suspend fun refreshWeather(): JahorinaWeatherData {
        if (ResortBackendConfig.isEnabled) {
            val fromApi = ResortBackendFetcher.refreshWeather()
            if (fromApi != null && fromApi.hasData) {
                return fromApi
            }
            logDebug("Falling back to on-device weather fetch")
            AppAnalytics.logDataFetchFailed("weather_api_fallback")
        }
        return LegacyJahorinaDataFetcher.refreshWeather()
    }

    private fun logDebug(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message)
        }
    }
}
