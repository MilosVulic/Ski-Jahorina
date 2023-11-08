package com.example.skiserbia.features.lifts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.skiserbia.NavigationGraphDirections
import com.example.skiserbia.databinding.FragmentSkiCenterDetailsBinding

class SkiCenterDetailsFragment : Fragment() {

    private var bindingProp: FragmentSkiCenterDetailsBinding? = null
    private val binding get() = bindingProp!!

    private val skiCenterUrl: SkiCenterDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentSkiCenterDetailsBinding.inflate(inflater, container, false)

        binding.cardViewLiftInfo.setOnClickListener {
            findNavController().navigate(NavigationGraphDirections.actionLiftInfo(skiCenterUrl.skiCenter))
        }

        binding.cardViewSlopesInfo.setOnClickListener {
            findNavController().navigate(NavigationGraphDirections.actionLiftInfo(skiCenterUrl.skiCenter))
        }

        binding.cardViewMap.setOnClickListener {
            findNavController().navigate(NavigationGraphDirections.actionSkiMap())
        }
        return binding.root
    }
}