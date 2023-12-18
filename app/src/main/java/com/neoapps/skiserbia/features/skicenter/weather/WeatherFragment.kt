package com.neoapps.skiserbia.features.skicenter.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.neoapps.skiserbia.R
import com.neoapps.skiserbia.common.IconWeatherSetter
import com.neoapps.skiserbia.databinding.FragmentWeatherInfoBinding
import com.neoapps.skiserbia.databinding.IncludeEmptyListPlaceholderBinding
import com.neoapps.skiserbia.main.MainActivity


class WeatherFragment : Fragment() {

    private var bindingProp: FragmentWeatherInfoBinding? = null
    private val binding get() = bindingProp!!
    private var bindingPropEmptyState: IncludeEmptyListPlaceholderBinding? = null
    private val bindingEmptyState get() = bindingPropEmptyState!!

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
        bindingPropEmptyState = bindingProp!!.includeEmptylistPlaceholder
        setUpFragmentName()
        binding.forecastRecyclerView.layoutManager = LinearLayoutManager(context)

        if (forecast.forecast.isNotEmpty()) {
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
        }

        if (forecast.forecast.isEmpty()) {
            bindingEmptyState.emptyListState.visibility = View.VISIBLE
            binding.forecastRecyclerView.visibility = View.GONE
            binding.cardViewTemperature.visibility = View.GONE
            binding.cardViewWind.visibility = View.GONE
            binding.cardViewSnow.visibility = View.GONE
        } else {
            bindingEmptyState.emptyListState.visibility = View.GONE
            binding.forecastRecyclerView.visibility = View.VISIBLE
            binding.cardViewTemperature.visibility = View.VISIBLE
            binding.cardViewWind.visibility = View.VISIBLE
            binding.cardViewSnow.visibility = View.VISIBLE
        }

        return binding.root
    }

    private fun setUpFragmentName() {
        (activity as MainActivity).supportActionBar?.title = ""
        val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)

        if (title1TextView != null) {
            title1TextView.visibility = View.VISIBLE
            title1TextView.text = resources.getText(R.string.weather_lowercase)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingProp = null
        bindingPropEmptyState = null
    }
}