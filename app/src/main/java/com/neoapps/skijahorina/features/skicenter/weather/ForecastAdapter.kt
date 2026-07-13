package com.neoapps.skijahorina.features.skicenter.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.neoapps.skijahorina.R
import com.neoapps.skijahorina.common.IconWeatherSetter

class ForecastAdapter : ListAdapter<ForecastDay, ForecastAdapter.ViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.forecast_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val forecastInfo = getItem(position)

        holder.textViewDay.text = forecastInfo.day
        holder.textViewDate.text = forecastInfo.date

        val wind = forecastInfo.windSpeed
        holder.textViewWind.text = if (wind.endsWith("m/s")) {
            wind.substring(0, wind.length - 3) + " m/s"
        } else {
            wind
        }

        holder.textViewMinTemperature.text = forecastInfo.minTemp
        holder.textViewMaxTemperature.text = forecastInfo.maxTemp

        holder.textViewMinTemperature.setTextColor(
            ContextCompat.getColor(holder.textViewMinTemperature.context, R.color.minTemperatureColor)
        )
        holder.textViewMaxTemperature.setTextColor(
            ContextCompat.getColor(holder.textViewMaxTemperature.context, R.color.maxTemperatureColor)
        )
        IconWeatherSetter.displayImage(forecastInfo.image, holder.imageViewWeather)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDay: TextView = itemView.findViewById(R.id.textViewDay)
        val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        val textViewWind: TextView = itemView.findViewById(R.id.textViewWind)
        val textViewMinTemperature: TextView = itemView.findViewById(R.id.textViewMinTemperature)
        val textViewMaxTemperature: TextView = itemView.findViewById(R.id.textViewMaxTemperature)
        val imageViewWeather: ImageView = itemView.findViewById(R.id.imageViewWeather)
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ForecastDay>() {
            override fun areItemsTheSame(oldItem: ForecastDay, newItem: ForecastDay): Boolean =
                oldItem.day == newItem.day && oldItem.date == newItem.date

            override fun areContentsTheSame(oldItem: ForecastDay, newItem: ForecastDay): Boolean =
                oldItem == newItem
        }
    }
}
