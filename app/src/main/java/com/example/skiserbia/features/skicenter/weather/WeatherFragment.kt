package com.example.skiserbia.features.skicenter.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.skiserbia.databinding.FragmentWeatherInfoBinding

class WeatherFragment : Fragment() {

    private var bindingProp: FragmentWeatherInfoBinding? = null
    private val binding get() = bindingProp!!

    private val temperature: WeatherFragmentArgs by navArgs()
    private val wind: WeatherFragmentArgs by navArgs()
    private val snow: WeatherFragmentArgs by navArgs()
    private val forecast: WeatherFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentWeatherInfoBinding.inflate(inflater, container, false)
        binding.weather.text = forecast.toString()
        binding.snowValue.text = snow.snow
        binding.windValue.text = wind.wind
        binding.temperatureValue.text = temperature.temperature
        return binding.root
    }
}