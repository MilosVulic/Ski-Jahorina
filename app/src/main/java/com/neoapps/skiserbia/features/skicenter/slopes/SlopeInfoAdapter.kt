package com.neoapps.skiserbia.features.skicenter.slopes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.neoapps.skiserbia.R
import com.neoapps.skiserbia.common.IconMarkCategorySetter
import com.neoapps.skiserbia.common.IconWorkingIndicatorSetter
import de.hdodenhof.circleimageview.CircleImageView

class SlopeInfoAdapter(private val mList: List<SlopeInfo>) : RecyclerView.Adapter<SlopeInfoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.slope_row, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val slopeInfo = mList[position]

        holder.textViewCardTitle.text = slopeInfo.name
        holder.textViewMark.text = slopeInfo.mark
        IconWorkingIndicatorSetter.displayImage(slopeInfo.inFunction, holder.workingIndicator)
        IconWorkingIndicatorSetter.setBackground(slopeInfo.inFunction, holder.workingIndicator)
        IconMarkCategorySetter.setBackground(slopeInfo.category, holder.circleImageViewCategory)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textViewCardTitle: TextView = itemView.findViewById(R.id.textViewRowTitle)
        val workingIndicator: ImageView = itemView.findViewById(R.id.workingIndicator)
        val circleImageViewCategory: CircleImageView = itemView.findViewById(R.id.imageViewDifficulty)
        val textViewMark: TextView = itemView.findViewById(R.id.textViewMark)
    }
}