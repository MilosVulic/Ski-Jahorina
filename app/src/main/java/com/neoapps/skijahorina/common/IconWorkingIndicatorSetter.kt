package com.neoapps.skijahorina.common

import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.neoapps.skijahorina.R

object IconWorkingIndicatorSetter {

    fun getBooleanWorkability(inFunction: String) : Boolean {
        return when (inFunction) {
            "/img/resorts/lift-status-close.svg" -> false
            "/img/resorts/lift-status-closed.svg" -> false
            "/img/resorts/lift-status-open.svg" -> true
            "/img/resorts/lift-status-opened.svg" -> true
            else -> {false}
        }
    }

    fun displayImage(inFunction: String, view: ImageView) {
        when (inFunction) {
            "/img/resorts/lift-status-close.svg", "/img/resorts/lift-status-closed.svg", "close", "Close", "closed", "Closed" -> view.setImageResource(R.drawable.ic_cancel)
            "/img/resorts/lift-status-open.svg", "/img/resorts/lift-status-opened.svg", "open", "Open", "opened", "Opened" -> view.setImageResource(R.drawable.ic_check)
            "unknown", "Unknown" -> view.setImageResource(R.drawable.ic_status_on_hold)
            else -> view.setImageResource(R.drawable.ic_cancel)
        }
    }

    fun setBackground(inFunction: String, view: ImageView) {
        when (inFunction) {
            "/img/resorts/lift-status-close.svg", "/img/resorts/lift-status-closed.svg", "close", "Close", "closed", "Closed" -> {
                view.setColorFilter(ContextCompat.getColor(view.context, R.color.cancelColor))
            }
            "/img/resorts/lift-status-open.svg", "/img/resorts/lift-status-opened.svg", "open", "Open", "opened", "Opened" -> {
                view.setColorFilter(ContextCompat.getColor(view.context, R.color.acceptColor))
            }
            "unknown", "Unknown" -> {
                return
            }
            else -> view.setColorFilter(ContextCompat.getColor(view.context, R.color.cancelColor))
        }
    }
}
