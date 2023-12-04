package com.example.skiserbia.features.skicenter.lifts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skiserbia.databinding.FragmentLiftInfoBinding


class LiftInfoFragment : Fragment() {

    private var bindingProp: FragmentLiftInfoBinding? = null
    private val binding get() = bindingProp!!
    private val lifts: LiftInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentLiftInfoBinding.inflate(inflater, container, false)

        binding.liftsRecyclerView.layoutManager = LinearLayoutManager(context)

        val liftsList = lifts.lifts.split('|').map {
            val values = it.split(',')
            LiftInfo(values[0], values[1], values[2], values[3])
        }

        val listAdapter = LiftInfoAdapter(liftsList)
        binding.liftsRecyclerView.adapter = listAdapter

        return binding.root
    }
}