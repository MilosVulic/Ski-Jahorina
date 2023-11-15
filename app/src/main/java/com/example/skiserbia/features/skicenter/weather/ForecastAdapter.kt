package com.example.skiserbia.features.skicenter.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.skiserbia.R
import com.example.skiserbia.common.IconWeatherSetter

class ForecastAdapter(private val mList: List<ForecastDay>) : RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.forecast_row, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val forecastInfo = mList[position]

        holder.textViewDay.text = forecastInfo.day
        holder.textViewDate.text = forecastInfo.date
        holder.textViewWind.text = forecastInfo.windSpeed.substring(0, forecastInfo.windSpeed.length - 3) + " " + forecastInfo.windSpeed.substring(forecastInfo.windSpeed.length - 3, forecastInfo.windSpeed.length)
        holder.textViewMinTemperature.text = forecastInfo.minTemp
        holder.textViewMaxTemperature.text = forecastInfo.maxTemp

        holder.textViewMinTemperature.setTextColor(ContextCompat.getColor(holder.textViewMinTemperature.context, R.color.minTemperatureColor))
        holder.textViewMaxTemperature.setTextColor(ContextCompat.getColor(holder.textViewMaxTemperature.context, R.color.maxTemperatureColor))
        IconWeatherSetter.displayImage(forecastInfo.image, holder.imageViewWeather)
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
}