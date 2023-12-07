package com.neoapps.skiserbia.features.skicenter

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.neoapps.skiserbia.common.PreferenceProvider
import com.neoapps.skiserbia.common.Utils
import com.neoapps.skiserbia.common.WebScarpingServiceImpl
import com.neoapps.skiserbia.databinding.FragmentSkiCenterDetailsBinding
import com.neoapps.skiserbia.features.skicenter.lifts.LiftInfo
import com.neoapps.skiserbia.features.skicenter.slopes.SlopeCategoryMapper
import com.neoapps.skiserbia.features.skicenter.slopes.SlopeInfo
import com.neoapps.skiserbia.features.skicenter.weather.ForecastDay
import com.neoapps.skiserbia.features.skicenter.weather.WeatherInfo
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime

class SkiCenterDetailsFragment : Fragment() {

    private var bindingProp: FragmentSkiCenterDetailsBinding? = null
    private val binding get() = bindingProp!!
    private val skiCenterUrl: SkiCenterDetailsFragmentArgs by navArgs()
    lateinit var temperature: String
    lateinit var wind: String
    lateinit var snow: String
    lateinit var currentWeatherImage: String
    lateinit var forecast: String
    lateinit var slopes: String
    lateinit var lifts: String
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentSkiCenterDetailsBinding.inflate(inflater, container, false)

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(requireContext(),"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        })

        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                findNavController().navigate(
                    com.neoapps.skiserbia.NavigationGraphDirections.actionWeatherInfo(
                        temperature,
                        wind,
                        snow,
                        currentWeatherImage,
                        forecast
                    )
                )
            }
        }

        binding.cardViewLiftInfo.setOnClickListener {
            findNavController().navigate(com.neoapps.skiserbia.NavigationGraphDirections.actionLiftInfo(lifts))
        }

        binding.cardViewSlopesInfo.setOnClickListener {
            findNavController().navigate(com.neoapps.skiserbia.NavigationGraphDirections.actionSlopeInfo(slopes))
        }

        binding.cardViewMap.setOnClickListener {
            findNavController().navigate(com.neoapps.skiserbia.NavigationGraphDirections.actionSkiMap(skiCenterUrl.skiCenter))
        }

        binding.cardViewForecastInfo.setOnClickListener {
            PreferenceProvider.weatherClicks += 1
            if (PreferenceProvider.weatherClicks % 3 == 0) {
                if (mInterstitialAd != null) {
                    PreferenceProvider.weatherClicks = 0
                    mInterstitialAd?.show(context as Activity)
                } else {
                    findNavController().navigate(
                        com.neoapps.skiserbia.NavigationGraphDirections.actionWeatherInfo(
                            temperature,
                            wind,
                            snow,
                            currentWeatherImage,
                            forecast
                        )
                    )
                }
            } else {
                findNavController().navigate(
                    com.neoapps.skiserbia.NavigationGraphDirections.actionWeatherInfo(
                        temperature,
                        wind,
                        snow,
                        currentWeatherImage,
                        forecast
                    )
                )
            }
        }

        binding.cardViewUsefulInformation.setOnClickListener {
            findNavController().navigate(com.neoapps.skiserbia.NavigationGraphDirections.actionUsefulInformation(skiCenterUrl.skiCenter))
        }

        binding.cardViewSomethingInfo.setOnClickListener {
            findNavController().navigate(com.neoapps.skiserbia.NavigationGraphDirections.actionCamera(getCameraUrl(skiCenterUrl.skiCenter)))
        }


        if (Utils.isTimeDifferenceGreaterThanProvidedMinutes(
                LocalDateTime.parse(getLastTimeFetchedForTheForecastFromThePreferences(skiCenterUrl.skiCenter)),
                LocalDateTime.now(),
                5
            )
        ) {
            val callForecast = getCall(skiCenterUrl.skiCenter)
            callForecast.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            val htmlContent = responseBody.string()
                            val weatherDetailsList = parseHtmlToWeatherInfo(htmlContent)
                            temperature = weatherDetailsList.temperature
                            wind = weatherDetailsList.windSpeed
                            snow = weatherDetailsList.snowHeight
                            currentWeatherImage = weatherDetailsList.image
                            forecast = weatherDetailsList.forecastDays.joinToString("|") { "${it.day},${it.date},${it.maxTemp},${it.minTemp},${it.windSpeed},${it.image}" }
                            setInfoForTheForecast(skiCenterUrl.skiCenter, forecast, temperature, wind, snow, currentWeatherImage)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        } else {
            temperature = getTemperatureFromThePreferences(skiCenterUrl.skiCenter)
            wind = getWindFromThePreferences(skiCenterUrl.skiCenter)
            snow = getSnowFromThePreferences(skiCenterUrl.skiCenter)
            currentWeatherImage = getImageFromThePreferences(skiCenterUrl.skiCenter)
            forecast = getForecastFromThePreferences(skiCenterUrl.skiCenter)
        }


        if (Utils.isTimeDifferenceGreaterThanProvidedMinutes(
                LocalDateTime.parse(getLastTimeFetchedForTheSlopesFromThePreferences(skiCenterUrl.skiCenter)),
                LocalDateTime.now(),
                5
            )
        ) {
            val callSkiSlopes = WebScarpingServiceImpl.getService(skiCenterUrl.skiCenter).scrapeWebPage(skiCenterUrl.skiCenter)
            callSkiSlopes.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            val htmlContent = responseBody.string()

                            val slopeLiftDetailsList = parseHtmlToSkiSlopeDetails(htmlContent)
                            slopes =
                                slopeLiftDetailsList.joinToString("|") { "${it.name},${it.mark},${it.inFunction},${it.category},${it.lastChange}" }
                            setInfoForTheSlopes(skiCenterUrl.skiCenter, slopes)

                            val skiLiftDetailsList = parseHtmlToSkiLiftDetails(htmlContent)
                            lifts = skiLiftDetailsList.joinToString("|") { "${it.name},${it.type},${it.inFunction},${it.lastChange}" }
                            setInfoForTheLifts(skiCenterUrl.skiCenter, lifts)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        } else {
            slopes = getSlopesFromThePreferences(skiCenterUrl.skiCenter)
            lifts = getLiftsFromThePreferences(skiCenterUrl.skiCenter)
        }
        return binding.root
    }


    private fun parseHtmlToWeatherInfo(html: String): WeatherInfo {
        val document: Document = Jsoup.parse(html)
        Log.d("Nesto", html)

        val location = document.select(".current-weather .forecast-day").text()
        val temperature = document.select(".current-weather .current-temp").text()
        val humidityTempElements = document.select(".current-weather .humidity-temp")
        val currentWeatherImageSrc = document.select(".current-temp img").attr("src")

        var snowHeight = ""
        var windSpeed = ""

        for (i in humidityTempElements.indices) {
            val element = humidityTempElements[i]
            println("Humidity Temp Element: ${element.text()}")

            val imgSrc = element.select("img").attr("src")
            when {
                imgSrc.contains("snow.png") -> snowHeight = humidityTempElements.getOrNull(i)?.text() ?: ""
                imgSrc.contains("wind.png") -> windSpeed = humidityTempElements.getOrNull(i)?.text() ?: ""
            }
        }
        val forecastDays = mutableListOf<ForecastDay>()

        val forecastElements = document.select(".current-forecast")
        for (forecastElement in forecastElements) {
            val day = forecastElement.select(".forecast-left-side .forecast-day").text()
            val date = forecastElement.select(".forecast-left-side .forecast-date").text()
            val maxTemp = forecastElement.select(".forecast-right-side .forecast-temp-red").text()
            val minTemp = forecastElement.select(".forecast-right-side .forecast-temp-blue").text()
            val forecastWindSpeed = forecastElement.select(".forecast-right-side .forecast-cond").text()
            val imageSrc = forecastElement.select(".forecast-left-side img").attr("src")

            val forecastDay = ForecastDay(day, date, maxTemp, minTemp, forecastWindSpeed, imageSrc)
            forecastDays.add(forecastDay)
        }

        return WeatherInfo(location, temperature, snowHeight, windSpeed, currentWeatherImageSrc, forecastDays)
    }

    private fun parseHtmlToSkiSlopeDetails(html: String): List<SlopeInfo> {
        val document: Document = Jsoup.parse(html)
        val detailsList = ArrayList<SlopeInfo>()

        val rows = document.select("table.views-table tbody tr")

        for (row in rows) {
            val columns = row.select("td")

            if (columns.size == 5) {
                val name = columns[0].select("strong").text()
                val mark = columns[1].select("span").text()
                val category = columns[2].select("span").text()
                val open = columns[3].text()
                val lastUpdate = columns[4].text()

                val slopeLiftDetails = SlopeInfo(name, mark, open, SlopeCategoryMapper.mapToSlopeCategory(category), lastUpdate)
                detailsList.add(slopeLiftDetails)
            }
        }

        return detailsList
            .filter { it.mark.isNotEmpty() && !it.mark.contains(",") && !it.mark.contains(".") }
            .distinctBy { it.mark }
    }

    private fun parseHtmlToSkiLiftDetails(html: String): List<LiftInfo> {
        val document: Document = Jsoup.parse(html)
        val detailsList = ArrayList<LiftInfo>()

        val table: Element? = document.select("table.views-table").first()

        if (table != null) {
            val rows: List<Element> = table.select("tr")

            for (row in rows) {
                val columns: List<Element> = row.select("td")

                if (columns.size == 6) {
                    val name = columns[0].text()
                    val type = columns[1].text()
                    val inFunction = columns[4].text()
                    val lastChange = columns[5].text()

                    val skiLiftDetails = LiftInfo(name, type, inFunction, lastChange)
                    detailsList.add(skiLiftDetails)
                }
            }
        }

        return detailsList
            .filter { it.type.isNotEmpty() }
    }

    private fun getCall(skiCenterUrl: String): Call<ResponseBody> {
        return if (skiCenterUrl.contains("kopaonik")) {
            WebScarpingServiceImpl.getService().scrapeKopaonikWeatherWebPage()
        } else if (skiCenterUrl.contains("tornik")) {
            WebScarpingServiceImpl.getService().scrapeTornikWeatherWebPage()
        } else {
            WebScarpingServiceImpl.getService().scrapeStaraPlaninaWeatherWebPage()
        }
    }

    private fun setInfoForTheSlopes(skiCenterUrl: String, slopes: String) {
        return if (skiCenterUrl.contains("kopaonik")) {
            PreferenceProvider.slopesKopaonik = slopes
            PreferenceProvider.lastSlopesInfoKopaonikFetchTime = LocalDateTime.now().toString()
        } else if (skiCenterUrl.contains("tornik")) {
            PreferenceProvider.slopesTornik = slopes
            PreferenceProvider.lastSlopesInfoTornikFetchTime = LocalDateTime.now().toString()
        } else {
            PreferenceProvider.slopesStaraPlanina = slopes
            PreferenceProvider.lastSlopesInfoStaraPlaninaFetchTime = LocalDateTime.now().toString()
        }
    }

    private fun setInfoForTheLifts(skiCenterUrl: String, lifts: String) {
        return if (skiCenterUrl.contains("kopaonik")) {
            PreferenceProvider.liftsKopaonik = lifts
        } else if (skiCenterUrl.contains("tornik")) {
            PreferenceProvider.liftsTornik = lifts
        } else {
            PreferenceProvider.liftsStaraPlanina = lifts
        }
    }

    private fun setInfoForTheForecast(skiCenterUrl: String, forecast: String, temperature: String, wind: String, snow: String, image: String) {
        return if (skiCenterUrl.contains("kopaonik")) {
            PreferenceProvider.forecastKopaonik = forecast
            PreferenceProvider.lastForecastKopaonikFetchTime = LocalDateTime.now().toString()
            PreferenceProvider.temperatureKopaonik = temperature
            PreferenceProvider.windKopaonik = wind
            PreferenceProvider.snowKopaonik = snow
            PreferenceProvider.imageKopaonik = image
        } else if (skiCenterUrl.contains("tornik")) {
            PreferenceProvider.forecastTornik = forecast
            PreferenceProvider.lastForecastTornikFetchTime = LocalDateTime.now().toString()
            PreferenceProvider.temperatureTornik = temperature
            PreferenceProvider.windTornik= wind
            PreferenceProvider.snowTornik = snow
            PreferenceProvider.imageTornik = image
        } else {
            PreferenceProvider.forecastStaraPlanina = forecast
            PreferenceProvider.lastForecastStaraPlaninaFetchTime = LocalDateTime.now().toString()
            PreferenceProvider.temperatureStaraPlanina = temperature
            PreferenceProvider.windStaraPlanina = wind
            PreferenceProvider.snowStaraPlanina = snow
            PreferenceProvider.imageStaraPlanina = image
        }
    }

    private fun getSlopesFromThePreferences(skiCenterUrl: String): String {
        return if (skiCenterUrl.contains("kopaonik")) {
            PreferenceProvider.slopesKopaonik
        } else if (skiCenterUrl.contains("tornik")) {
            PreferenceProvider.slopesTornik
        } else {
            PreferenceProvider.slopesStaraPlanina
        }
    }

    private fun getLiftsFromThePreferences(skiCenterUrl: String): String {
        return if (skiCenterUrl.contains("kopaonik")) {
            PreferenceProvider.liftsKopaonik
        } else if (skiCenterUrl.contains("tornik")) {
            PreferenceProvider.liftsTornik
        } else {
            PreferenceProvider.liftsStaraPlanina
        }
    }

    private fun getForecastFromThePreferences(skiCenterUrl: String): String {
        return if (skiCenterUrl.contains("kopaonik")) {
            PreferenceProvider.forecastKopaonik
        } else if (skiCenterUrl.contains("tornik")) {
            PreferenceProvider.forecastTornik
        } else {
            PreferenceProvider.forecastStaraPlanina
        }
    }

    private fun getTemperatureFromThePreferences(skiCenterUrl: String): String {
        return if (skiCenterUrl.contains("kopaonik")) {
            PreferenceProvider.temperatureKopaonik
        } else if (skiCenterUrl.contains("tornik")) {
            PreferenceProvider.temperatureTornik
        } else {
            PreferenceProvider.temperatureStaraPlanina
        }
    }


    private fun getWindFromThePreferences(skiCenterUrl: String): String {
        return if (skiCenterUrl.contains("kopaonik")) {
            PreferenceProvider.windKopaonik
        } else if (skiCenterUrl.contains("tornik")) {
            PreferenceProvider.windTornik
        } else {
            PreferenceProvider.windStaraPlanina
        }
    }

    private fun getSnowFromThePreferences(skiCenterUrl: String): String {
        return if (skiCenterUrl.contains("kopaonik")) {
            PreferenceProvider.snowKopaonik
        } else if (skiCenterUrl.contains("tornik")) {
            PreferenceProvider.snowTornik
        } else {
            PreferenceProvider.snowStaraPlanina
        }
    }

    private fun getImageFromThePreferences(skiCenterUrl: String): String {
        return if (skiCenterUrl.contains("kopaonik")) {
            PreferenceProvider.imageKopaonik
        } else if (skiCenterUrl.contains("tornik")) {
            PreferenceProvider.imageTornik
        } else {
            PreferenceProvider.imageStaraPlanina
        }
    }


    private fun getLastTimeFetchedForTheSlopesFromThePreferences(skiCenterUrl: String): String {
        return if (skiCenterUrl.contains("kopaonik")) {
            PreferenceProvider.lastSlopesInfoKopaonikFetchTime
        } else if (skiCenterUrl.contains("tornik")) {
            PreferenceProvider.lastSlopesInfoTornikFetchTime
        } else {
            PreferenceProvider.lastSlopesInfoStaraPlaninaFetchTime
        }
    }

    private fun getLastTimeFetchedForTheForecastFromThePreferences(skiCenterUrl: String): String {
        return if (skiCenterUrl.contains("kopaonik")) {
            PreferenceProvider.lastForecastKopaonikFetchTime
        } else if (skiCenterUrl.contains("tornik")) {
            PreferenceProvider.lastForecastTornikFetchTime
        } else {
            PreferenceProvider.lastForecastStaraPlaninaFetchTime
        }
    }

    private fun getCameraUrl(skiCenterUrl: String): String {
        return if (skiCenterUrl.contains("kopaonik")) {
            PreferenceProvider.kopaonikCameraUrl
        } else if (skiCenterUrl.contains("tornik")) {
            PreferenceProvider.zlatiborCameraUrl
        } else {
            PreferenceProvider.staraPlaninaCameraUrl
        }
    }
}