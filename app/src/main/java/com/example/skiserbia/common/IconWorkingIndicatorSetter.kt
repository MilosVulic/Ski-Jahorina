package com.example.skiserbia.common

import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.skiserbia.R

object IconWorkingIndicatorSetter {

    fun displayImage(inFunction: String, view: ImageView) {
        when (inFunction) {
            "Ne" -> view.setImageResource(R.drawable.ic_cancel)
            "NE" -> view.setImageResource(R.drawable.ic_cancel)
            "No" -> view.setImageResource(R.drawable.ic_cancel)
            "NO" -> view.setImageResource(R.drawable.ic_cancel)
            "Closed" -> view.setImageResource(R.drawable.ic_cancel)
            "CLOSED" -> view.setImageResource(R.drawable.ic_cancel)
            "DA" -> view.setImageResource(R.drawable.ic_check)
            "Da" -> view.setImageResource(R.drawable.ic_check)
            "Yes" -> view.setImageResource(R.drawable.ic_check)
            "YES" -> view.setImageResource(R.drawable.ic_check)
            "OPENED" -> view.setImageResource(R.drawable.ic_check)
            "Opened" -> view.setImageResource(R.drawable.ic_check)
        }
    }

    fun setBackground(inFunction: String, view: ImageView) {
        when (inFunction) {
            "Ne", "NE", "No", "NO", "Closed", "CLOSED" -> {
                view.setColorFilter(ContextCompat.getColor(view.context, R.color.cancelColor))
            }
            "DA", "Da", "Yes", "YES", "OPENED", "Opened" -> {
                view.setColorFilter(ContextCompat.getColor(view.context, R.color.acceptColor))
            }
        }
    }
}