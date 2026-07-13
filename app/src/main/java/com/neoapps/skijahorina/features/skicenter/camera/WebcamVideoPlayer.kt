package com.neoapps.skijahorina.features.skicenter.camera

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.neoapps.skijahorina.R

class WebcamVideoPlayer(
    private val context: Context,
    private val playerView: PlayerView,
    private val thumbnail: ImageView,
    private val playButton: ImageView,
    parent: ViewGroup
) {
    private var exoPlayer: ExoPlayer? = null
    private var videoUrl: String? = null
    private val handler = Handler(Looper.getMainLooper())
    private var loadTimeoutRunnable: Runnable? = null

    private val loadingIndicator: ProgressBar = ProgressBar(context).apply {
        isIndeterminate = true
        visibility = View.GONE
        layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.CENTER_IN_PARENT)
        }
    }

    private val errorText: TextView = TextView(context).apply {
        visibility = View.GONE
        setText(R.string.video_unavailable)
        setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
        textSize = 14f
        gravity = Gravity.CENTER
        setPadding(24, 24, 24, 8)
        layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.CENTER_IN_PARENT)
        }
    }

    private val retryButton: TextView = TextView(context).apply {
        visibility = View.GONE
        setText(R.string.retry)
        setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
        textSize = 14f
        gravity = Gravity.CENTER
        setPadding(24, 8, 24, 24)
        setOnClickListener { play() }
    }

    init {
        errorText.id = View.generateViewId()
        parent.addView(loadingIndicator)
        parent.addView(errorText)
        retryButton.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.CENTER_HORIZONTAL)
            addRule(RelativeLayout.BELOW, errorText.id)
            topMargin = 8
        }
        parent.addView(retryButton)
        playerView.useController = false
        playerView.visibility = View.GONE
    }

    fun setup(url: String) {
        release()
        videoUrl = url
        if (url.isBlank()) {
            playButton.isEnabled = false
            showError()
            return
        }
        playButton.isEnabled = true
        hideError()
        thumbnail.isVisible = true
        playButton.isVisible = true
        loadingIndicator.isVisible = false
        playerView.isVisible = false
    }

    fun play() {
        val url = videoUrl
        if (url.isNullOrBlank()) {
            showError()
            return
        }

        loadingIndicator.isVisible = true
        playButton.isVisible = false
        hideError()

        val player = exoPlayer ?: createPlayer().also {
            exoPlayer = it
            playerView.player = it
        }

        val currentUrl = player.currentMediaItem?.localConfiguration?.uri?.toString()
        if (currentUrl != url) {
            player.setMediaItem(MediaItem.fromUri(url))
            player.prepare()
        }

        startLoadTimeout()
        player.play()
    }

    fun pause() {
        cancelLoadTimeout()
        exoPlayer?.pause()
        loadingIndicator.isVisible = false
        playButton.isVisible = true
    }

    fun toggle() {
        if (exoPlayer?.isPlaying == true) pause() else play()
    }

    fun resetToThumbnail() {
        cancelLoadTimeout()
        exoPlayer?.stop()
        loadingIndicator.isVisible = false
        thumbnail.isVisible = true
        playButton.isVisible = true
        playerView.isVisible = false
    }

    fun release() {
        cancelLoadTimeout()
        exoPlayer?.release()
        exoPlayer = null
        playerView.player = null
    }

    private fun createPlayer(): ExoPlayer {
        return ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_READY -> {
                            cancelLoadTimeout()
                            loadingIndicator.isVisible = false
                            thumbnail.isVisible = false
                            playerView.isVisible = true
                            hideError()
                        }

                        Player.STATE_BUFFERING -> {
                            loadingIndicator.isVisible = true
                        }

                        Player.STATE_ENDED -> resetToThumbnail()
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    cancelLoadTimeout()
                    resetToThumbnail()
                    showError()
                }
            })
        }
    }

    private fun showError() {
        errorText.isVisible = true
        retryButton.isVisible = true
        playButton.isVisible = true
        loadingIndicator.isVisible = false
    }

    private fun hideError() {
        errorText.isVisible = false
        retryButton.isVisible = false
    }

    private fun startLoadTimeout() {
        cancelLoadTimeout()
        loadTimeoutRunnable = Runnable {
            resetToThumbnail()
            showError()
        }
        handler.postDelayed(loadTimeoutRunnable!!, LOAD_TIMEOUT_MS)
    }

    private fun cancelLoadTimeout() {
        loadTimeoutRunnable?.let { handler.removeCallbacks(it) }
        loadTimeoutRunnable = null
    }

    companion object {
        private const val LOAD_TIMEOUT_MS = 15_000L
    }
}
