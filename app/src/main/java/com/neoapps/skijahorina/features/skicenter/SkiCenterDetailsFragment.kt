package com.neoapps.skijahorina.features.skicenter

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.neoapps.skijahorina.R
import com.neoapps.skijahorina.common.PreferenceProvider
import com.neoapps.skijahorina.common.Utils
import com.neoapps.skijahorina.common.WebScarpingServiceImpl
import com.neoapps.skijahorina.databinding.FragmentSkiCenterDetailsBinding
import com.neoapps.skijahorina.features.skicenter.lifts.LiftInfo
import com.neoapps.skijahorina.features.skicenter.slopes.SlopeCategoryMapper
import com.neoapps.skijahorina.features.skicenter.slopes.SlopeInfo
import com.neoapps.skijahorina.features.skicenter.weather.ForecastDay
import com.neoapps.skijahorina.features.skicenter.weather.WeatherInfo
import com.neoapps.skijahorina.main.MainActivity
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
    private var mInterstitialLiftsAd: InterstitialAd? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentSkiCenterDetailsBinding.inflate(inflater, container, false)
        setUpFragmentName()

        // "ca-app-pub-7130760675198405/2157972704"
        // test ca-app-pub-3940256099942544/1033173712
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(requireContext(), "ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        })

        // "ca-app-pub-7130760675198405/4592564355"
        // test ca-app-pub-3940256099942544/1033173712
        val adRequest1 = AdRequest.Builder().build()
        InterstitialAd.load(requireContext(), "ca-app-pub-3940256099942544/1033173712", adRequest1, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialLiftsAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialLiftsAd = interstitialAd
            }
        })

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                findNavController().navigate(
                    com.neoapps.skijahorina.NavigationGraphDirections.actionWeatherInfo(
                        temperature,
                        wind,
                        snow,
                        currentWeatherImage,
                        forecast
                    )
                )
            }
        }

        mInterstitialLiftsAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                mInterstitialLiftsAd = null
                findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionLiftInfo(lifts))
            }
        }

        binding.cardViewLiftInfo.setOnClickListener {
            PreferenceProvider.liftsClicks += 1
            if (PreferenceProvider.liftsClicks % 3 == 0) {
                if (mInterstitialLiftsAd != null) {
                    PreferenceProvider.liftsClicks = 0
                    mInterstitialLiftsAd?.show(context as Activity)
                } else {
                    findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionLiftInfo(getLifts(skiCenterUrl.skiCenter)))
                }
            } else {
                findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionLiftInfo(getLifts(skiCenterUrl.skiCenter)))
            }
        }

        binding.cardViewSlopesInfo.setOnClickListener {
            findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionSlopeInfo(getSlopes(skiCenterUrl.skiCenter)))
        }

        binding.cardViewMap.setOnClickListener {
            findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionSkiMap())
        }

        binding.cardViewForecastInfo.setOnClickListener {
            PreferenceProvider.weatherClicks += 1
            if (PreferenceProvider.weatherClicks % 3 == 0) {
                if (mInterstitialAd != null) {
                    PreferenceProvider.weatherClicks = 0
                    mInterstitialAd?.show(context as Activity)
                } else {
                    findNavController().navigate(
                        com.neoapps.skijahorina.NavigationGraphDirections.actionWeatherInfo(
                            getTemperature(skiCenterUrl.skiCenter),
                            getWind(skiCenterUrl.skiCenter),
                            getSnow(skiCenterUrl.skiCenter),
                            getCurrentImage(skiCenterUrl.skiCenter),
                            getForecast(skiCenterUrl.skiCenter)
                        )
                    )
                }
            } else {
                findNavController().navigate(
                    com.neoapps.skijahorina.NavigationGraphDirections.actionWeatherInfo(
                        getTemperature(skiCenterUrl.skiCenter),
                        getWind(skiCenterUrl.skiCenter),
                        getSnow(skiCenterUrl.skiCenter),
                        getCurrentImage(skiCenterUrl.skiCenter),
                        getForecast(skiCenterUrl.skiCenter)
                    )
                )
            }
        }

        binding.cardViewUsefulInformation.setOnClickListener {
            findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionUsefulInformation(skiCenterUrl.skiCenter))
        }

        binding.cardViewSomethingInfo.setOnClickListener {
            if (skiCenterUrl.skiCenter.contains("kopaonik")) {
                findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionCameraVideo())
            } else {
                findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionCamera(getCameraUrl(skiCenterUrl.skiCenter)))
            }
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
                            forecast =
                                weatherDetailsList.forecastDays.joinToString("|") { "${it.day},${it.date},${it.maxTemp},${it.minTemp},${it.windSpeed},${it.image}" }
                            setInfoForTheForecast(skiCenterUrl.skiCenter, forecast, temperature, wind, snow, currentWeatherImage)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    temperature = ""
                    wind = ""
                    snow = ""
                    currentWeatherImage = ""
                    forecast = ""
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
                            lifts = skiLiftDetailsList.joinToString("|") { "${it.name},${it.type},${it.workingHours},${it.inFunction},${it.lastChange}" }
                            setInfoForTheLifts(skiCenterUrl.skiCenter, lifts)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    lifts = ""
                    slopes = ""
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
                    val workingHours = columns[3].text()
                    val inFunction = columns[4].text()
                    val lastChange = columns[5].text()

                    val skiLiftDetails = LiftInfo(name, type, workingHours, inFunction, lastChange)
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
            PreferenceProvider.windTornik = wind
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

    override fun onDestroyView() {
        super.onDestroyView()
        bindingProp = null
    }

    private fun setUpFragmentName() {
        (activity as MainActivity).supportActionBar?.title = ""
        val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)

        if (title1TextView != null) {
            title1TextView.visibility = View.GONE
        }
    }

    private fun getLifts(skiCenterUrl: String): String {
        return if (::lifts.isInitialized) {
            lifts.takeIf { it.isNotEmpty() } ?: getLiftsFromThePreferences(skiCenterUrl)
        } else {
            getLiftsFromThePreferences(skiCenterUrl)
        }
    }

    private fun getSlopes(skiCenterUrl: String): String {
        return if (::slopes.isInitialized) {
            slopes.takeIf { it.isNotEmpty() } ?: getSlopesFromThePreferences(skiCenterUrl)
        } else {
            getSlopesFromThePreferences(skiCenterUrl)
        }
    }

    private fun getWind(skiCenterUrl: String): String {
        return if (::wind.isInitialized) {
            wind.takeIf { it.isNotEmpty() } ?: getWindFromThePreferences(skiCenterUrl)
        } else {
            getWindFromThePreferences(skiCenterUrl)
        }
    }

    private fun getTemperature(skiCenterUrl: String): String {
        return if (::temperature.isInitialized) {
            temperature.takeIf { it.isNotEmpty() } ?: getTemperatureFromThePreferences(skiCenterUrl)
        } else {
            getTemperatureFromThePreferences(skiCenterUrl)
        }
    }

    private fun getSnow(skiCenterUrl: String): String {
        return if (::snow.isInitialized) {
            snow.takeIf { it.isNotEmpty() } ?: getSnowFromThePreferences(skiCenterUrl)
        } else {
            getSnowFromThePreferences(skiCenterUrl)
        }
    }

    private fun getCurrentImage(skiCenterUrl: String): String {
        return if (::currentWeatherImage.isInitialized) {
            currentWeatherImage.takeIf { it.isNotEmpty() } ?: getImageFromThePreferences(skiCenterUrl)
        } else {
            getImageFromThePreferences(skiCenterUrl)
        }
    }

    private fun getForecast(skiCenterUrl: String): String {
        return if (::forecast.isInitialized) {
            forecast.takeIf { it.isNotEmpty() } ?: getForecastFromThePreferences(skiCenterUrl)
        } else {
            getForecastFromThePreferences(skiCenterUrl)
        }
    }
}