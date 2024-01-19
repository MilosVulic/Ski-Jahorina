package com.neoapps.skiserbia.features.skicenter.lifts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.neoapps.skiserbia.R
import com.neoapps.skiserbia.databinding.FragmentLiftInfoBinding
import com.neoapps.skiserbia.databinding.IncludeEmptyListPlaceholderBinding
import com.neoapps.skiserbia.main.MainActivity


class LiftInfoFragment : Fragment() {

    private var bindingProp: FragmentLiftInfoBinding? = null
    private var bindingPropEmptyState: IncludeEmptyListPlaceholderBinding? = null
    private val binding get() = bindingProp!!
    private val bindingEmptyState get() = bindingPropEmptyState!!
    private val lifts: LiftInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentLiftInfoBinding.inflate(inflater, container, false)
        bindingPropEmptyState = bindingProp!!.includeEmptylistPlaceholder
        setUpFragmentName()
        binding.liftsRecyclerView.layoutManager = LinearLayoutManager(context)

        binding.searchView.apply {
            val searchTextView = this.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
            searchTextView.setTextColor(resources.getColor(R.color.colorWhite))

            val searchPlate = this.findViewById<View>(androidx.appcompat.R.id.search_plate)
            val queryHintTextView = searchPlate.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
            queryHintTextView.setHintTextColor(resources.getColor(R.color.colorWhite))
            queryHint = "Search by name"
        }

        if (lifts.lifts.isNotEmpty()) {
            val liftsList = lifts.lifts.split('|').map {
                val values = it.split(',')
                LiftInfo(values[0], values[1], values[2], values[3], values[4])
            }

            val listAdapter = LiftInfoAdapter(liftsList)
            binding.liftsRecyclerView.adapter = listAdapter

            binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    // Handle search submission if needed
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // Filter the list based on the entered text
                    listAdapter.filter.filter(newText)
                    return true
                }
            })
        }

        if (lifts.lifts.isEmpty()) {
            bindingEmptyState.emptyListState.visibility = View.VISIBLE
            binding.liftsRecyclerView.visibility = View.GONE
        } else {
            bindingEmptyState.emptyListState.visibility = View.GONE
            binding.liftsRecyclerView.visibility = View.VISIBLE
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingProp = null
        bindingPropEmptyState = null
    }

    private fun setUpFragmentName() {
        (activity as MainActivity).supportActionBar?.title = ""
        val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)

        if (title1TextView != null) {
            /*title1TextView.visibility = View.VISIBLE
            title1TextView.text = resources.getText(R.string.lifts_lowercase)*/

            title1TextView.visibility = View.GONE
        }
    }
}