package com.example.skiserbia.features.skicenter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.skiserbia.NavigationGraphDirections
import com.example.skiserbia.common.WebScarpingServiceImpl
import com.example.skiserbia.databinding.FragmentSkiCenterDetailsBinding
import com.example.skiserbia.features.skicenter.weather.ForecastDay
import com.example.skiserbia.features.skicenter.weather.WeatherInfo
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SkiCenterDetailsFragment : Fragment() {

    private var bindingProp: FragmentSkiCenterDetailsBinding? = null
    private val binding get() = bindingProp!!
    private val skiCenterUrl: SkiCenterDetailsFragmentArgs by navArgs()
    lateinit var temperature : String
    lateinit var wind : String
    lateinit var snow : String
    lateinit var currentWeatherImage : String
    lateinit var forecast : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentSkiCenterDetailsBinding.inflate(inflater, container, false)

        binding.cardViewLiftInfo.setOnClickListener {
            findNavController().navigate(NavigationGraphDirections.actionLiftInfo(skiCenterUrl.skiCenter))
        }

        binding.cardViewSlopesInfo.setOnClickListener {
            findNavController().navigate(NavigationGraphDirections.actionSlopeInfo(skiCenterUrl.skiCenter))
        }

        binding.cardViewMap.setOnClickListener {
            findNavController().navigate(NavigationGraphDirections.actionSkiMap(skiCenterUrl.skiCenter))
        }

        binding.button2.setOnClickListener {
            findNavController().navigate(NavigationGraphDirections.actionWeatherInfo(temperature, wind, snow, currentWeatherImage, forecast))
        }


        val call = getCall(skiCenterUrl.skiCenter)
        call.enqueue(object : Callback<ResponseBody> {
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
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })

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

    private fun getCall(skiCenterUrl: String): Call<ResponseBody> {
        return if (skiCenterUrl.contains("kopaonik")) {
            WebScarpingServiceImpl.getService().scrapeKopaonikWeatherWebPage()
        } else if (skiCenterUrl.contains("tornik")) {
            WebScarpingServiceImpl.getService().scrapeTornikWeatherWebPage()
        } else {
            WebScarpingServiceImpl.getService().scrapeStaraPlaninaWeatherWebPage()
        }
    }
}