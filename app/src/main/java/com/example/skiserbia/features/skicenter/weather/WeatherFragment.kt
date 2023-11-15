package com.example.skiserbia.features.skicenter.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skiserbia.common.IconWeatherSetter
import com.example.skiserbia.databinding.FragmentWeatherInfoBinding

class WeatherFragment : Fragment() {

    private var bindingProp: FragmentWeatherInfoBinding? = null
    private val binding get() = bindingProp!!

    private val temperature: WeatherFragmentArgs by navArgs()
    private val wind: WeatherFragmentArgs by navArgs()
    private val snow: WeatherFragmentArgs by navArgs()
    private val currentWeatherIcon: WeatherFragmentArgs by navArgs()
    private val forecast: WeatherFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentWeatherInfoBinding.inflate(inflater, container, false)
        binding.forecastRecyclerView.layoutManager = LinearLayoutManager(context)

        val forecastList = forecast.forecast.split('|').map {
            val values = it.split(',')
            ForecastDay(values[0], values[1], values[2], values[3], values[4], values[5])
        }

        val listAdapter = ForecastAdapter(forecastList)
        binding.forecastRecyclerView.adapter = listAdapter

        binding.snowValue.text = snow.snow
        binding.windValue.text = wind.wind
        binding.temperatureValue.text = temperature.temperature
        IconWeatherSetter.displayImage(currentWeatherIcon.currentWeatherIcon, binding.temperatureIcon)
        return binding.root
    }
}