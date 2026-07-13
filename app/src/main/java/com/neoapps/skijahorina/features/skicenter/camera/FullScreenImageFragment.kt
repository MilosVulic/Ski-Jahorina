package com.neoapps.skijahorina.features.skicenter.camera

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.request.RequestOptions
import com.neoapps.skijahorina.R
import java.security.MessageDigest

class FullScreenImageFragment : Fragment() {

    private var videoPlayer: WebcamVideoPlayer? = null
    private var imageUrl: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_full_screen_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageUrl = FullScreenImageFragmentArgs.fromBundle(requireArguments()).imageUrl
        val imageView = view.findViewById<ImageView>(R.id.fullScreenImage)
        val playerView = view.findViewById<PlayerView>(R.id.fullScreenPlayerView)
        val playButton = view.findViewById<ImageView>(R.id.fullScreenPlayButton)
        val container = view.findViewById<ViewGroup>(R.id.fullScreenContainer)

        if (WebcamUrlHelper.isLikelyStream(imageUrl)) {
            imageView.visibility = View.GONE
            playButton.visibility = View.VISIBLE
            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM

            videoPlayer = WebcamVideoPlayer(
                requireContext(),
                playerView,
                imageView,
                playButton,
                container
            ).also { player ->
                player.setup(imageUrl)
                playButton.setOnClickListener { player.toggle() }
                container.setOnClickListener { player.toggle() }
            }
        } else {
            playerView.visibility = View.GONE
            playButton.visibility = View.GONE
            imageView.visibility = View.VISIBLE
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP

            // Webcam stills are landscape; rotate so they fill the portrait screen.
            Glide.with(imageView)
                .load(imageUrl)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .apply(RequestOptions().transform(RotateTransformation()))
                .into(imageView)
        }
    }

    override fun onDestroyView() {
        videoPlayer?.release()
        videoPlayer = null
        super.onDestroyView()
    }
}

private class RotateTransformation : BitmapTransformation() {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("rotate_90".toByteArray())
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(90f)
        return Bitmap.createBitmap(
            toTransform,
            0,
            0,
            toTransform.width,
            toTransform.height,
            matrix,
            true
        )
    }
}
