package com.example.skiserbia.features.skicenter.weather

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.skiserbia.common.WebScarpingServiceImpl
import com.example.skiserbia.databinding.FragmentWeatherInfoBinding
import com.example.skiserbia.features.skicenter.lifts.LiftInfoFragmentArgs
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherFragment : Fragment() {

    private var bindingProp: FragmentWeatherInfoBinding? = null
    private val binding get() = bindingProp!!

    private val skiCenterUrl: LiftInfoFragmentArgs by navArgs()
    private val weatherUrl: LiftInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentWeatherInfoBinding.inflate(inflater, container, false)

        val call = getCall(skiCenterUrl.skiCenter)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val htmlContent = responseBody.string()
                        val skiLiftDetailsList = parseHtmlToWeatherInfo(htmlContent)
                        binding.weather.text = skiLiftDetailsList.toString()
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
        val snowHeight = document.select(".current-weather .humidity-temp:contains(Visina snega) + div").text()
        val windSpeed = document.select(".current-weather .humidity-temp:contains(Brzina vetra) + div").text()

        val forecastDays = mutableListOf<ForecastDay>()

        val forecastElements = document.select(".current-forecast")
        for (forecastElement in forecastElements) {
            val day = forecastElement.select(".forecast-left-side .forecast-day").text()
            val date = forecastElement.select(".forecast-left-side .forecast-date").text()
            val maxTemp = forecastElement.select(".forecast-right-side .forecast-temp-red").text()
            val minTemp = forecastElement.select(".forecast-right-side .forecast-temp-blue").text()
            val forecastWindSpeed = forecastElement.select(".forecast-right-side .forecast-cond").text()

            val forecastDay = ForecastDay(day, date, maxTemp, minTemp, forecastWindSpeed)
            forecastDays.add(forecastDay)
        }

        return WeatherInfo(location, temperature, snowHeight, windSpeed, forecastDays)
    }

    private fun getCall(skiCenterUrl: String): Call<ResponseBody> {
        Log.d("Nesto1", skiCenterUrl )
        return if (skiCenterUrl.contains("kopaonik")) {
            WebScarpingServiceImpl.getService().scrapeKopaonikWeatherWebPage()
        } else if (skiCenterUrl.contains("tornik")) {
            WebScarpingServiceImpl.getService().scrapeTornikWeatherWebPage()
        } else {
            WebScarpingServiceImpl.getService().scrapeStaraPlaninaWeatherWebPage()
        }
    }
}