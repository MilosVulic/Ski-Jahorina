package com.neoapps.skijahorina.features.skicenter.weather

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.neoapps.skijahorina.R
import com.neoapps.skijahorina.common.IconWeatherSetter
import com.neoapps.skijahorina.common.RetrofitClient
import com.neoapps.skijahorina.databinding.FragmentWeatherInfoBinding
import com.neoapps.skijahorina.databinding.LayoutUnableToFetchDataBinding
import com.neoapps.skijahorina.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


class WeatherFragment : Fragment() {

    private var bindingProp: FragmentWeatherInfoBinding? = null
    private val binding get() = bindingProp!!
    private var bindingPropEmptyState: LayoutUnableToFetchDataBinding? = null
    private val bindingEmptyState get() = bindingPropEmptyState!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentWeatherInfoBinding.inflate(inflater, container, false)
        bindingPropEmptyState = bindingProp?.includeWeatherUnavailable
        if (bindingProp == null || bindingPropEmptyState == null) {
            return inflater.inflate(R.layout.fragment_weather_info, container, false)
        }
        setUpFragmentName()


        if (!isNetworkAvailable(requireContext())) {
            bindingEmptyState.noInternet.visibility = View.VISIBLE
            binding.cardViewSnow.visibility = View.GONE
            binding.cardViewWind.visibility = View.GONE
            binding.cardViewTemperature.visibility = View.GONE
        } else {
            bindingEmptyState.noInternet.visibility = View.GONE
            binding.cardViewSnow.visibility = View.VISIBLE
            binding.cardViewWind.visibility = View.VISIBLE
            binding.cardViewTemperature.visibility = View.VISIBLE
            binding.forecastRecyclerView.layoutManager = LinearLayoutManager(context)
            val weatherService = RetrofitClient.retrofit.create(WeatherService::class.java)
            val callWeather = weatherService.getWeatherByCoordinates(43.738, 18.563, "metric","b0c38f507b5aaf41845adad0f759e199")

            callWeather.enqueue(object : Callback<WeatherDataForecast> {
                override fun onResponse(call: Call<WeatherDataForecast>, response: Response<WeatherDataForecast>) {
                    if (response.isSuccessful) {
                        bindingEmptyState.noInternet.visibility = View.GONE
                        binding.cardViewSnow.visibility = View.VISIBLE
                        binding.cardViewWind.visibility = View.VISIBLE
                        binding.cardViewTemperature.visibility = View.VISIBLE
                        val weatherData = response.body()
                        binding.snowValue.text = (weatherData?.snow?.`1h`?.toString() ?: "0") + " cm"
                        binding.windValue.text = weatherData?.wind?.speed?.roundToInt().toString() + " m/s"
                        binding.temperatureValue.text = weatherData?.main?.temp?.roundToInt().toString()  + "°"
                        if (weatherData != null) {
                            IconWeatherSetter.displayImage(weatherData.weather[0].id.toString(), weatherData.weather[0].icon, binding.temperatureIcon)
                        }
                        Log.d("WeatherFrag", "Response: $weatherData")
                    } else {
                        bindingEmptyState.noInternet.visibility = View.VISIBLE
                        binding.cardViewSnow.visibility = View.GONE
                        binding.cardViewWind.visibility = View.GONE
                        binding.cardViewTemperature.visibility = View.GONE
                        Log.e("WeatherFrag", "Request failed with status: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<WeatherDataForecast>, t: Throwable) {
                    bindingEmptyState.noInternet.visibility = View.VISIBLE
                    binding.cardViewSnow.visibility = View.GONE
                    binding.cardViewWind.visibility = View.GONE
                    binding.cardViewTemperature.visibility = View.GONE
                    Log.e("WeatherFrag", "Request failed: ${t.message}")
                }
            })


            val callForecast = weatherService.getForecastByCoordinates(43.738, 18.563, "metric","b0c38f507b5aaf41845adad0f759e199")

            callForecast.enqueue(object : Callback<ForecastData> {
                override fun onResponse(call: Call<ForecastData>, response: Response<ForecastData>) {
                    if (response.isSuccessful) {
                        binding.forecastRecyclerView.visibility = View.VISIBLE
                        binding.forecastRecyclerView.layoutManager = LinearLayoutManager(context)

                        val forecastData = response.body()?.list ?: emptyList()

                        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.")
                        val groupedByDate = forecastData.groupBy { data ->
                            Instant.ofEpochSecond(data.dt)
                                .atZone(ZoneId.of("UTC"))
                                .format(dateFormatter)
                        }

                        val filteredForecast = groupedByDate.map { (_, entries) ->
                            val minTemp = entries.minOf { it.main.temp_min }
                            val maxTemp = entries.maxOf { it.main.temp_max }
                            val maxWindSpeed = entries.maxOf { it.wind.speed }

                            val representativeEntry = entries.first().copy(
                                main = entries.first().main.copy(
                                    temp_min = minTemp,
                                    temp_max = maxTemp
                                ),
                                wind = entries.first().wind.copy(
                                    speed = maxWindSpeed
                                )
                            )

                            representativeEntry
                        }

                        if (filteredForecast.size == 6){
                            val listAdapter = ForecastAdapter(filteredForecast.subList(1, 6))
                            binding.forecastRecyclerView.adapter = listAdapter
                        } else {
                            val listAdapter = ForecastAdapter(filteredForecast)
                            binding.forecastRecyclerView.adapter = listAdapter
                        }

                        Log.d("WeatherFrag", "Response: $forecastData")
                    } else {
                        binding.forecastRecyclerView.visibility = View.GONE
                        Log.e("WeatherFrag", "Request failed with status: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ForecastData>, t: Throwable) {
                    binding.forecastRecyclerView.visibility = View.GONE
                    Log.e("WeatherFrag", "Request failed: ${t.message}")
                }
            })
        }

        return binding.root
    }

    private fun setUpFragmentName() {
        (activity as MainActivity).supportActionBar?.title = ""
        val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)
        val toolbar = (activity as MainActivity).findViewById<Toolbar>(R.id.toolbar)

        if (title1TextView != null) {
            title1TextView.visibility = View.VISIBLE
            title1TextView.text = resources.getText(R.string.weather_lowercase)
        }

        if (toolbar != null) {
            toolbar.navigationContentDescription = ""
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network: Network? = connectivityManager.activeNetwork
        val capabilities: NetworkCapabilities? = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingProp = null
        bindingPropEmptyState = null
    }
}