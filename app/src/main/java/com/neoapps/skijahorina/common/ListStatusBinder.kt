package com.neoapps.skijahorina.common

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.neoapps.skijahorina.R

object ListStatusBinder {

    fun bind(label: TextView, status: LiftStatus) {
        val context = label.context
        when (status) {
            LiftStatus.OPEN -> {
                label.text = context.getString(R.string.lift_status_open)
                label.setBackgroundResource(R.drawable.bg_status_chip_open)
                label.setTextColor(ContextCompat.getColor(context, R.color.statusOpenText))
            }
            LiftStatus.CLOSED -> {
                label.text = context.getString(R.string.lift_status_closed)
                label.setBackgroundResource(R.drawable.bg_status_chip_closed)
                label.setTextColor(ContextCompat.getColor(context, R.color.statusClosedText))
            }
            LiftStatus.ON_HOLD -> {
                label.text = context.getString(R.string.lift_status_on_hold)
                label.setBackgroundResource(R.drawable.bg_status_chip_partial)
                label.setTextColor(ContextCompat.getColor(context, R.color.statusPartialText))
            }
        }
    }
}
