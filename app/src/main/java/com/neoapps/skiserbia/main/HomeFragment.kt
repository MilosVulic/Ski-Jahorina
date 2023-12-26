package com.neoapps.skiserbia.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.neoapps.skiserbia.R
import com.neoapps.skiserbia.common.PreferenceProvider
import com.neoapps.skiserbia.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        hideTitle()

        binding.cardViewKopaonik.setOnClickListener {
            findNavController().navigate(com.neoapps.skiserbia.NavigationGraphDirections.actionSkiInfo(PreferenceProvider.kopaonikUrl))
        }

        binding.cardViewTornik.setOnClickListener {
            findNavController().navigate(com.neoapps.skiserbia.NavigationGraphDirections.actionSkiInfo(PreferenceProvider.zlatiborUrl))
        }

        binding.cardViewStaraPlanina.setOnClickListener {
            findNavController().navigate(com.neoapps.skiserbia.NavigationGraphDirections.actionSkiInfo(PreferenceProvider.staraPlaninaUrl))
        }
        return binding.root
    }

    private fun hideTitle() {
        val ac = activity as MainActivity
        ac.supportActionBar?.title = ""
        val toolbar = ac.findViewById<Toolbar>(R.id.toolbar)

        if (null != toolbar) {
            val title1TextView = ac.findViewById<TextView>(R.id.title1)
            title1TextView.visibility = View.VISIBLE
            title1TextView.text = resources.getText(R.string.ski_resorts)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}