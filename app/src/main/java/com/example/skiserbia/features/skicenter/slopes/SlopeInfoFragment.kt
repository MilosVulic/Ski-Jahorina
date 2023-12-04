package com.example.skiserbia.features.skicenter.slopes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skiserbia.databinding.FragmentSlopeInfoBinding

class SlopeInfoFragment : Fragment() {

    private var bindingProp: FragmentSlopeInfoBinding? = null
    private val binding get() = bindingProp!!
    private val slopes: SlopeInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentSlopeInfoBinding.inflate(inflater, container, false)

        binding.slopesRecyclerView.layoutManager = LinearLayoutManager(context)

        val slopeList = slopes.slopes.split('|').map {
            val values = it.split(',')
            SlopeInfo(values[0], values[1], values[2], SlopeCategory.valueOf(values[3]), values[4])
        }

        val listAdapter = SlopeInfoAdapter(slopeList)
        binding.slopesRecyclerView.adapter = listAdapter

        return binding.root
    }
}