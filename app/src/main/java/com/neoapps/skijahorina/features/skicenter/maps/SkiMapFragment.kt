package com.neoapps.skijahorina.features.skicenter.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.neoapps.skijahorina.R
import com.neoapps.skijahorina.databinding.FragmentSkiMapBinding
import com.neoapps.skijahorina.main.MainActivity

class SkiMapFragment : Fragment() {

    private var bindingProp: FragmentSkiMapBinding? = null
    private val binding get() = bindingProp!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentSkiMapBinding.inflate(inflater, container, false)
        setUpFragmentName()
        setMap()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingProp = null
    }

    private fun setMap() {
        binding.myZoomageView.setImageResource(R.drawable.jahorina_ski_map)
    }

    private fun setUpFragmentName() {
        (activity as MainActivity).supportActionBar?.title = ""
        val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)

        if (title1TextView != null) {
            title1TextView.visibility = View.VISIBLE
            title1TextView.text = resources.getText(R.string.map_lowercase)
        }
    }
}
