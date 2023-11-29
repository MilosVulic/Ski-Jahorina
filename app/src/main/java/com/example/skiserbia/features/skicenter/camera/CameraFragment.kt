package com.example.skiserbia.features.skicenter.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.skiserbia.R
import com.example.skiserbia.databinding.FragmentCameraBinding
import com.example.skiserbia.features.skicenter.camera.CameraFragmentArgs


class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CameraViewModel by viewModels()
    private val skiCenterUrl: CameraFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe the LiveData and update the UI
        viewModel.imageList.observe(viewLifecycleOwner) { result ->
            result?.let { images ->
                val container = binding.imageContainer
                container.removeAllViews()

                for ((index, imageBitmap) in images.withIndex()) {
                    // Inflate the custom ImageView layout
                    val cardView =
                        layoutInflater.inflate(R.layout.image_view_row, container, false) as CardView
                    val imageView = cardView.findViewById<ImageView>(R.id.imageView)

                    // Load image using Glide
                    Glide.with(requireContext())
                        .load(imageBitmap)
                        .into(imageView)

                    // Set margins for the CardView
                    val layoutParams =
                        cardView.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.setMargins(0, 10, 0, 0)

                    // Add CardView to the container
                    container.addView(cardView)
                }
            }
        }

        // Trigger the image fetching when the fragment is created
        viewModel.fetchImages(skiCenterUrl.skiCenter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}