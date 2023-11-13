package com.example.skiserbia.features.skicenter.lifts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skiserbia.R
import com.example.skiserbia.common.IconWorkingIndicatorSetter


class LiftInfoAdapter(private val mList: List<LiftInfo>) : RecyclerView.Adapter<LiftInfoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lift_row, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val liftInfo = mList[position]

        holder.textViewCardTitle.text = liftInfo.name
        IconWorkingIndicatorSetter.displayImage(liftInfo.inFunction, holder.workingIndicator)
        IconWorkingIndicatorSetter.setBackground(liftInfo.inFunction, holder.workingIndicator)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textViewCardTitle: TextView = itemView.findViewById(R.id.textViewRowTitle)
        val workingIndicator: ImageView = itemView.findViewById(R.id.workingIndicator)
    }
}