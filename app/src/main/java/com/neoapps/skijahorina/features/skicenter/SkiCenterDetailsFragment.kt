package com.neoapps.skijahorina.features.skicenter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.neoapps.skijahorina.R
import com.neoapps.skijahorina.common.ads.AdClickStore
import com.neoapps.skijahorina.common.ads.AdsConfigFetcher
import com.neoapps.skijahorina.common.ads.AdsPolicyStore
import com.neoapps.skijahorina.common.ads.AdsScreens
import com.neoapps.skijahorina.common.ads.RotatingInterstitialAdManager
import com.neoapps.skijahorina.common.AppAnalytics
import com.neoapps.skijahorina.databinding.FragmentSkiCenterDetailsBinding
import com.neoapps.skijahorina.features.skicenter.camera.CameraViewModel
import com.neoapps.skijahorina.main.MainActivity
import kotlinx.coroutines.launch

class SkiCenterDetailsFragment : Fragment() {

    private var bindingProp: FragmentSkiCenterDetailsBinding? = null
    private val binding get() = bindingProp!!
    private var interstitialAdManager: RotatingInterstitialAdManager? = null

    private val viewModel: CameraViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentSkiCenterDetailsBinding.inflate(inflater, container, false)
        setUpFragmentName()
        bindClickListeners()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AppAnalytics.logFeatureOpened(AppAnalytics.Feature.HUB)

        interstitialAdManager = RotatingInterstitialAdManager(
            requireContext().applicationContext,
            RotatingInterstitialAdManager.HUB_UNIT_IDS
        ).also { it.preload() }

        viewLifecycleOwner.lifecycleScope.launch {
            AdsConfigFetcher.refreshIfNeeded()
        }

        ResortDashboardBinder.bindHubDashboard(binding, layoutInflater)

        viewLifecycleOwner.lifecycleScope.launch {
            JahorinaDataFetcher.refreshAll()
            if (bindingProp != null) {
                ResortDashboardBinder.bindHubDashboard(binding, layoutInflater)
            }
        }

        viewModel.cameraDataList.observe(viewLifecycleOwner) { cameraList ->
            if (bindingProp == null) return@observe
            cameraList?.firstOrNull()?.let { firstCamera ->
                Glide.with(binding.hubWebcamPreview)
                    .load(firstCamera.url)
                    .centerCrop()
                    .into(binding.hubWebcamPreview)
            }
        }
        viewModel.fetchCameraDataFromFirestore()
    }

    private fun bindClickListeners() {
        binding.quickActionWeather.setOnClickListener {
            handleMonetizedNavigation(AdsScreens.WEATHER) {
                findNavController().navigate(
                    com.neoapps.skijahorina.NavigationGraphDirections.actionWeatherInfo()
                )
            }
        }

        binding.quickActionLifts.setOnClickListener { openLifts() }
        binding.hubLiftsCard.setOnClickListener { openLifts() }
        binding.hubLiftsSeeAll.setOnClickListener { openLifts() }

        binding.quickActionWebcams.setOnClickListener { openCameras() }
        binding.hubWebcamsCard.setOnClickListener { openCameras() }
        binding.hubWebcamsSeeAll.setOnClickListener { openCameras() }

        binding.quickActionMap.setOnClickListener {
            handleMonetizedNavigation(AdsScreens.MAP) {
                findNavController().navigate(
                    com.neoapps.skijahorina.NavigationGraphDirections.actionSkiMap()
                )
            }
        }
        binding.quickActionUsefulInfo.setOnClickListener {
            handleMonetizedNavigation(AdsScreens.USEFUL_INFO) {
                findNavController().navigate(
                    com.neoapps.skijahorina.NavigationGraphDirections.actionUsefulInformation()
                )
            }
        }

        binding.hubApartmentsCard.setOnClickListener {
            handleMonetizedNavigation(AdsScreens.APARTMENTS) {
                findNavController().navigate(
                    com.neoapps.skijahorina.NavigationGraphDirections.actionPropertyDetails()
                )
            }
        }
    }

    private fun openLifts() {
        handleMonetizedNavigation(AdsScreens.LIFTS) {
            findNavController().navigate(
                com.neoapps.skijahorina.NavigationGraphDirections.actionLiftInfo()
            )
        }
    }

    private fun openCameras() {
        handleMonetizedNavigation(AdsScreens.CAMERAS) {
            findNavController().navigate(
                com.neoapps.skijahorina.NavigationGraphDirections.actionCamera()
            )
        }
    }

    override fun onDestroyView() {
        interstitialAdManager?.release()
        interstitialAdManager = null
        bindingProp?.hubWebcamPreview?.let { Glide.with(it).clear(it) }
        bindingProp = null
        super.onDestroyView()
    }

    private fun setUpFragmentName() {
        (activity as MainActivity).supportActionBar?.title = ""
        val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)
        title1TextView?.visibility = View.GONE
    }

    private fun handleMonetizedNavigation(screenId: String, navigate: () -> Unit) {
        val clicks = AdClickStore.increment(screenId)
        if (AdsPolicyStore.shouldShowAd(screenId, clicks)) {
            val shown = interstitialAdManager?.show(requireActivity()) {
                if (isAdded) navigate()
            } == true
            if (shown) {
                AdClickStore.reset(screenId)
                AppAnalytics.logAdShown(screenId)
            } else {
                navigate()
            }
        } else {
            navigate()
        }
    }
}
