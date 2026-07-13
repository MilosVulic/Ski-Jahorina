package com.neoapps.skijahorina.features.skicenter.data.api

import com.neoapps.skijahorina.BuildConfig

object ResortBackendConfig {

    const val RESORT_SLUG = "jahorina"
    const val API_KEY_HEADER = "X-Api-Key"

    val isEnabled: Boolean
        get() = BuildConfig.RESORT_API_ENABLED && BuildConfig.RESORT_API_BASE_URL.isNotBlank()

    val baseUrl: String
        get() {
            val url = BuildConfig.RESORT_API_BASE_URL.trim()
            return if (url.endsWith("/")) url else "$url/"
        }

    val apiKey: String
        get() = BuildConfig.RESORT_API_KEY.trim()
}
