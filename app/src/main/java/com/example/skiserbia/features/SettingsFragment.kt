package com.example.skiserbia.features

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.skiserbia.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    private var bindingProp: FragmentSettingsBinding? = null
    private val binding get() = bindingProp!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
}