package com.neoapps.skijahorina.common

import android.widget.ImageView
import com.neoapps.skijahorina.R

object IconWeatherSetter {

    fun displayImage(imagePath: String, view: ImageView) {
        if (imagePath.startsWith("owm:")) {
            val parts = imagePath.removePrefix("owm:").split(":")
            if (parts.size == 2) {
                displayOpenWeatherImage(parts[0], parts[1], view)
            }
            return
        }
        if (imagePath.startsWith("images/weather/")) {
            displayBackendImage(imagePath, view)
            return
        }
        displayOpenWeatherImage("", imagePath, view)
    }

    fun displayOpenWeatherImage(imageCode: String, image: String, view: ImageView) {
        when (image) {
            "01d" -> view.setImageResource(R.drawable.ic_sunny)
            "01n" -> view.setImageResource(R.drawable.ic_clear_night)
        }

        when (imageCode) {
            "801" -> view.setImageResource(R.drawable.ic_partially_cloudy)

            "500", "501", "502", "503",
            "504", "511", "520", "521",
            "522", "531" -> view.setImageResource(R.drawable.ic_rainy)

            "600", "611", "612", "613" -> view.setImageResource(R.drawable.ic_cloudy_snowing)

            "601", "602", "615", "616",
            "620", "621", "622" -> view.setImageResource(R.drawable.ic_snowy)

            "802", "803", "804" -> view.setImageResource(R.drawable.ic_cloudy)

            "701", "711", "721", "731", "741" -> view.setImageResource(R.drawable.ic_foggy)
            else -> view.setImageResource(R.drawable.ic_partially_cloudy)
        }
    }

    private fun displayBackendImage(image: String, view: ImageView) {
        when (image) {
            "images/weather/d01.png" -> view.setImageResource(R.drawable.ic_sunny)
            "images/weather/d03.png" -> view.setImageResource(R.drawable.ic_partially_cloudy)
            "images/weather/d04.png" -> view.setImageResource(R.drawable.ic_partially_cloudy)
            "images/weather/d05.png" -> view.setImageResource(R.drawable.ic_rainy)
            "images/weather/d06.png" -> view.setImageResource(R.drawable.ic_cloudy_snowing)
            "images/weather/d07.png" -> view.setImageResource(R.drawable.ic_cloudy_snowing)
            "images/weather/d08.png" -> view.setImageResource(R.drawable.ic_cloudy)
            "images/weather/d09.png" -> view.setImageResource(R.drawable.ic_rainy)
            "images/weather/d10.png" -> view.setImageResource(R.drawable.ic_rainy)
            "images/weather/d11.png" -> view.setImageResource(R.drawable.ic_snowy)
            "images/weather/d12.png" -> view.setImageResource(R.drawable.ic_snowy)
            "images/weather/d15.png" -> view.setImageResource(R.drawable.ic_foggy)
            "images/weather/n01.png" -> view.setImageResource(R.drawable.ic_clear_night)
            "images/weather/n03.png" -> view.setImageResource(R.drawable.ic_partially_cloudy)
            "images/weather/n04.png" -> view.setImageResource(R.drawable.ic_partially_cloudy)
            "images/weather/n05.png" -> view.setImageResource(R.drawable.ic_rainy)
            "images/weather/n06.png" -> view.setImageResource(R.drawable.ic_cloudy_snowing)
            "images/weather/n07.png" -> view.setImageResource(R.drawable.ic_cloudy_snowing)
            "images/weather/n08.png" -> view.setImageResource(R.drawable.ic_cloudy)
            "images/weather/n09.png" -> view.setImageResource(R.drawable.ic_rainy)
            "images/weather/n10.png" -> view.setImageResource(R.drawable.ic_rainy)
            "images/weather/n11.png" -> view.setImageResource(R.drawable.ic_snowy)
            "images/weather/n12.png" -> view.setImageResource(R.drawable.ic_snowy)
            "images/weather/n15.png" -> view.setImageResource(R.drawable.ic_foggy)
            else -> view.setImageResource(R.drawable.ic_partially_cloudy)
        }
    }
}
