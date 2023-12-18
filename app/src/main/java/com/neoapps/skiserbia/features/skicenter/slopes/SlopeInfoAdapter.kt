package com.neoapps.skiserbia.features.skicenter.slopes

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.ads.nativead.NativeAd
import com.neoapps.skiserbia.R
import com.neoapps.skiserbia.common.IconMarkCategorySetter
import com.neoapps.skiserbia.common.IconWorkingIndicatorSetter
import de.hdodenhof.circleimageview.CircleImageView

class SlopeInfoAdapter(private val mList: MutableList<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SLOPE = 0
    private val VIEW_TYPE_AD = 1
    var adLoaded = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SLOPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.slope_row, parent, false)
                SlopeViewHolder(view)
            }
            VIEW_TYPE_AD -> {
                val adView = LayoutInflater.from(parent.context).inflate(R.layout.ad_layout, parent, false)
                Log.d("SlopeInfoAdapter", "Creating AdViewHolder")
                AdViewHolder(adView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_SLOPE -> {
                if (holder is SlopeViewHolder) {
                    val slopeInfo = mList[position] as? SlopeInfo
                    slopeInfo?.let {
                        Log.d("SlopeInfoAdapter", "Binding SlopeViewHolder at position: $position")
                        holder.bind(slopeInfo)
                    }
                }
            }
            VIEW_TYPE_AD -> {
                if (holder is AdViewHolder) {
                    val nativeAd = mList[position] as? NativeAd
                    nativeAd?.let {
                        Log.d("SlopeInfoAdapter", "Binding AdViewHolder at position: $position")
                        holder.bind(nativeAd)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 2 && adLoaded) {
            // Return VIEW_TYPE_AD only for the third position if an ad is loaded
            VIEW_TYPE_AD
        } else if (position < mList.size - 1 && mList[position] is NativeAd) {
            // Return VIEW_TYPE_AD for other positions where the item is a UnifiedNativeAd
            VIEW_TYPE_AD
        } else {
            // Return VIEW_TYPE_SLOPE for regular slopes or last position
            VIEW_TYPE_SLOPE
        }
    }

    class SlopeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewCardTitle: TextView = itemView.findViewById(R.id.textViewRowTitle)
        private val workingIndicator: ImageView = itemView.findViewById(R.id.workingIndicator)
        private val circleImageViewCategory: CircleImageView = itemView.findViewById(R.id.imageViewDifficulty)
        private val textViewMark: TextView = itemView.findViewById(R.id.textViewMark)

        fun bind(slopeInfo: SlopeInfo) {
            textViewCardTitle.text = slopeInfo.name
            textViewMark.text = slopeInfo.mark
            IconWorkingIndicatorSetter.displayImage(slopeInfo.inFunction, workingIndicator)
            IconWorkingIndicatorSetter.setBackground(slopeInfo.inFunction, workingIndicator)
            IconMarkCategorySetter.setBackground(slopeInfo.category, circleImageViewCategory)
        }
    }

    class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val adTitle: TextView = itemView.findViewById(R.id.nativeAdTitle)
        private val adIcon: ImageView = itemView.findViewById(R.id.nativeAdIcon)

        init {
            Log.d("AdViewHolder", "AdViewHolder instantiated")
        }

        fun bind(nativeAd: NativeAd) {
            Log.d("AdViewHolder", "Binding AdViewHolder")
            Log.d("AdViewHolder", "Images are" + nativeAd.images)
            adTitle.text = nativeAd.headline
            Glide.with(itemView.context)
                .load(nativeAd.icon?.uri)
                .into(adIcon)

            adTitle.visibility = View.VISIBLE
            adIcon.visibility = View.VISIBLE


            itemView.setOnClickListener {
                val clickAction = nativeAd.callToAction
                if (!clickAction.isNullOrBlank() && Patterns.WEB_URL.matcher(clickAction).matches()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(clickAction))
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    // Add this method to add native ads to the dataset
    fun addNativeAd(nativeAd: NativeAd) {
        if (!adLoaded) {
            mList.add(2, nativeAd)
            notifyItemInserted(2)
            adLoaded = true
        }
    }
}
