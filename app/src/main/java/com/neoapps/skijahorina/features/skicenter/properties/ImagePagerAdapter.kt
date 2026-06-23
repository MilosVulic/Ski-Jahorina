package com.neoapps.skijahorina.features.skicenter.properties

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.neoapps.skijahorina.databinding.FragmentImageBinding

class ImagePagerAdapter(
    private val fragment: Fragment,
    private val imageUrls: List<String>
) : RecyclerView.Adapter<ImagePagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        Glide.with(fragment)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val imageView = holder.binding.imageView
                    imageView.setImageBitmap(resource)
                    adjustImageViewSize(imageView, resource.width, resource.height)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    override fun getItemCount(): Int = imageUrls.size

    private fun adjustImageViewSize(imageView: ImageView, imageWidth: Int, imageHeight: Int) {
        val layoutParams = imageView.layoutParams

        val parentWidth = (imageView.parent as ViewGroup).width
        val parentHeight = (imageView.parent as ViewGroup).height

        val imageAspectRatio = imageWidth.toFloat() / imageHeight.toFloat()
        val parentAspectRatio = parentWidth.toFloat() / parentHeight.toFloat()

        if (imageAspectRatio > parentAspectRatio) {
            layoutParams.width = parentWidth
            layoutParams.height = (parentWidth / imageAspectRatio).toInt()
        } else {
            layoutParams.height = parentHeight
            layoutParams.width = (parentHeight * imageAspectRatio).toInt()
        }

        imageView.layoutParams = layoutParams
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
    }

    inner class ViewHolder(val binding: FragmentImageBinding) : RecyclerView.ViewHolder(binding.root)
}
