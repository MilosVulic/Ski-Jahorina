package com.neoapps.skijahorina.features.skicenter.properties

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.neoapps.skijahorina.databinding.FragmentImageBinding

class ImagePagerAdapter(
    private val imageUrls: List<String>
) : RecyclerView.Adapter<ImagePagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageView = holder.binding.imageView
        Glide.with(imageView)
            .load(imageUrls[position])
            .fitCenter()
            .into(imageView)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        Glide.with(holder.binding.imageView).clear(holder.binding.imageView)
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int = imageUrls.size

    class ViewHolder(val binding: FragmentImageBinding) : RecyclerView.ViewHolder(binding.root)
}
