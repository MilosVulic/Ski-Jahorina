package com.neoapps.skijahorina.features.skicenter.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.neoapps.skijahorina.R
import com.neoapps.skijahorina.common.IconWeatherSetter
import com.neoapps.skijahorina.common.PreferenceProvider
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

class ForecastAdapter(private val mList: List<WeatherDataForecast>) : RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.forecast_row, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val forecastInfo = mList[position]

        holder.textViewDay.text = convertUnixToDayOfWeek(forecastInfo.dt)
        holder.textViewDate.text = convertUnixToDate(forecastInfo.dt)

        holder.textViewWind.text = forecastInfo.wind.speed.roundToInt().toString() + " m/s"

        holder.textViewMinTemperature.text = forecastInfo.main.temp_min.roundToInt().toString() + "°"
        holder.textViewMaxTemperature.text = forecastInfo.main.temp_max.roundToInt().toString() + "°"

        holder.textViewMinTemperature.setTextColor(ContextCompat.getColor(holder.textViewMinTemperature.context, R.color.minTemperatureColor))
        holder.textViewMaxTemperature.setTextColor(ContextCompat.getColor(holder.textViewMaxTemperature.context, R.color.maxTemperatureColor))
        IconWeatherSetter.displayImage(forecastInfo.weather[0].id.toString(), forecastInfo.weather[0].icon, holder.imageViewWeather)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textViewDay: TextView = itemView.findViewById(R.id.textViewDay)
        val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        val textViewWind: TextView = itemView.findViewById(R.id.textViewWind)
        val textViewMinTemperature: TextView = itemView.findViewById(R.id.textViewMinTemperature)
        val textViewMaxTemperature: TextView = itemView.findViewById(R.id.textViewMaxTemperature)
        val imageViewWeather: ImageView = itemView.findViewById(R.id.imageViewWeather)
    }

    private fun convertUnixToDate(timestamp: Long): String {
        val instant = Instant.ofEpochSecond(timestamp)
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.").withZone(ZoneId.of("UTC"))
        return formatter.format(instant)
    }

    private fun convertUnixToDayOfWeek(timestamp: Long): String {
        val instant = Instant.ofEpochSecond(timestamp)
        val formatter = DateTimeFormatter
            .ofPattern("EEE")
            .withZone(ZoneId.of("UTC"))
            .withLocale(Locale.forLanguageTag(PreferenceProvider.language))
        return formatter.format(instant)
    }
}