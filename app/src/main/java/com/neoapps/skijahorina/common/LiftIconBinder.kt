package com.neoapps.skijahorina.common

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.neoapps.skijahorina.R

object LiftIconBinder {

    fun bind(type: String, container: View, icon: ImageView) {
        val style = styleFor(type)
        IconLiftSetter.displayImage(type, icon)
        container.setBackgroundResource(style.backgroundRes)
        icon.setColorFilter(ContextCompat.getColor(icon.context, style.tintRes))

        // Ensure icon stays centered inside circle.
        (icon.layoutParams as? FrameLayout.LayoutParams)?.let { params ->
            params.width = (48 * icon.context.resources.displayMetrics.density).toInt()
            params.height = params.width
            icon.layoutParams = params
        }
    }

    private fun styleFor(type: String): LiftIconStyle {
        val t = type.lowercase()
        return when {
            t.contains("gondola") -> LiftIconStyle(
                R.drawable.bg_lift_icon_gondola,
                R.color.liftIconGondolaTint
            )
            t.contains("chair") ||
                t.contains("šestosjed") || t.contains("sestosjed") ||
                t.contains("četvorosjed") || t.contains("cetvorosjed") ||
                t.contains("dvosjed") || t.contains("4") || t.contains("6") || t.contains("2") ->
                LiftIconStyle(R.drawable.bg_lift_icon_chair, R.color.liftIconChairTint)
            else -> LiftIconStyle(R.drawable.bg_lift_icon_tbar, R.color.liftIconTbarTint)
        }
    }

    private data class LiftIconStyle(
        val backgroundRes: Int,
        val tintRes: Int
    )
}
