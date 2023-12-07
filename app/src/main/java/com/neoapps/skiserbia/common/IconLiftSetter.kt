package com.neoapps.skiserbia.common

import android.widget.ImageView
import com.neoapps.skiserbia.R

object IconLiftSetter {

    fun displayImage(type: String, view: ImageView) {
        when {
            type.contains("Ski lift") -> view.setImageResource(R.drawable.ic_tbar)
            type.contains("Ski lift - T bar") -> view.setImageResource(R.drawable.ic_tbar)
            type.contains("6-seat chair lift") -> view.setImageResource(R.drawable.ic_sestosed)
            type.contains("4-seat chair lift") -> view.setImageResource(R.drawable.ic_cetvorosed)
            type.contains("2-seat chair lift") -> view.setImageResource(R.drawable.ic_dvosed)
            type.contains("Šestosed") -> view.setImageResource(R.drawable.ic_sestosed)
            type.contains("Četvorosed") -> view.setImageResource(R.drawable.ic_cetvorosed)
            type.contains("Dvosed") -> view.setImageResource(R.drawable.ic_dvosed)
            type.contains("Gondola") -> view.setImageResource(R.drawable.ic_gondola)
            type.contains("Zipline") -> view.setImageResource(R.drawable.ic_zipline)
            type.contains("Bob na šinama") -> view.setImageResource(R.drawable.ic_bobsled)
            else -> {
                view.setImageResource(R.drawable.ic_tbar)
            }
        }
    }

}