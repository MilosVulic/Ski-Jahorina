package com.example.skiserbia.features.lifts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skiserbia.R
import com.example.skiserbia.common.IconLiftSetter
import de.hdodenhof.circleimageview.CircleImageView


class LiftInfoAdapter(private val mList: List<LiftInfo>) : RecyclerView.Adapter<LiftInfoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lift_row, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val liftInfo = mList[position]

        holder.textViewCardTitle.text = liftInfo.name
        IconLiftSetter.displayImage(liftInfo, holder.workingIndicator)
        IconLiftSetter.setBackground(liftInfo, holder.circleImageView)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textViewCardTitle: TextView = itemView.findViewById(R.id.textViewRowTitle)
        val workingIndicator: ImageView = itemView.findViewById(R.id.workingIndicator)
        val circleImageView: CircleImageView = itemView.findViewById(R.id.imageViewCircleCloseDialog)
    }
}