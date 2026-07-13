package com.neoapps.skijahorina.features.skicenter.lifts

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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.neoapps.skijahorina.R
import com.neoapps.skijahorina.common.AppAnalytics
import com.neoapps.skijahorina.common.CacheTimestampFormatter
import com.neoapps.skijahorina.common.FetchEmptyState
import com.neoapps.skijahorina.common.LiftStatus
import com.neoapps.skijahorina.common.PreferenceProvider
import com.neoapps.skijahorina.common.openCount
import com.neoapps.skijahorina.databinding.FragmentLiftInfoBinding
import com.neoapps.skijahorina.databinding.LayoutUnableToFetchDataBinding
import com.neoapps.skijahorina.features.skicenter.JahorinaDataFetcher
import com.neoapps.skijahorina.main.MainActivity
import kotlinx.coroutines.launch

class LiftInfoFragment : Fragment() {

    private var bindingProp: FragmentLiftInfoBinding? = null
    private var bindingPropEmptyState: LayoutUnableToFetchDataBinding? = null
    private val binding get() = bindingProp!!
    private val bindingEmptyState get() = bindingPropEmptyState!!

    private var listAdapter: LiftInfoAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentLiftInfoBinding.inflate(inflater, container, false)
        bindingPropEmptyState = bindingProp?.includeUnableToFetchData
        setUpFragmentName()
        AppAnalytics.logFeatureOpened(AppAnalytics.Feature.LIFTS)
        binding.liftRecyclerView.layoutManager = LinearLayoutManager(context)

        setupFilterChips()
        setupSearch()

        listAdapter = LiftInfoAdapter().also { adapter ->
            binding.liftRecyclerView.adapter = adapter
        }

        loadLifts()

        return binding.root
    }

    private fun setupSearch() {
        val searchView = activity?.findViewById<Toolbar>(R.id.toolbar)?.findViewById<SearchView>(R.id.searchView2)
            ?: return
        searchView.visibility = View.VISIBLE

        val searchTextView = searchView.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
        searchTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorWhite))
        val searchPlate = searchView.findViewById<View>(androidx.appcompat.R.id.search_plate)
        val queryHintTextView = searchPlate.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
        queryHintTextView.setTextCursorDrawable(R.drawable.white_cursor)
        queryHintTextView.setHintTextColor(
            ContextCompat.getColor(requireContext(), R.color.colorWhite)
        )
        searchView.queryHint = getString(R.string.search_lift_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                listAdapter?.setQuery(newText)
                return true
            }
        })
    }

    private fun setupFilterChips() {
        binding.liftFilterChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val status = when (checkedIds.firstOrNull()) {
                R.id.chipFilterOpen -> LiftStatus.OPEN
                R.id.chipFilterClosed -> LiftStatus.CLOSED
                R.id.chipFilterOnHold -> LiftStatus.ON_HOLD
                else -> null
            }
            listAdapter?.setStatusFilter(status)
        }
    }

    private fun loadLifts() {
        viewLifecycleOwner.lifecycleScope.launch {
            val liftsList = JahorinaDataFetcher.refreshLifts()
            bindLifts(liftsList)
        }
    }

    private fun bindLifts(liftsList: List<LiftInfo>) {
        if (bindingProp == null) return

        bindLastUpdated()
        bindSummary(liftsList)

        if (liftsList.isNotEmpty()) {
            FetchEmptyState.hide(bindingEmptyState)
            binding.liftRecyclerView.visibility = View.VISIBLE
            binding.liftsHeaderCard.visibility = View.VISIBLE
            listAdapter?.submitSource(liftsList)
        } else {
            binding.liftRecyclerView.visibility = View.GONE
            binding.liftsHeaderCard.visibility = View.GONE
            FetchEmptyState.bind(
                bindingEmptyState,
                FetchEmptyState.resolve(requireContext(), hasCachedData = false),
                onRetry = { loadLifts() }
            )
        }
    }

    private fun bindSummary(liftsList: List<LiftInfo>) {
        if (liftsList.isEmpty()) {
            binding.listSummary.visibility = View.GONE
            return
        }
        binding.listSummary.visibility = View.VISIBLE
        binding.listSummary.text = getString(
            R.string.list_summary_lifts,
            liftsList.openCount(),
            liftsList.size
        )
    }

    private fun bindLastUpdated() {
        val formatted = CacheTimestampFormatter.bestTimestamp(
            PreferenceProvider.resortApiUpdatedAt,
            PreferenceProvider.lastLiftInfoJahorinaFetchTime
        )
        if (formatted != null) {
            binding.lastUpdatedText.visibility = View.VISIBLE
            binding.lastUpdatedText.text = getString(R.string.last_updated, formatted)
        } else {
            binding.lastUpdatedText.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.liftRecyclerView.adapter = null
        binding.liftRecyclerView.layoutManager = null
        listAdapter = null
        val searchView = activity?.findViewById<Toolbar>(R.id.toolbar)?.findViewById<SearchView>(R.id.searchView2)
        searchView?.setQuery("", false)
        searchView?.setOnQueryTextListener(null)
        bindingProp = null
        bindingPropEmptyState = null
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
        val searchView = activity?.findViewById<Toolbar>(R.id.toolbar)?.findViewById<SearchView>(R.id.searchView2)
        searchView?.visibility = View.GONE
    }

    private fun setUpFragmentName() {
        (activity as MainActivity).supportActionBar?.title = ""
        val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)
        val toolbar = (activity as MainActivity).findViewById<Toolbar>(R.id.toolbar)

        if (title1TextView != null) {
            title1TextView.visibility = View.GONE
        }

        if (toolbar != null) {
            toolbar.navigationContentDescription = getString(R.string.cd_navigate_back)
        }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun Fragment.hideKeyboard() {
        view?.let { context?.hideKeyboard(it) }
    }
}
