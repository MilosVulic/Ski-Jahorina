package com.example.skiserbia.features.skicenter.usefulinformation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.skiserbia.R
import com.example.skiserbia.databinding.FragmentUsefulInformationBinding

class UsefulInformationFragment : Fragment() {

    private var bindingProp: FragmentUsefulInformationBinding? = null
    private val binding get() = bindingProp!!

    private val skiCenterUrl: UsefulInformationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentUsefulInformationBinding.inflate(inflater, container, false)
        binding.mountainRescueNumber.text = getMountainRescueNumber(skiCenterUrl.skiCenter)

        binding.phoneIconPolice.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + requireContext().getString(R.string.police_number)))
            startActivity(dialIntent)
        }

        binding.policeNumber.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + requireContext().getString(R.string.police_number)))
            startActivity(dialIntent)
        }

        binding.phoneIconEmergency.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + requireContext().getString(R.string.emergency_number)))
            startActivity(dialIntent)
        }

        binding.emergencyNumber.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + requireContext().getString(R.string.emergency_number)))
            startActivity(dialIntent)
        }

        binding.phoneIconFirefigther.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + requireContext().getString(R.string.firefighter_number)))
            startActivity(dialIntent)
        }

        binding.firefighterNumber.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + requireContext().getString(R.string.firefighter_number)))
            startActivity(dialIntent)
        }

        binding.phoneIconMountainRescue.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getMountainRescueNumber(skiCenterUrl.skiCenter)))
            startActivity(dialIntent)
        }

        binding.mountainRescueNumber.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getMountainRescueNumber(skiCenterUrl.skiCenter)))
            startActivity(dialIntent)
        }
        return binding.root
    }

    private fun getMountainRescueNumber(skiCenterUrl: String): String {
        return if (skiCenterUrl.contains("kopaonik")) {
            requireContext().getString(R.string.mountain_rescue_kopaonik_number)
        } else if (skiCenterUrl.contains("tornik")) {
            requireContext().getString(R.string.mountain_rescue_tornik_number)
        } else {
            requireContext().getString(R.string.mountain_rescue_stara_planina_number)
        }
    }
}