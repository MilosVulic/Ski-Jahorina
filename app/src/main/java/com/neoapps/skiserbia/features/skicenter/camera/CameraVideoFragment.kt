package com.neoapps.skiserbia.features.skicenter.camera

import android.app.Activity
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.neoapps.skiserbia.R
import com.neoapps.skiserbia.common.PreferenceProvider
import com.neoapps.skiserbia.databinding.FragmentCameraVideoBinding
import com.neoapps.skiserbia.main.MainActivity

class CameraVideoFragment : Fragment() {

    private var bindingProp: FragmentCameraVideoBinding? = null
    private val binding get() = bindingProp!!
    private var mInterstitialCameraAd: InterstitialAd? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentCameraVideoBinding.inflate(inflater, container, false)
        setUpFragmentName()
        binding.cardView1.requestFocus()

        // "ca-app-pub-7130760675198405/7607461423"
        // test ca-app-pub-3940256099942544/1033173712
        val adRequest1 = AdRequest.Builder().build()
        InterstitialAd.load(requireContext(), "ca-app-pub-7130760675198405/7607461423", adRequest1, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialCameraAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialCameraAd = interstitialAd
            }
        })


        mInterstitialCameraAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                mInterstitialCameraAd = null
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.scrollView.setOnScrollChangeListener { _, _, _, _, _ ->

            handleVideoPlayback(binding.videoView1, binding.playButton1, binding.thumbnail1)
            handleVideoPlayback(binding.videoView2, binding.playButton2, binding.thumbnail2)
            handleVideoPlayback(binding.videoView4, binding.playButton4, binding.thumbnail4)
            handleVideoPlayback(binding.videoView5, binding.playButton5, binding.thumbnail5)
            handleVideoPlayback(binding.videoView6, binding.playButton6, binding.thumbnail6)
            handleVideoPlayback(binding.videoView7, binding.playButton7, binding.thumbnail7)
            handleVideoPlayback(binding.videoView8, binding.playButton8, binding.thumbnail8)
            handleVideoPlayback(binding.videoView9, binding.playButton9, binding.thumbnail9)
            handleVideoPlayback(binding.videoView10, binding.playButton10, binding.thumbnail10)
        }

        val imageUrls = listOf(
            "https://www.infokop.net/images/webcam_thumb/thumb_webcam-kopaonik-pancic1.jpg",
            "https://www.infokop.net/images/webcam_thumb/thumb_webcam-kopaonik-suvorudiste1.jpg",
            "https://www.infokop.net/images/webcam_thumb/thumb_webcam-kopaonik-srebrnac3.jpg",
            "https://www.infokop.net/images/webcam_thumb/thumb_webcam-kopaonik-gondola.jpg",
            "https://www.infokop.net/images/webcam_thumb/thumb_webcamkopaonik9.jpg",
            "https://www.infokop.net/images/webcam_thumb/thumb_skituljko.jpg",
            "https://www.infokop.net/images/webcam_thumb/thumb_webcam-kopaonik-crownpeaks.jpg",
            "https://www.infokop.net/images/webcam_thumb/thumb_webcam-kopaonik-green.jpg",
            "https://www.infokop.net/images/webcam_thumb/thumb_webcamkopaonik5.jpg"
        )

        for (element in imageUrls) {
            Glide.with(this)
                .load(element)
                .preload()
        }

        for (i in imageUrls.indices) {
            val bindingThumbnail = when (i) {
                0 -> binding.thumbnail1
                1 -> binding.thumbnail2
                2 -> binding.thumbnail4
                3 -> binding.thumbnail5
                4 -> binding.thumbnail6
                5 -> binding.thumbnail7
                6 -> binding.thumbnail8
                7 -> binding.thumbnail9
                8 -> binding.thumbnail10
                else -> throw IllegalArgumentException("Invalid index: $i")
            }

            Glide.with(this)
                .load(imageUrls[i])
                .fitCenter()
                .into(bindingThumbnail)
        }

        setupVideoView(
            binding.videoView1,
            binding.playButton1,
            "https://stream5.infokop.net:1443/live_cameras/dolinasportova/playlist.m3u8",
            binding.relativeLayout1,
            binding.thumbnail1
        )

        setupVideoView(
            binding.videoView2,
            binding.playButton2,
            "https://stream5.infokop.net:1443/live_cameras/suvorudiste/playlist.m3u8",
            binding.relativeLayout2,
            binding.thumbnail2
        )

        setupVideoView(
            binding.videoView4,
            binding.playButton4,
            "https://stream5.infokop.net:1443/live_cameras/gobelja/playlist.m3u8",
            binding.relativeLayout4,
            binding.thumbnail4
        )

        setupVideoView(
            binding.videoView5,
            binding.playButton5,
            "https://stream5.infokop.net:1443/live_cameras/gondola/playlist.m3u8",
            binding.relativeLayout5,
            binding.thumbnail5
        )

        setupVideoView(
            binding.videoView6,
            binding.playButton6,
            "https://stream5.infokop.net:1443/live_cameras/camera_6/playlist.m3u8",
            binding.relativeLayout6,
            binding.thumbnail6
        )
        setupVideoView(
            binding.videoView7,
            binding.playButton7,
            "https://stream5.infokop.net:1443/live_cameras/skituljko/playlist.m3u8",
            binding.relativeLayout7,
            binding.thumbnail7
        )

        setupVideoView(
            binding.videoView8,
            binding.playButton8,
            "https://stream5.infokop.net:1443/live_cameras/jaram/playlist.m3u8",
            binding.relativeLayout8,
            binding.thumbnail8
        )

        setupVideoView(
            binding.videoView9,
            binding.playButton9,
            "https://stream5.infokop.net:1443/live_cameras/green/playlist.m3u8",
            binding.relativeLayout9,
            binding.thumbnail9
        )
        setupVideoView(
            binding.videoView10,
            binding.playButton10,
            "https://stream5.infokop.net:1443/live_cameras/camera_2/playlist.m3u8",
            binding.relativeLayout10,
            binding.thumbnail10
        )
    }

    private fun setupVideoView(
        videoView: VideoView,
        playButton: ImageView,
        videoUrl: String,
        constraintLayout: ConstraintLayout,
        imageView: ImageView
    ) {
        resetVideoView(videoView)

        val videoUri = Uri.parse(videoUrl)
        videoView.setVideoURI(videoUri)

        playButton.setOnClickListener {
            PreferenceProvider.cameraClicks += 1
            if (PreferenceProvider.cameraClicks % 3 == 0) {
                if (mInterstitialCameraAd != null) {
                    PreferenceProvider.cameraClicks = 0
                    mInterstitialCameraAd?.show(context as Activity)
                } else {
                    if (videoView.isPlaying) {
                        playButton.visibility = View.VISIBLE
                        videoView.pause()
                    } else {
                        playButton.visibility = View.GONE
                        videoView.start()
                    }

                    if (imageView.isVisible){
                        imageView.visibility = View.GONE
                    }
                }
            } else {
                if (videoView.isPlaying) {
                    playButton.visibility = View.VISIBLE
                    videoView.pause()
                } else {
                    playButton.visibility = View.GONE
                    videoView.start()
                }

                if (imageView.isVisible){
                    imageView.visibility = View.GONE
                }
            }
        }

        constraintLayout.setOnClickListener {
            if (videoView.isPlaying) {
                playButton.visibility = View.VISIBLE
                videoView.pause()
            } else {
                if (playButton.visibility == View.GONE) {
                    playButton.visibility = View.VISIBLE
                    videoView.pause()
                } else {
                    playButton.visibility = View.GONE
                    videoView.start()
                }
            }

            if (imageView.isVisible){
                imageView.visibility = View.GONE
            }
        }

        videoView.setOnPreparedListener {
        }

        videoView.setOnErrorListener { nesto, nesto1, nesto2 ->
            if (nesto2.toString() != "-1004") {
                activity?.recreate()
            }
            true
        }

    }

    private fun handleVideoPlayback(
        videoView: VideoView,
        playButton: ImageView,
        thumbnailView: ImageView
    ) {
        if (videoView.isPlaying && getVisibilityPercentage(videoView) < 0.1) {
            videoView.pause()
            playButton.visibility = View.VISIBLE
            thumbnailView.visibility = View.VISIBLE
        }
    }

    private fun resetVideoView(videoView: VideoView) {
        videoView.stopPlayback()
        videoView.resume()
        videoView.setVideoURI(null)
    }


    private fun setUpFragmentName() {
        (activity as MainActivity).supportActionBar?.title = ""
        val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)

        if (title1TextView != null) {
            title1TextView.visibility = View.VISIBLE
            title1TextView.text = resources.getText(R.string.webcams_lowercase)
        }
    }

    private fun getVisibilityPercentage(view: View): Float {
        val scrollBounds = Rect()
        binding.scrollView.getGlobalVisibleRect(scrollBounds)

        val viewRect = Rect()
        view.getGlobalVisibleRect(viewRect)

        val visibleRegion = Rect()
        if (viewRect.intersect(scrollBounds)) {
            visibleRegion.set(viewRect)
        } else {
            visibleRegion.setEmpty()
        }

        val visibleArea = visibleRegion.width() * visibleRegion.height()
        val totalArea = view.width * view.height

        return if (totalArea > 0) visibleArea.toFloat() / totalArea.toFloat() else 0f
    }
    override fun onDestroyView() {
        super.onDestroyView()
        bindingProp = null
    }
}