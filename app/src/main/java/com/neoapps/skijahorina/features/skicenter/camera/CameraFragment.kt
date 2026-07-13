package com.neoapps.skijahorina.features.skicenter.camera

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.media3.ui.PlayerView
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.neoapps.skijahorina.R
import com.neoapps.skijahorina.common.AppAnalytics
import com.neoapps.skijahorina.common.FetchEmptyKind
import com.neoapps.skijahorina.common.FetchEmptyState
import com.neoapps.skijahorina.common.NetworkStatus
import com.neoapps.skijahorina.databinding.FragmentAsyncCameraBinding
import com.neoapps.skijahorina.databinding.LayoutUnableToFetchDataBinding
import com.neoapps.skijahorina.main.MainActivity

class CameraFragment : Fragment() {

    private var bindingProp: FragmentAsyncCameraBinding? = null
    private val binding get() = bindingProp!!
    private val viewModel: CameraViewModel by viewModels()
    private var noInternetLayout: View? = null
    private val videoPlayers = mutableListOf<WebcamVideoPlayer>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentAsyncCameraBinding.inflate(inflater, container, false)
        setUpFragmentName()
        checkInternetConnection()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppAnalytics.logFeatureOpened(AppAnalytics.Feature.WEBCAMS)

        viewModel.cameraDataList.observe(viewLifecycleOwner) { cameraList ->
            if (!isViewActive()) return@observe
            releaseVideoPlayers()

            val container = binding.imageContainer
            container.removeAllViews()

            for (camera in cameraList.orEmpty()) {
                bindCameraRow(container, camera)
            }
        }

        if (NetworkStatus.isOnline(requireContext())) {
            viewModel.fetchCameraDataFromFirestore()
        }
    }

    private fun bindCameraRow(container: ViewGroup, camera: Camera) {
        val cardView = layoutInflater.inflate(R.layout.image_view_row, container, false) as CardView
        val title = cardView.findViewById<TextView>(R.id.textView1)
        val thumbnail = cardView.findViewById<ImageView>(R.id.thumbnail1)
        val playerView = cardView.findViewById<PlayerView>(R.id.playerView)
        val playButton = cardView.findViewById<ImageView>(R.id.playButton)
        val videoContainer = cardView.findViewById<ViewGroup>(R.id.videoContainer)

        title.text = camera.name

        val openFullScreen = {
            val action = CameraFragmentDirections.actionCameraFragmentToFullScreenImageFragment(camera.url)
            findNavController().navigate(action)
        }

        if (WebcamUrlHelper.isLikelyStream(camera.url)) {
            playButton.visibility = View.VISIBLE
            Glide.with(thumbnail)
                .load(camera.url)
                .centerCrop()
                .into(thumbnail)

            val player = WebcamVideoPlayer(
                requireContext(),
                playerView,
                thumbnail,
                playButton,
                videoContainer
            )
            player.setup(camera.url)
            playButton.setOnClickListener { player.toggle() }
            videoContainer.setOnClickListener { openFullScreen() }
            videoPlayers.add(player)
        } else {
            playButton.visibility = View.GONE
            playerView.visibility = View.GONE
            Glide.with(thumbnail)
                .load(camera.url)
                .centerCrop()
                .into(thumbnail)
            videoContainer.setOnClickListener { openFullScreen() }
        }

        val layoutParams = cardView.layoutParams as ViewGroup.MarginLayoutParams
        cardView.radius = 28F
        layoutParams.setMargins(15, 15, 15, 15)
        container.addView(cardView)
    }

    override fun onDestroyView() {
        releaseVideoPlayers()
        noInternetLayout?.let { (binding.root as? ViewGroup)?.removeView(it) }
        noInternetLayout = null
        bindingProp = null
        super.onDestroyView()
    }

    private fun releaseVideoPlayers() {
        videoPlayers.forEach { it.release() }
        videoPlayers.clear()
    }

    private fun isViewActive(): Boolean {
        return bindingProp != null &&
            viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
    }

    private fun setUpFragmentName() {
        (activity as MainActivity).supportActionBar?.title = ""
        val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)

        if (title1TextView != null) {
            title1TextView.visibility = View.VISIBLE
            title1TextView.text = resources.getText(R.string.webcams_lowercase)
        }
    }

    @SuppressLint("ResourceType")
    private fun checkInternetConnection() {
        removeOfflineLayout()
        if (!NetworkStatus.isOnline(requireContext())) {
            binding.scrollView.visibility = View.GONE

            val offlineLayout = layoutInflater.inflate(
                R.layout.layout_unable_to_fetch_data,
                binding.root as ViewGroup,
                false
            )
            val noInternetLayoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            noInternetLayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            noInternetLayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            noInternetLayoutParams.topToBottom = R.id.frameLayoutTop
            noInternetLayoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            noInternetLayoutParams.horizontalBias = 0.5f
            noInternetLayoutParams.verticalBias = 0.5f

            noInternetLayout = offlineLayout
            (binding.root as? ViewGroup)?.addView(offlineLayout, noInternetLayoutParams)

            FetchEmptyState.bind(
                LayoutUnableToFetchDataBinding.bind(offlineLayout),
                FetchEmptyKind.NO_INTERNET,
                onRetry = {
                    checkInternetConnection()
                    if (NetworkStatus.isOnline(requireContext())) {
                        viewModel.fetchCameraDataFromFirestore()
                    }
                }
            )
        } else {
            binding.scrollView.visibility = View.VISIBLE
        }
    }

    private fun removeOfflineLayout() {
        noInternetLayout?.let { layout ->
            (binding.root as? ViewGroup)?.removeView(layout)
            noInternetLayout = null
        }
    }
}
