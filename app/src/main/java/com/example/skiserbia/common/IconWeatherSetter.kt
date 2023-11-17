package com.example.skiserbia.common

import android.widget.ImageView
import com.example.skiserbia.R

object IconWeatherSetter {

    fun displayImage(image: String, view: ImageView) {
        when (image) {
            "images/weather/d01.png" -> view.setImageResource(R.drawable.ic_sunny)
            "images/weather/d03.png" -> view.setImageResource(R.drawable.ic_partially_cloudy)
            "images/weather/d04.png" -> view.setImageResource(R.drawable.ic_partially_cloudy)
            "images/weather/d05.png"-> view.setImageResource(R.drawable.ic_rainy)
            "images/weather/d06.png" -> view.setImageResource(R.drawable.ic_cloudy_snowing)
            "images/weather/d07.png" -> view.setImageResource(R.drawable.ic_cloudy_snowing)
            "images/weather/d08.png"-> view.setImageResource(R.drawable.ic_cloudy)
            "images/weather/d09.png"-> view.setImageResource(R.drawable.ic_rainy)
            "images/weather/d10.png"-> view.setImageResource(R.drawable.ic_rainy)
            "images/weather/d11.png"-> view.setImageResource(R.drawable.ic_snowy)
            "images/weather/d12.png"-> view.setImageResource(R.drawable.ic_snowy)  //susnezica
            "images/weather/d15.png"-> view.setImageResource(R.drawable.ic_foggy)

            "images/weather/n01.png" -> view.setImageResource(R.drawable.ic_clear_night)
            "images/weather/n03.png" -> view.setImageResource(R.drawable.ic_partially_cloudy)
            "images/weather/n04.png" -> view.setImageResource(R.drawable.ic_partially_cloudy)
            "images/weather/n05.png"-> view.setImageResource(R.drawable.ic_rainy)
            "images/weather/n06.png" -> view.setImageResource(R.drawable.ic_cloudy_snowing)
            "images/weather/n07.png" -> view.setImageResource(R.drawable.ic_cloudy_snowing)
            "images/weather/n08.png"-> view.setImageResource(R.drawable.ic_cloudy)
            "images/weather/n09.png"-> view.setImageResource(R.drawable.ic_rainy)
            "images/weather/n10.png"-> view.setImageResource(R.drawable.ic_rainy)
            "images/weather/n11.png"-> view.setImageResource(R.drawable.ic_snowy)
            "images/weather/n12.png"-> view.setImageResource(R.drawable.ic_snowy)  //susnezica
            "images/weather/n15.png"-> view.setImageResource(R.drawable.ic_foggy)
            else -> view.setImageResource(R.drawable.ic_partially_cloudy)
        }
    }
}