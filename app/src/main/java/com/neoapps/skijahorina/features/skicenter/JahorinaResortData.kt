package com.neoapps.skijahorina.features.skicenter

import com.neoapps.skijahorina.common.PreferenceProvider
import com.neoapps.skijahorina.common.SnowDepthFormatter
import com.neoapps.skijahorina.features.skicenter.lifts.LiftInfo
import com.neoapps.skijahorina.features.skicenter.weather.ForecastDay
import com.neoapps.skijahorina.features.skicenter.weather.parseForecastDays
import java.time.LocalDateTime

data class JahorinaWeatherData(
    val temperature: String,
    val wind: String,
    val snow: String,
    val weatherImage: String,
    val forecast: List<ForecastDay>
) {
    val hasData: Boolean
        get() = temperature.isNotBlank() || forecast.isNotEmpty()

    companion object {
        fun fromCache(): JahorinaWeatherData = JahorinaWeatherData(
            temperature = PreferenceProvider.temperature,
            wind = PreferenceProvider.wind,
            snow = PreferenceProvider.snow,
            weatherImage = PreferenceProvider.weatherImage,
            forecast = parseForecastDays(PreferenceProvider.forecast)
        )
    }
}

data class JahorinaLiftData(
    val lifts: List<LiftInfo>
) {
    companion object {
        fun fromCache(): JahorinaLiftData = JahorinaLiftData(
            lifts = com.neoapps.skijahorina.features.skicenter.weather.parseCachedLifts()
        )
    }
}

fun applyWeatherToCache(data: JahorinaWeatherData, forecastPipe: String) {
    PreferenceProvider.temperature = data.temperature
    PreferenceProvider.wind = data.wind
    PreferenceProvider.snow = SnowDepthFormatter.normalizeForStorage(data.snow)
    PreferenceProvider.weatherImage = data.weatherImage
    PreferenceProvider.forecast = forecastPipe
    PreferenceProvider.lastWeatherFetchTime = LocalDateTime.now().toString()
}

fun applyLiftsToCache(lifts: List<LiftInfo>) {
    PreferenceProvider.liftsJahorina = lifts.joinToString("|") {
        "${it.name},${it.type},${it.inFunction},${it.openingHours}"
    }
    PreferenceProvider.lastLiftInfoJahorinaFetchTime = LocalDateTime.now().toString()
}
