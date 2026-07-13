package com.neoapps.skijahorina.features.skicenter.lifts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.neoapps.skijahorina.R
import com.neoapps.skijahorina.common.LiftIconBinder
import com.neoapps.skijahorina.common.LiftStatus
import com.neoapps.skijahorina.common.ListStatusBinder
import com.neoapps.skijahorina.common.liftStatus
import com.neoapps.skijahorina.common.sortedByStatusThenName
import java.util.Locale

class LiftInfoAdapter : ListAdapter<LiftInfo, LiftInfoAdapter.ViewHolder>(DIFF) {

    private var sourceList: List<LiftInfo> = emptyList()
    private var query: String = ""
    private var statusFilter: LiftStatus? = null

    fun submitSource(list: List<LiftInfo>) {
        sourceList = list.sortedByStatusThenName()
        applyFilters()
    }

    fun setQuery(newQuery: String?) {
        query = newQuery?.trim().orEmpty()
        applyFilters()
    }

    fun setStatusFilter(status: LiftStatus?) {
        statusFilter = status
        applyFilters()
    }

    private fun applyFilters() {
        val queryLower = query.lowercase(Locale.ROOT)
        val filtered = sourceList.filter { lift ->
            val matchesQuery =
                queryLower.isEmpty() || lift.name.lowercase(Locale.ROOT).contains(queryLower)
            val matchesStatus = statusFilter == null || lift.liftStatus() == statusFilter
            matchesQuery && matchesStatus
        }
        submitList(filtered)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lift_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val liftInfo = getItem(position)
        val status = liftInfo.liftStatus()

        holder.textViewCardTitle.text = liftInfo.name
        holder.textViewType.text = liftInfo.type
        holder.textViewWorkingHours.text = liftInfo.openingHours
        holder.textViewWorkingHours.visibility =
            if (status == LiftStatus.OPEN && liftInfo.openingHours.isNotBlank()) {
                View.VISIBLE
            } else {
                View.GONE
            }

        ListStatusBinder.bind(holder.statusLabel, status)
        LiftIconBinder.bind(liftInfo.type, holder.liftIconContainer, holder.imageViewIcon)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val liftIconContainer: FrameLayout = itemView.findViewById(R.id.liftIconContainer)
        val textViewCardTitle: TextView = itemView.findViewById(R.id.textViewRowTitle)
        val textViewType: TextView = itemView.findViewById(R.id.textViewRowType)
        val textViewWorkingHours: TextView = itemView.findViewById(R.id.textViewRowWorkingHours)
        val statusLabel: TextView = itemView.findViewById(R.id.statusLabel)
        val imageViewIcon: ImageView = itemView.findViewById(R.id.imageViewIcon)
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<LiftInfo>() {
            override fun areItemsTheSame(oldItem: LiftInfo, newItem: LiftInfo): Boolean =
                oldItem.name == newItem.name && oldItem.type == newItem.type

            override fun areContentsTheSame(oldItem: LiftInfo, newItem: LiftInfo): Boolean =
                oldItem == newItem
        }
    }
}
