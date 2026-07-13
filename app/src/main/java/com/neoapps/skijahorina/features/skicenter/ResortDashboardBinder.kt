package com.neoapps.skijahorina.features.skicenter

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.neoapps.skijahorina.R
import com.neoapps.skijahorina.common.CacheTimestampFormatter
import com.neoapps.skijahorina.common.IconWorkingIndicatorSetter
import com.neoapps.skijahorina.common.LiftStatus
import com.neoapps.skijahorina.common.PreferenceProvider
import com.neoapps.skijahorina.common.SnowDepthFormatter
import com.neoapps.skijahorina.common.liftStatus
import com.neoapps.skijahorina.common.openCount
import com.neoapps.skijahorina.databinding.FragmentSkiCenterDetailsBinding
import com.neoapps.skijahorina.features.skicenter.lifts.LiftInfo
import com.neoapps.skijahorina.features.skicenter.weather.parseCachedLifts

object ResortDashboardBinder {

    fun bindHubDashboard(binding: FragmentSkiCenterDetailsBinding, inflater: LayoutInflater) {
        val context = binding.root.context
        val lifts = parseCachedLifts()
        val open = lifts.openCount()
        val total = lifts.size
        val temp = PreferenceProvider.temperature.trim()
        val snow = SnowDepthFormatter.formatForDisplay(PreferenceProvider.snow)

        binding.hubStatusTemperature.text = temp.ifBlank { "—" }
        binding.hubStatusSnow.text = snow
        binding.hubStatusLifts.text = if (total > 0) {
            context.getString(R.string.hub_lifts_ratio, open, total)
        } else {
            "—"
        }

        val updated = CacheTimestampFormatter.bestTimestamp(
            PreferenceProvider.resortApiUpdatedAt,
            PreferenceProvider.lastWeatherFetchTime.takeIf { it != "2007-12-03T10:15:30" }
                ?: PreferenceProvider.lastLiftInfoJahorinaFetchTime
        )
        binding.hubStatusUpdated.text = if (updated != null) {
            context.getString(R.string.last_updated, updated)
        } else {
            context.getString(R.string.resort_data_unavailable)
        }

        bindStatusChip(binding.hubStatusChip, resolveStatus(lifts))
        bindLiftSnapshot(binding.hubLiftsSnapshotList, inflater, lifts)
    }

    private fun resolveStatus(lifts: List<LiftInfo>): ResortStatus {
        if (lifts.isEmpty()) return ResortStatus.UNKNOWN
        val open = lifts.count { it.liftStatus() == LiftStatus.OPEN }
        val closed = lifts.count { it.liftStatus() == LiftStatus.CLOSED }
        return when {
            open == lifts.size -> ResortStatus.OPEN
            open == 0 && closed == lifts.size -> ResortStatus.CLOSED
            open > 0 -> ResortStatus.PARTIAL
            else -> ResortStatus.UNKNOWN
        }
    }

    private fun bindStatusChip(chip: TextView, status: ResortStatus) {
        chip.text = chip.context.getString(
            when (status) {
                ResortStatus.OPEN -> R.string.resort_status_open
                ResortStatus.PARTIAL -> R.string.resort_status_partial
                ResortStatus.CLOSED -> R.string.resort_status_closed
                ResortStatus.UNKNOWN -> R.string.resort_status_unknown
            }
        )
        val backgroundRes = when (status) {
            ResortStatus.OPEN -> R.drawable.bg_status_chip_open
            ResortStatus.PARTIAL -> R.drawable.bg_status_chip_partial
            ResortStatus.CLOSED -> R.drawable.bg_status_chip_closed
            ResortStatus.UNKNOWN -> R.drawable.bg_status_chip_unknown
        }
        val textColorRes = when (status) {
            ResortStatus.OPEN -> R.color.statusOpenText
            ResortStatus.PARTIAL -> R.color.statusPartialText
            ResortStatus.CLOSED -> R.color.statusClosedText
            ResortStatus.UNKNOWN -> R.color.statusUnknownText
        }
        chip.setBackgroundResource(backgroundRes)
        chip.setTextColor(ContextCompat.getColor(chip.context, textColorRes))
    }

    private fun bindLiftSnapshot(
        container: LinearLayout,
        inflater: LayoutInflater,
        lifts: List<LiftInfo>
    ) {
        container.removeAllViews()
        if (lifts.isEmpty()) {
            container.visibility = View.GONE
            return
        }
        container.visibility = View.VISIBLE
        lifts.take(3).forEach { lift ->
            val row = inflater.inflate(R.layout.item_hub_lift_snapshot, container, false)
            val dot = row.findViewById<View>(R.id.liftStatusDot)
            val isOpen = IconWorkingIndicatorSetter.getBooleanWorkability(lift.inFunction)
            dot.setBackgroundResource(
                if (isOpen) R.drawable.bg_hub_status_dot_open else R.drawable.bg_hub_status_dot_closed
            )
            row.findViewById<TextView>(R.id.liftSnapshotName).text = lift.name
            container.addView(row)
        }
    }
}

enum class ResortStatus {
    OPEN,
    PARTIAL,
    CLOSED,
    UNKNOWN,
}
