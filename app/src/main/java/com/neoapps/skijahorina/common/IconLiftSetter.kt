package com.neoapps.skijahorina.common

import android.widget.ImageView
import com.neoapps.skijahorina.R

object IconLiftSetter {

    fun displayImage(type: String, view: ImageView) {
        when {
            type.contains("gondola", ignoreCase = true) -> view.setImageResource(R.drawable.ic_gondola)
            type.contains("chairlift", ignoreCase = true) -> view.setImageResource(R.drawable.ic_cetvorosed)
            type.contains("šestosjed") -> view.setImageResource(R.drawable.ic_sestosed)
            type.contains("6") -> view.setImageResource(R.drawable.ic_sestosed)
            type.contains("Gondola") -> view.setImageResource(R.drawable.ic_gondola)
            type.contains("ski lift") -> view.setImageResource(R.drawable.ic_tbar)
            type.contains("sidro") -> view.setImageResource(R.drawable.ic_tbar)
            type.contains("t-bar", ignoreCase = true) -> view.setImageResource(R.drawable.ic_tbar)
            type.contains("tbar", ignoreCase = true) -> view.setImageResource(R.drawable.ic_tbar)
            type.contains("cetvorosjed") -> view.setImageResource(R.drawable.ic_cetvorosed)
            type.contains("4") -> view.setImageResource(R.drawable.ic_cetvorosed)
            type.contains("dvosjed") -> view.setImageResource(R.drawable.ic_dvosed)
            type.contains("2") -> view.setImageResource(R.drawable.ic_dvosed)
            else -> view.setImageResource(R.drawable.ic_tbar)
        }
    }

}