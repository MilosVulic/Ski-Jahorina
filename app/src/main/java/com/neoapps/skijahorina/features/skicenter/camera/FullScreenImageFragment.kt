package com.neoapps.skijahorina.features.skicenter.camera

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.request.RequestOptions
import com.neoapps.skijahorina.R
import java.security.MessageDigest

class FullScreenImageFragment : Fragment() {

    private lateinit var fullScreenImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_full_screen_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fullScreenImageView = view.findViewById(R.id.fullScreenImage)

        val imageUrl = FullScreenImageFragmentArgs.fromBundle(requireArguments()).imageUrl

        // Use Glide to load the image and rotate it by 90 degrees
        Glide.with(requireContext())
            .load(imageUrl)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .apply(
                RequestOptions()
                    .transform(RotateTransformation())
            )
            .into(fullScreenImageView)


        fullScreenImageView.scaleType = ImageView.ScaleType.CENTER_CROP
    }
}

class RotateTransformation : BitmapTransformation() {

    override fun updateDiskCacheKey(p0: MessageDigest) {
        p0.update("rotate_90".toByteArray())
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(90f)  // Rotate 90 degrees
        return Bitmap.createBitmap(toTransform, 0, 0, toTransform.width, toTransform.height, matrix, true)
    }
}