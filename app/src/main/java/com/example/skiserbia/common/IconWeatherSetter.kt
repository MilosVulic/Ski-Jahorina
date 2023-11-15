package com.example.skiserbia.common

import android.widget.ImageView
import com.example.skiserbia.R

object IconWeatherSetter {

    fun displayImage(image: String, view: ImageView) {
        when (image) {
            "images/weather/d01.png" -> view.setImageResource(R.drawable.ic_sunny)
            "images/weather/d03.png" -> view.setImageResource(R.drawable.ic_partially_cloudy)
            "images/weather/d05.png"-> view.setImageResource(R.drawable.ic_rainy)
            "images/weather/d06.png" -> view.setImageResource(R.drawable.ic_cloudy_snowing)
            "images/weather/d07.png" -> view.setImageResource(R.drawable.ic_cloudy_snowing)
            "images/weather/d09.png"-> view.setImageResource(R.drawable.ic_rainy)
            "images/weather/d11.png"-> view.setImageResource(R.drawable.ic_snowy)

            "images/weather/n01.png" -> view.setImageResource(R.drawable.ic_sunny)
            "images/weather/n03.png" -> view.setImageResource(R.drawable.ic_partially_cloudy)
            "images/weather/n05.png"-> view.setImageResource(R.drawable.ic_rainy)
            "images/weather/n06.png" -> view.setImageResource(R.drawable.ic_cloudy_snowing)
            "images/weather/n07.png" -> view.setImageResource(R.drawable.ic_cloudy_snowing)
            "images/weather/n09.png"-> view.setImageResource(R.drawable.ic_rainy)
            "images/weather/n11.png"-> view.setImageResource(R.drawable.ic_snowy)
            else -> view.setImageResource(R.drawable.ic_partially_cloudy)
        }
    }
}