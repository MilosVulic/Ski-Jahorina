package com.neoapps.skijahorina.features.skicenter.properties

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.neoapps.skijahorina.R
import com.neoapps.skijahorina.databinding.FragmentFullScreenPropertyImageBinding
import com.neoapps.skijahorina.main.MainActivity

class FullScreenPropertyImageFragment : Fragment() {

    private var _binding: FragmentFullScreenPropertyImageBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageUrls: List<String>
    private var initialPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val imageUrlString = it.getString("imageUrls") ?: ""
            imageUrls = imageUrlString.split(",").map { it.trim() }
            initialPosition = it.getInt("initialPosition", 0)
        }
        Log.d("FullScreenImageFragment", "Received imageUrls: $imageUrls, initialPosition: $initialPosition")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFullScreenPropertyImageBinding.inflate(inflater, container, false)
        setUpFragmentName()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ImagePagerAdapter(imageUrls)
        binding.viewPager.adapter = adapter
        binding.viewPager.setCurrentItem(initialPosition, false)
    }

    private fun setUpFragmentName() {
        (activity as MainActivity).supportActionBar?.title = ""
        val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)

        if (title1TextView != null) {
            title1TextView.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        binding.viewPager.adapter = null
        _binding = null
        super.onDestroyView()
    }
}