package com.neoapps.skijahorina.features.skicenter

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.neoapps.skijahorina.R
import com.neoapps.skijahorina.common.PreferenceProvider
import com.neoapps.skijahorina.databinding.FragmentSkiCenterDetailsBinding
import com.neoapps.skijahorina.features.skicenter.camera.CameraViewModel
import com.neoapps.skijahorina.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream

class SkiCenterDetailsFragment : Fragment() {

    private var bindingProp: FragmentSkiCenterDetailsBinding? = null
    private val binding get() = bindingProp!!
    private var weatherInterstitialAd: InterstitialAd? = null
    private var liftsInterstitialAd: InterstitialAd? = null
    private var camerasInterstitialAd: InterstitialAd? = null

    private val viewModel: CameraViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentSkiCenterDetailsBinding.inflate(inflater, container, false)
        setUpFragmentName()
        setHeaderVisibility()
        setUpAds()

        binding.cardViewMapBox.setOnClickListener {
            findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionSkiMap())
        }

        binding.includedHeader.cardViewWeather.setOnClickListener {
            PreferenceProvider.weatherClicks += 1
            if (PreferenceProvider.weatherClicks % 3 == 0) {
                if (weatherInterstitialAd != null) {
                    PreferenceProvider.weatherClicks = 0
                    weatherInterstitialAd?.show(context as Activity)
                } else {
                    findNavController().navigate(
                        com.neoapps.skijahorina.NavigationGraphDirections.actionWeatherInfo()
                    )
                }
            } else {
                findNavController().navigate(
                    com.neoapps.skijahorina.NavigationGraphDirections.actionWeatherInfo()
                )
            }
        }

        binding.includedHeader.cardViewUsefulInfo.setOnClickListener {
            findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionPropertyDetails())
        }

        binding.moreUsefulInfoIcon.setOnClickListener {
            findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionUsefulInformation())
        }

        binding.cardViewCameraBox.setOnClickListener {
            PreferenceProvider.cameraClicks += 1
            if (PreferenceProvider.cameraClicks % 3 == 0) {
                if (camerasInterstitialAd != null) {
                    PreferenceProvider.cameraClicks = 0
                    camerasInterstitialAd?.show(context as Activity)
                } else {
                    findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionCamera())

                }
            } else {
                findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionCamera())
            }
        }

        binding.includedHeader.cardViewLifts.setOnClickListener {
            PreferenceProvider.liftsClicks += 1
            if (PreferenceProvider.liftsClicks % 3 == 0) {
                if (liftsInterstitialAd != null) {
                    PreferenceProvider.liftsClicks = 0
                    liftsInterstitialAd?.show(context as Activity)
                } else {
                    findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionLiftInfo())

                }
            } else {
                findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionLiftInfo())
            }

        }

        binding.downloadIcon.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val dialog = showProgressDialog()
                val result = withContext(Dispatchers.IO) { downloadSkiMap() }
                dialog.dismiss()
                Toast.makeText(
                    context,
                    if (result) getString(R.string.homepage_download_successful) else getString(R.string.homepage_download_failed),
                    Toast.LENGTH_LONG
                ).show()
            }
        }


        binding.firstNumber.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + requireContext().getString(R.string.mountain_rescue_jahorina_number)))
            startActivity(dialIntent)
        }

        binding.secondNumber.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + requireContext().getString(R.string.emergency_number)))
            startActivity(dialIntent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.cameraDataList.observe(viewLifecycleOwner) { cameraList ->
            cameraList?.firstOrNull()?.let { firstCamera ->
                val imageView = binding.imageViewFullCameraBox

                Glide.with(requireContext())
                    .load(firstCamera.url)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageView)
            }
        }

        viewModel.fetchCameraDataFromFirestore()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingProp = null
    }

    private fun setUpFragmentName() {
        (activity as MainActivity).supportActionBar?.title = ""
        val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)

        if (title1TextView != null) {
            title1TextView.visibility = View.GONE
        }
    }

    private fun setHeaderVisibility() {
        binding.includedHeader.imageViewPinDotCircle1.visibility = View.VISIBLE
        binding.includedHeader.imageViewPinDotCircle2.visibility = View.INVISIBLE
        binding.includedHeader.imageViewPinDotCircle3.visibility = View.INVISIBLE
        binding.includedHeader.imageViewPinDotCircle4.visibility = View.INVISIBLE
    }


    private fun showProgressDialog(): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setView(layoutInflater.inflate(R.layout.progress_dialog, null))
        val dialog = builder.create()
        dialog.show()
        return dialog
    }

    private fun downloadSkiMap(): Boolean {
        val skiMapDrawable = resources.getDrawable(R.drawable.jahorina_ski_map, null)
        val bitmap = (skiMapDrawable as BitmapDrawable).bitmap
        return saveBitmapToGallery(bitmap)
    }

    private fun saveBitmapToGallery(bitmap: Bitmap): Boolean {
        val outputStream: OutputStream?
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Use MediaStore for Android 10+
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "ski_map.png")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SkiMaps") // Save in Pictures/SkiMaps folder
                }

                val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                val uri = context?.contentResolver?.insert(contentUri, contentValues) ?: return false
                outputStream = context?.contentResolver?.openOutputStream(uri)
            } else {
                val directory = context?.getExternalFilesDir("SkiMaps")
                val file = java.io.File(directory, "ski_map.png")
                outputStream = file.outputStream()
            }

            outputStream.use {
                if (it != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun setUpAds() {
        // "ca-app-pub-7130760675198405/2157972704"
        // test ca-app-pub-3940256099942544/1033173712
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(requireContext(), "ca-app-pub-7130760675198405/2157972704", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                weatherInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                weatherInterstitialAd = interstitialAd
            }
        })

        // "ca-app-pub-7130760675198405/4592564355"
        // test ca-app-pub-3940256099942544/1033173712
        val adRequest1 = AdRequest.Builder().build()
        InterstitialAd.load(requireContext(), "ca-app-pub-7130760675198405/4592564355", adRequest1, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                liftsInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                liftsInterstitialAd = interstitialAd
            }
        })

        // "ca-app-pub-7130760675198405/8365994691"
        // test ca-app-pub-3940256099942544/1033173712
        val adRequest2 = AdRequest.Builder().build()
        InterstitialAd.load(requireContext(), "ca-app-pub-7130760675198405/8365994691", adRequest2, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                camerasInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                camerasInterstitialAd = interstitialAd
            }
        })

        weatherInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                weatherInterstitialAd = null
                findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionWeatherInfo())
            }
        }

        liftsInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                liftsInterstitialAd = null
                findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionLiftInfo())
            }
        }

        camerasInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                camerasInterstitialAd = null
                findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionCamera())
            }
        }
    }
}