package com.neoapps.skiserbia.features.skicenter.slopes

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.neoapps.skiserbia.R
import com.neoapps.skiserbia.common.IconMarkCategorySetter
import com.neoapps.skiserbia.common.IconWorkingIndicatorSetter
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Locale

class SlopeInfoAdapter(private val mList: List<SlopeInfo>) : RecyclerView.Adapter<SlopeInfoAdapter.ViewHolder>(), Filterable {

    private var filteredList: List<SlopeInfo> = mList
    private var visibleList: List<SlopeInfo> = mList

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
                filteredList = results?.values as List<SlopeInfo>
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
            .inflate(R.layout.slope_row, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in visibleList.indices) {
            val slopeInfo = visibleList[position]

            holder.textViewCardTitle.text = slopeInfo.name
            holder.textViewMark.text = slopeInfo.mark
            IconWorkingIndicatorSetter.displayImage(slopeInfo.inFunction, holder.workingIndicator)
            IconWorkingIndicatorSetter.setBackground(slopeInfo.inFunction, holder.workingIndicator)
            IconMarkCategorySetter.setBackground(slopeInfo.category, holder.circleImageViewCategory)
            holder.itemView.visibility = View.VISIBLE
        } else {
            holder.itemView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return visibleList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewCardTitle: TextView = itemView.findViewById(R.id.textViewRowTitle)
        val workingIndicator: ImageView = itemView.findViewById(R.id.workingIndicator)
        val circleImageViewCategory: CircleImageView = itemView.findViewById(R.id.imageViewDifficulty)
        val textViewMark: TextView = itemView.findViewById(R.id.textViewMark)
    }
}
