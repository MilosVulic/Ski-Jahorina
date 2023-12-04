package com.example.skiserbia.features.skicenter.camera

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.skiserbia.R
import com.example.skiserbia.databinding.FragmentAsyncCameraBinding


class CameraFragment : Fragment() {

    private var _binding: FragmentAsyncCameraBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CameraViewModel by viewModels()
    private val skiCenterUrl: CameraFragmentArgs by navArgs()

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAsyncCameraBinding.inflate(inflater, container, false)
        val screen = inflater.inflate(R.layout.fragment_camera, container, false)

        val asyncLayoutInflater = context?.let { AsyncLayoutInflater(it) }
        asyncLayoutInflater?.inflate(R.layout.fragment_async_camera, null) { view, _, _ ->
            (screen as? ViewGroup)?.addView(view)
            _binding = FragmentAsyncCameraBinding.bind(view)
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

                    val layoutParams =
                        cardView.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.setMargins(0, 10, 0, 0)

                    container.addView(cardView)
                }
            }
        }

        viewModel.fetchImages(skiCenterUrl.skiCenter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
