package com.neoapps.skijahorina.features.skicenter.lifts

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.neoapps.skijahorina.R
import com.neoapps.skijahorina.common.IconLiftSetter
import com.neoapps.skijahorina.common.IconWorkingIndicatorSetter
import com.neoapps.skijahorina.common.IconWorkingIndicatorSetter.getBooleanWorkability
import java.util.Locale

class LiftInfoAdapter(private val mList: List<LiftInfo>) : RecyclerView.Adapter<LiftInfoAdapter.ViewHolder>(),
    Filterable {

    private var filteredList: List<LiftInfo> = mList
    private var visibleList: List<LiftInfo> = mList

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val query = constraint?.toString()?.toLowerCase(Locale.ROOT)?.trim()

                filteredList = if (query.isNullOrEmpty()) {
                    mList
                } else {
                    mList.filter { it.name.toLowerCase(Locale.ROOT).contains(query) }
                }

                filterResults.values = filteredList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as List<LiftInfo>
                updateVisibleList()
            }
        }
    }

    private fun updateVisibleList() {
        visibleList = if (filteredList.isEmpty()) {
            emptyList()
        } else {
            filteredList
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lift_row, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in visibleList.indices) {
            val liftInfo = visibleList[position]

            holder.textViewCardTitle.text = liftInfo.name
            holder.textViewWorkingHours.text = setWorkingHours(liftInfo.workingHours)
            IconWorkingIndicatorSetter.displayImage(liftInfo.inFunction, holder.workingIndicator)
            IconWorkingIndicatorSetter.setBackground(liftInfo.inFunction, holder.workingIndicator)
            IconLiftSetter.displayImage(liftInfo.type, holder.imageViewIcon)

            if (!getBooleanWorkability(liftInfo.inFunction)) {
                holder.textViewWorkingHours.visibility = View.GONE
            } else {
                holder.textViewWorkingHours.visibility = View.VISIBLE
            }
            holder.itemView.visibility = View.VISIBLE // Ensure the item view is visible
        } else {
            // Hide the entire item view for positions that are out of bounds
            holder.itemView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return visibleList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewCardTitle: TextView = itemView.findViewById(R.id.textViewRowTitle)
        val textViewWorkingHours: TextView = itemView.findViewById(R.id.textViewRowWorkingHours)
        val workingIndicator: ImageView = itemView.findViewById(R.id.workingIndicator)
        val imageViewIcon: ImageView = itemView.findViewById(R.id.imageViewIcon)
    }

    private fun setWorkingHours(hours: String): String {
        return if (hours.length >= 5) {
            val from = hours.substring(0, 5)
            val to = hours.substring(hours.length - 5, hours.length)
            "$from  -  $to"
        } else {
            hours
        }
    }
}