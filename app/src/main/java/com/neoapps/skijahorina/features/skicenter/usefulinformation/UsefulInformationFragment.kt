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
import com.neoapps.skijahorina.R
import com.neoapps.skijahorina.common.AppAnalytics
import com.neoapps.skijahorina.databinding.FragmentUsefulInformationBinding
import com.neoapps.skijahorina.main.MainActivity

class UsefulInformationFragment : Fragment() {

    private var bindingProp: FragmentUsefulInformationBinding? = null
    private val binding get() = bindingProp!!
    private var aboutExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentUsefulInformationBinding.inflate(inflater, container, false)
        setUpFragmentName()
        AppAnalytics.logFeatureOpened(AppAnalytics.Feature.USEFUL_INFO)

        binding.firstNumber.setOnClickListener {
            AppAnalytics.logAction("dial", "mountain_rescue")
            dial(getString(R.string.mountain_rescue_jahorina_number))
        }
        binding.secondNumber.setOnClickListener {
            AppAnalytics.logAction("dial", "police")
            dial(getString(R.string.police_number))
        }
        binding.thirdNumber.setOnClickListener {
            AppAnalytics.logAction("dial", "firefighter")
            dial(getString(R.string.firefighter_number))
        }
        binding.fourthNumber.setOnClickListener {
            AppAnalytics.logAction("dial", "emergency")
            dial(getString(R.string.emergency_number))
        }
        binding.moreDetailsToggle.setOnClickListener { toggleAboutSection() }
        updateAboutSection()
        return binding.root
    }

    private fun toggleAboutSection() {
        aboutExpanded = !aboutExpanded
        updateAboutSection()
    }

    private fun updateAboutSection() {
        binding.moreDetailsText.maxLines = if (aboutExpanded) Int.MAX_VALUE else ABOUT_COLLAPSED_LINES
        binding.moreDetailsToggle.text = getString(
            if (aboutExpanded) R.string.read_less else R.string.read_more
        )
    }

    private fun dial(number: String) {
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))
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
            toolbar.navigationContentDescription = getString(R.string.cd_navigate_back)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingProp = null
    }

    companion object {
        private const val ABOUT_COLLAPSED_LINES = 4
    }
}
