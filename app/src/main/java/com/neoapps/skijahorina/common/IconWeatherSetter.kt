package com.neoapps.skijahorina.common

import android.widget.ImageView
import com.neoapps.skijahorina.R

object IconWeatherSetter {

    fun displayImage(imageCode: String, image: String, view: ImageView) {

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
}