package com.example.skiserbia.features.skicenter.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.skiserbia.R
import com.example.skiserbia.databinding.FragmentSkiMapBinding
import com.example.skiserbia.features.skicenter.SkiCenterDetailsFragmentArgs

class SkiMapFragment : Fragment() {

    private var bindingProp: FragmentSkiMapBinding? = null
    private val binding get() = bindingProp!!
    private val skiCenterUrl: SkiCenterDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentSkiMapBinding.inflate(inflater, container, false)
        setMap(skiCenterUrl.skiCenter)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingProp = null
    }

    private fun setMap(skiCenterUrl: String) {
        if (skiCenterUrl.contains("kopaonik")) {
            binding.myZoomageView.setImageResource(R.drawable.kopaonik_ski_map)
        } else if (skiCenterUrl.contains("tornik")) {
            binding.myZoomageView.setImageResource(R.drawable.tornik_ski_map)
        } else {
            binding.myZoomageView.setImageResource(R.drawable.stara_planina_ski_map)
        }
    }
}
