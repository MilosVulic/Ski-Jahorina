package com.neoapps.skijahorina.features.skicenter.weather



import android.os.Bundle

import android.view.LayoutInflater

import android.view.View

import android.view.ViewGroup

import android.widget.TextView

import androidx.appcompat.widget.Toolbar

import androidx.fragment.app.Fragment

import androidx.fragment.app.viewModels

import androidx.recyclerview.widget.LinearLayoutManager

import com.neoapps.skijahorina.R

import com.neoapps.skijahorina.common.CacheTimestampFormatter

import com.neoapps.skijahorina.common.FetchEmptyState

import com.neoapps.skijahorina.common.AppAnalytics

import com.neoapps.skijahorina.common.IconWeatherSetter

import com.neoapps.skijahorina.common.PreferenceProvider

import com.neoapps.skijahorina.common.SnowDepthFormatter

import com.neoapps.skijahorina.databinding.FragmentWeatherInfoBinding

import com.neoapps.skijahorina.databinding.LayoutUnableToFetchDataBinding

import com.neoapps.skijahorina.features.skicenter.JahorinaWeatherData

import com.neoapps.skijahorina.main.MainActivity



class WeatherFragment : Fragment() {



    private var bindingProp: FragmentWeatherInfoBinding? = null

    private val binding get() = bindingProp!!

    private var bindingPropEmptyState: LayoutUnableToFetchDataBinding? = null

    private val bindingEmptyState get() = bindingPropEmptyState!!



    private val viewModel: WeatherViewModel by viewModels()

    private var forecastAdapter: ForecastAdapter? = null



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

        binding.forecastRecyclerView.layoutManager = LinearLayoutManager(context)

        forecastAdapter = ForecastAdapter().also { adapter ->

            binding.forecastRecyclerView.adapter = adapter

        }

        return binding.root

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        AppAnalytics.logFeatureOpened(AppAnalytics.Feature.WEATHER)

        viewModel.weatherData.observe(viewLifecycleOwner) { data ->

            bindWeatherUi(data)

        }

        viewModel.loadWeather()

    }



    private fun bindWeatherUi(data: JahorinaWeatherData) {

        if (bindingProp == null) return



        val hasData = data.hasData

        if (hasData) {

            FetchEmptyState.hide(bindingEmptyState)

            binding.weatherHeroCard.visibility = View.VISIBLE

            binding.forecastCard.visibility = View.VISIBLE



            bindLastUpdated()



            val snowDisplay = SnowDepthFormatter.formatForDisplay(data.snow)

            binding.snowValue.text = snowDisplay

            binding.snowMetricTile.visibility =

                if (snowDisplay == "—") View.GONE else View.VISIBLE



            binding.windValue.text = data.wind.ifBlank { "—" }

            binding.temperatureValue.text = data.temperature.ifBlank { "—" }

            if (data.weatherImage.isNotBlank()) {

                IconWeatherSetter.displayImage(data.weatherImage, binding.temperatureIcon)

            }

            forecastAdapter?.submitList(data.forecast)

        } else {

            binding.includeLastUpdated.root.visibility = View.GONE

            binding.weatherHeroCard.visibility = View.GONE

            binding.forecastCard.visibility = View.GONE

            FetchEmptyState.bind(

                bindingEmptyState,

                FetchEmptyState.resolve(requireContext(), hasCachedData = false),

                onRetry = { viewModel.loadWeather() }

            )

        }

    }



    private fun bindLastUpdated() {

        val formatted = CacheTimestampFormatter.bestTimestamp(

            PreferenceProvider.resortApiUpdatedAt,

            PreferenceProvider.lastWeatherFetchTime

        )

        if (formatted != null) {

            binding.includeLastUpdated.root.visibility = View.VISIBLE

            binding.includeLastUpdated.lastUpdatedText.text =

                getString(R.string.last_updated, formatted)

        } else {

            binding.includeLastUpdated.root.visibility = View.GONE

        }

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

            toolbar.navigationContentDescription = getString(R.string.cd_navigate_back)

        }

    }



    override fun onDestroyView() {

        super.onDestroyView()

        bindingProp?.forecastRecyclerView?.adapter = null

        forecastAdapter = null

        bindingProp = null

        bindingPropEmptyState = null

    }

}


