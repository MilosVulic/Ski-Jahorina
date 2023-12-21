package com.neoapps.skiserbia.features.skicenter.camera

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.neoapps.skiserbia.R
import com.neoapps.skiserbia.databinding.FragmentAsyncCameraBinding
import com.neoapps.skiserbia.main.MainActivity


class CameraFragment : Fragment() {

    private var bindingProp: FragmentAsyncCameraBinding? = null
    private val binding get() = bindingProp!!
    private val viewModel: CameraViewModel by viewModels()
    private val skiCenterUrl: CameraFragmentArgs by navArgs()
    private lateinit var noInternetLayout: View

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingProp = FragmentAsyncCameraBinding.inflate(inflater, container, false)
        val screen = inflater.inflate(R.layout.fragment_camera, container, false)

        // Find the ProgressBar in the inflated view
        val progressBar = screen.findViewById<ProgressBar>(R.id.progressBar)

        // Inflate the "no internet" layout
        noInternetLayout =
            inflater.inflate(R.layout.include_empty_list_placeholder, container, false)


        val asyncLayoutInflater = context?.let { AsyncLayoutInflater(it) }
        asyncLayoutInflater?.inflate(R.layout.fragment_async_camera, null) { view, _, _ ->
            (screen as? ViewGroup)?.addView(view)
            bindingProp = FragmentAsyncCameraBinding.bind(view)
            setUpFragmentName()
            checkInternetConnection(progressBar)
        }
        return screen
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.imageList.observe(viewLifecycleOwner) { result ->
            result?.let { images ->
                val container = binding.imageContainer
                container.removeAllViews()

                for (imageBitmap in images) {
                    val cardView =
                        layoutInflater.inflate(R.layout.image_view_row, container, false) as CardView
                    val imageView = cardView.findViewById<ImageView>(R.id.imageView)

                    Glide.with(requireContext())
                        .load(imageBitmap)
                        .into(imageView)

                    val layoutParams = cardView.layoutParams as ViewGroup.MarginLayoutParams
                    cardView.radius = 45F
                    layoutParams.setMargins(40, 40, 40, 0)

                    container.addView(cardView)
                }
            }
        }

        viewModel.fetchImages(skiCenterUrl.skiCenter)
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
    private fun checkInternetConnection(progressBar: ProgressBar) {
        if (!isNetworkAvailable()) {
            progressBar.visibility = View.GONE
            binding.scrollView.visibility = View.GONE

            // Layout parameters for noInternetLayout
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

            (binding.root as? ViewGroup)?.addView(noInternetLayout, noInternetLayoutParams)

        } else {
            progressBar.visibility = View.VISIBLE
            binding.scrollView.visibility = View.VISIBLE
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }
    override fun onDestroyView() {
        super.onDestroyView()
        bindingProp = null
    }
}
