package com.neoapps.skijahorina.features.skicenter.data

import com.neoapps.skijahorina.common.PreferenceProvider
import com.neoapps.skijahorina.common.Utils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

object ResortRefreshPolicy {

    private val zoneId: ZoneId = ZoneId.of("Europe/Belgrade")

    private const val IN_SEASON_WEATHER_MINUTES = 5
    private const val IN_SEASON_LIFTS_MINUTES = 10
    private const val OFF_SEASON_MINUTES = 60

    fun weatherCacheMinutes(): Int =
        if (isInSeason()) IN_SEASON_WEATHER_MINUTES else OFF_SEASON_MINUTES

    fun liftsCacheMinutes(): Int =
        if (isInSeason()) IN_SEASON_LIFTS_MINUTES else OFF_SEASON_MINUTES

    fun isWeatherStale(): Boolean =
        isOlderThanMinutes(PreferenceProvider.lastWeatherFetchTime, weatherCacheMinutes())

    fun isLiftsStale(): Boolean =
        isOlderThanMinutes(PreferenceProvider.lastLiftInfoJahorinaFetchTime, liftsCacheMinutes())

    fun isAnyStale(): Boolean = isWeatherStale() || isLiftsStale()

    fun isInSeason(): Boolean {
        val month = LocalDate.now(zoneId).monthValue
        return month == 12 || month == 1 || month == 2 || month == 3
    }

    private fun isOlderThanMinutes(lastFetchIso: String, minutes: Int): Boolean {
        return Utils.isTimeDifferenceGreaterThanProvidedMinutes(
            LocalDateTime.parse(lastFetchIso),
            LocalDateTime.now(),
            minutes
        )
    }
}
