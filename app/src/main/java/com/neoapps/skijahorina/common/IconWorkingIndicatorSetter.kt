package com.neoapps.skijahorina.common

import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.neoapps.skijahorina.R

object IconWorkingIndicatorSetter {

    fun getBooleanWorkability(inFunction: String): Boolean =
        liftStatusFrom(inFunction) == LiftStatus.OPEN

    fun displayImage(inFunction: String, view: ImageView) {
        when (liftStatusFrom(inFunction)) {
            LiftStatus.OPEN -> view.setImageResource(R.drawable.ic_check)
            LiftStatus.ON_HOLD -> view.setImageResource(R.drawable.ic_status_on_hold)
            LiftStatus.CLOSED -> view.setImageResource(R.drawable.ic_cancel)
        }
        view.contentDescription = statusContentDescription(view.context, inFunction)
    }

    fun setBackground(inFunction: String, view: ImageView) {
        when (liftStatusFrom(inFunction)) {
            LiftStatus.CLOSED ->
                view.setColorFilter(ContextCompat.getColor(view.context, R.color.cancelColor))
            LiftStatus.OPEN ->
                view.setColorFilter(ContextCompat.getColor(view.context, R.color.acceptColor))
            LiftStatus.ON_HOLD -> view.clearColorFilter()
        }
    }

    fun statusContentDescription(context: android.content.Context, inFunction: String): String {
        val res = when (liftStatusFrom(inFunction)) {
            LiftStatus.OPEN -> R.string.lift_status_open
            LiftStatus.CLOSED -> R.string.lift_status_closed
            LiftStatus.ON_HOLD -> R.string.lift_status_on_hold
        }
        return context.getString(res)
    }

    private fun liftStatusFrom(inFunction: String): LiftStatus {
        val status = inFunction.trim()
        return when {
            status.equals("unknown", ignoreCase = true) -> LiftStatus.ON_HOLD
            isOpenStatus(status) -> LiftStatus.OPEN
            isClosedStatus(status) -> LiftStatus.CLOSED
            else -> LiftStatus.CLOSED
        }
    }
}
