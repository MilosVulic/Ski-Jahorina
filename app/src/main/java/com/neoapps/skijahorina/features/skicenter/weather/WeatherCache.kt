package com.neoapps.skijahorina.features.skicenter.weather

import com.neoapps.skijahorina.common.PreferenceProvider
import com.neoapps.skijahorina.features.skicenter.lifts.LiftInfo

fun parseForecastDays(forecast: String): List<ForecastDay> {
    if (forecast.isBlank()) return emptyList()
    return forecast.split('|').mapNotNull { entry ->
        val values = entry.split(',')
        if (values.size < 6) return@mapNotNull null
        ForecastDay(
            day = values[0],
            date = values[1],
            maxTemp = values[2],
            minTemp = values[3],
            windSpeed = values[4],
            image = values[5]
        )
    }
}

fun parseCachedLifts(): List<LiftInfo> {
    val cached = PreferenceProvider.liftsJahorina
    if (cached.isBlank()) return emptyList()
    return cached.split('|').mapNotNull { entry ->
        val values = entry.split(',')
        if (values.size < 4) return@mapNotNull null
        LiftInfo(values[0], values[1], values[2], values[3])
    }
}

fun encodeForecastDays(days: List<ForecastDay>): String {
    return days.joinToString("|") {
        listOf(it.day, it.date, it.maxTemp, it.minTemp, it.windSpeed, it.image).joinToString(",")
    }
}
