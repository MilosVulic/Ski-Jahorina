package com.neoapps.skijahorina.features.skicenter.data.legacy

import com.neoapps.skijahorina.BuildConfig
import com.neoapps.skijahorina.common.AppAnalytics
import com.neoapps.skijahorina.common.RetrofitClient
import com.neoapps.skijahorina.features.skicenter.JahorinaWeatherData
import com.neoapps.skijahorina.features.skicenter.applyLiftsToCache
import com.neoapps.skijahorina.features.skicenter.applyWeatherToCache
import com.neoapps.skijahorina.features.skicenter.data.ResortRefreshPolicy
import com.neoapps.skijahorina.features.skicenter.lifts.LiftInfo
import com.neoapps.skijahorina.features.skicenter.weather.ForecastDay
import com.neoapps.skijahorina.features.skicenter.weather.encodeForecastDays
import com.neoapps.skijahorina.features.skicenter.weather.parseCachedLifts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

/**
 * On-device scraping — same path older APKs use, and fallback when Railway API is unavailable.
 */
object LegacyJahorinaDataFetcher {

    private const val PEAKVISOR_URL =
        "https://peakvisor.com/ski/bosnia-and-herzegovina/republika-srpska/jahorina/live"
    private const val LATITUDE = 43.738
    private const val LONGITUDE = 18.563

    suspend fun refreshAll() = coroutineScope {
        val liftsJob = async { refreshLifts() }
        val weatherJob = async { refreshWeather() }
        liftsJob.await()
        weatherJob.await()
    }

    suspend fun refreshLifts(): List<LiftInfo> = withContext(Dispatchers.IO) {
        if (!ResortRefreshPolicy.isLiftsStale()) {
            return@withContext parseCachedLifts()
        }
        try {
            val parsedLifts = parseLiftData()
            applyLiftsToCache(parsedLifts)
            parsedLifts
        } catch (e: Exception) {
            AppAnalytics.recordException(e, "PeakVisor lift scrape failed", mapOf("resort" to "jahorina"))
            AppAnalytics.logDataFetchFailed("lifts_scrape")
            parseCachedLifts()
        }
    }

    suspend fun refreshWeather(): JahorinaWeatherData = withContext(Dispatchers.IO) {
        val cached = JahorinaWeatherData.fromCache()
        if (!ResortRefreshPolicy.isWeatherStale() && cached.hasData) {
            return@withContext cached
        }

        val apiKey = BuildConfig.OPENWEATHER_API_KEY
        if (apiKey.isBlank()) {
            AppAnalytics.logDataFetchFailed("weather_openweather_missing_key")
            return@withContext cached
        }

        try {
            val weatherService = RetrofitClient.weatherApiService
            val currentResponse = weatherService
                .getWeatherByCoordinates(LATITUDE, LONGITUDE, "metric", apiKey)
                .execute()
            val forecastResponse = weatherService
                .getForecastByCoordinates(LATITUDE, LONGITUDE, "metric", apiKey)
                .execute()

            if (!currentResponse.isSuccessful || !forecastResponse.isSuccessful) {
                val code = if (!currentResponse.isSuccessful) currentResponse.code() else forecastResponse.code()
                AppAnalytics.logDataFetchFailed("weather_openweather", code)
                AppAnalytics.recordFailure(
                    "OpenWeather HTTP $code",
                    mapOf("source" to "weather_openweather", "resort" to "jahorina")
                )
                return@withContext cached
            }

            val weatherData = currentResponse.body()
            val forecastBody = forecastResponse.body()
            if (weatherData == null || forecastBody == null) {
                AppAnalytics.logDataFetchFailed("weather_openweather_empty")
                return@withContext cached
            }

            val temperature = weatherData.main.temp.roundToInt().toString() + "°"
            val wind = weatherData.wind.speed.roundToInt().toString() + " m/s"
            val snowCm = weatherData.snow?.`1h`?.roundToInt() ?: 0
            val snow = "$snowCm cm"
            val weatherImage = weatherData.weather.firstOrNull()?.let { weather ->
                "owm:${weather.id}:${weather.icon}"
            }.orEmpty()

            val forecastDays = buildForecastDays(forecastBody.list)
            val forecastPipe = encodeForecastDays(forecastDays)
            val result = JahorinaWeatherData(
                temperature = temperature,
                wind = wind,
                snow = snow,
                weatherImage = weatherImage,
                forecast = forecastDays
            )
            applyWeatherToCache(result, forecastPipe)
            result
        } catch (e: Exception) {
            AppAnalytics.recordException(e, "OpenWeather fetch failed", mapOf("resort" to "jahorina"))
            AppAnalytics.logDataFetchFailed("weather_openweather")
            cached
        }
    }

    private fun parseLiftData(): List<LiftInfo> {
        val doc = Jsoup.connect(PEAKVISOR_URL).get()
        return doc.select("div.lift").map { lift ->
            val statusIconUrl = lift.select(
                "div.lift-details-info:has(div.lift-details-info__header:contains(Status)) div.lift-details-info__content"
            ).text()
            val liftType = lift.select("div.lift-type use").attr("xlink:href").split("#").last()
            val liftName = lift.select("div.lift-name span").text()
            val liftWorkingHours = lift.select("div.lift-opening-hours span").text()
            LiftInfo(liftName, liftType, statusIconUrl, liftWorkingHours)
        }
    }

    private fun buildForecastDays(
        forecastEntries: List<com.neoapps.skijahorina.features.skicenter.weather.WeatherDataForecast>
    ): List<ForecastDay> {
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.")
        val dayFormatter = DateTimeFormatter.ofPattern("EEE")

        val groupedByDate = forecastEntries.groupBy { data ->
            Instant.ofEpochSecond(data.dt)
                .atZone(ZoneId.of("UTC"))
                .format(dateFormatter)
        }

        val days = groupedByDate.map { (dateLabel, entries) ->
            val minTemp = entries.minOf { it.main.temp_min }.roundToInt().toString() + "°"
            val maxTemp = entries.maxOf { it.main.temp_max }.roundToInt().toString() + "°"
            val maxWindSpeed = entries.maxOf { it.wind.speed }.roundToInt().toString() + "m/s"
            val representative = entries.first().weather.first()
            val instant = Instant.ofEpochSecond(entries.first().dt)
            val dayName = dayFormatter.format(instant.atZone(ZoneId.of("UTC")))

            ForecastDay(
                day = dayName,
                date = dateLabel,
                maxTemp = maxTemp,
                minTemp = minTemp,
                windSpeed = maxWindSpeed,
                image = "owm:${representative.id}:${representative.icon}"
            )
        }

        return if (days.size > 5) days.drop(1).take(5) else days
    }
}
