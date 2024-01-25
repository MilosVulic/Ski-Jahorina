package com.neoapps.skiserbia.features.skicenter.lifts

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
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

        val searchView = activity?.findViewById<Toolbar>(R.id.toolbar)?.findViewById<SearchView>(R.id.searchView2)
        if (searchView != null) {
            searchView.visibility = View.VISIBLE

            val searchTextView = searchView.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
            searchTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorWhite))
            val searchPlate = searchView.findViewById<View>(androidx.appcompat.R.id.search_plate)
            val queryHintTextView = searchPlate.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
            queryHintTextView.setTextCursorDrawable(R.drawable.white_cursor)
            queryHintTextView.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.colorWhite))
            searchView.queryHint = "Search lift"
        }

        if (lifts.lifts.isNotEmpty()) {
            val liftsList = lifts.lifts.split('|').map {
                val values = it.split(',')
                LiftInfo(values[0], values[1], values[2], values[3], values[4])
            }

            val listAdapter = LiftInfoAdapter(liftsList)
            binding.liftsRecyclerView.adapter = listAdapter

            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
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
        binding.liftsRecyclerView.adapter = null
        binding.liftsRecyclerView.layoutManager = null
        val searchView = activity?.findViewById<Toolbar>(R.id.toolbar)?.findViewById<SearchView>(R.id.searchView2)
        searchView?.setQuery("", false)
        searchView?.setOnQueryTextListener(null)
        bindingProp = null
        bindingPropEmptyState = null
    }

    // Hide keyboard if its opened
    override fun onStop() {
        super.onStop()
        hideKeyboard()

        val searchView = activity?.findViewById<Toolbar>(R.id.toolbar)?.findViewById<SearchView>(R.id.searchView2)
        if (searchView != null) {
            searchView.visibility = View.GONE
        }
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

    // Extension function for closing the keyboard inside the fragment
    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // Calling method of extension function for closing the keyboard
    private fun Fragment.hideKeyboard() {
        view?.let { context?.hideKeyboard(it) }
    }
}