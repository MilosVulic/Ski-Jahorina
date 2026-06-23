package com.neoapps.skijahorina.features.skicenter.usefulinformation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.neoapps.skijahorina.R
import com.neoapps.skijahorina.common.PreferenceProvider
import com.neoapps.skijahorina.databinding.FragmentUsefulInformationBinding
import com.neoapps.skijahorina.main.MainActivity

class UsefulInformationFragment : Fragment() {

    private var bindingProp: FragmentUsefulInformationBinding? = null
    private val binding get() = bindingProp!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentUsefulInformationBinding.inflate(inflater, container, false)
        setUpFragmentName()
        setHeaderVisibility()

        binding.includedHeader.cardViewWeather.setOnClickListener {
            PreferenceProvider.weatherClicks += 1
            findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionWeatherInfo())

        }

        binding.includedHeader.cardViewLifts.setOnClickListener {
            PreferenceProvider.liftsClicks += 1
            findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionLiftInfo())
        }

        binding.includedHeader.cardViewMapAndCams.setOnClickListener {
            findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionSkiInfo())
        }

        binding.firstNumber.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + requireContext().getString(R.string.mountain_rescue_jahorina_number)))
            startActivity(dialIntent)
        }

        binding.secondNumber.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + requireContext().getString(R.string.police_number)))
            startActivity(dialIntent)
        }

        binding.thirdNumber.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + requireContext().getString(R.string.firefighter_number)))
            startActivity(dialIntent)
        }

        binding.fourthNumber.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + requireContext().getString(R.string.emergency_number)))
            startActivity(dialIntent)
        }
        return binding.root
    }

    private fun setUpFragmentName() {
        (activity as MainActivity).supportActionBar?.title = ""
        val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)
        val toolbar = (activity as MainActivity).findViewById<Toolbar>(R.id.toolbar)

        if (title1TextView != null) {
            title1TextView.visibility = View.VISIBLE
            title1TextView.text = resources.getText(R.string.useful_information_lowercase)
        }

        if (toolbar != null) {
            toolbar.navigationContentDescription = ""
        }
    }

    private fun setHeaderVisibility() {
        binding.includedHeader.imageViewPinDotCircle1.visibility = View.INVISIBLE
        binding.includedHeader.imageViewPinDotCircle2.visibility = View.INVISIBLE
        binding.includedHeader.imageViewPinDotCircle3.visibility = View.INVISIBLE
        binding.includedHeader.imageViewPinDotCircle4.visibility = View.INVISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingProp = null
    }
}