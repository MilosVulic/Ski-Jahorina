package com.example.skiserbia.features.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.skiserbia.databinding.FragmentKopaonikMapBinding

class KopaonikMapFragment : Fragment() {

    private var bindingProp: FragmentKopaonikMapBinding? = null
    private val binding get() = bindingProp!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentKopaonikMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingProp = null
    }
}
