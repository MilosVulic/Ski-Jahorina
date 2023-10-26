package com.example.skiserbia.features

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.skiserbia.R
import com.example.skiserbia.databinding.FragmentSettingsBinding
import com.example.skiserbia.main.MainActivity

class SettingsFragment : Fragment() {

    private var bindingProp: FragmentSettingsBinding? = null
    private val binding get() = bindingProp!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentSettingsBinding.inflate(inflater, container, false)
        setUpFragmentName()
        return binding.root
    }

    private fun setUpFragmentName() {
        (activity as MainActivity).supportActionBar?.title = ""
        val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)
        title1TextView.visibility = View.VISIBLE
        title1TextView.text = resources.getText(R.string.settings)
    }
}