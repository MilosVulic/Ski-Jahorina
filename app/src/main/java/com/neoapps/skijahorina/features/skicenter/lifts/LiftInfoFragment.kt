package com.neoapps.skijahorina.features.skicenter.lifts

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.neoapps.skijahorina.R
import com.neoapps.skijahorina.common.PreferenceProvider
import com.neoapps.skijahorina.common.Utils
import com.neoapps.skijahorina.databinding.FragmentLiftInfoBinding
import com.neoapps.skijahorina.databinding.LayoutUnableToFetchDataBinding
import com.neoapps.skijahorina.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.time.LocalDateTime

class LiftInfoFragment : Fragment() {

    private var bindingProp: FragmentLiftInfoBinding? = null
    private var bindingPropEmptyState: LayoutUnableToFetchDataBinding? = null
    private val binding get() = bindingProp!!
    private val bindingEmptyState get() = bindingPropEmptyState!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
            bindingProp = FragmentLiftInfoBinding.inflate(inflater, container, false)
            bindingPropEmptyState = bindingProp!!.includeWeatherUnavailable
            setUpFragmentName()
            setHeaderVisibility()
            binding.liftsRecyclerView.layoutManager = LinearLayoutManager(context)

            binding.includedHeader.cardViewWeather.setOnClickListener {
                PreferenceProvider.weatherClicks += 1
                findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionWeatherInfo())
            }

            binding.includedHeader.cardViewUsefulInfo.setOnClickListener {
                findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionPropertyDetails())
            }

            binding.includedHeader.cardViewMapAndCams.setOnClickListener {
                findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionSkiInfo())
            }

            val searchView = activity?.findViewById<Toolbar>(R.id.toolbar) ?.findViewById<SearchView>(R.id.searchView2)
            if (searchView != null) {
                searchView.visibility = View.VISIBLE

                val searchTextView = searchView.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
                searchTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorWhite))
                val searchPlate = searchView.findViewById<View>(androidx.appcompat.R.id.search_plate)
                val queryHintTextView = searchPlate.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
                queryHintTextView.setTextCursorDrawable(R.drawable.white_cursor)
                queryHintTextView.setHintTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorWhite
                    )
                )
                searchView.queryHint = "Search lift"
            }

             getLiftData { liftsList ->
                if (liftsList.isNotEmpty()) {
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

                if (liftsList.isEmpty()) {
                    bindingEmptyState.noInternet.visibility = View.VISIBLE
                    binding.liftsRecyclerView.visibility = View.GONE
                } else {
                    bindingEmptyState.noInternet.visibility = View.GONE
                    binding.liftsRecyclerView.visibility = View.VISIBLE
                }
            }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.liftsRecyclerView.adapter = null
        binding.liftsRecyclerView.layoutManager = null
        val searchView = activity?.findViewById<Toolbar>(R.id.toolbar) ?.findViewById<SearchView>(R.id.searchView2)
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
        val toolbar = (activity as MainActivity).findViewById<Toolbar>(R.id.toolbar)

        if (title1TextView != null) {
            /*title1TextView.visibility = View.VISIBLE
            title1TextView.text = resources.getText(R.string.lifts_lowercase)*/

            title1TextView.visibility = View.GONE
        }

        if (toolbar != null) {
            toolbar.navigationContentDescription = ""
        }
    }

    private fun setHeaderVisibility() {
        binding.includedHeader.imageViewPinDotCircle1.visibility = View.INVISIBLE
        binding.includedHeader.imageViewPinDotCircle2.visibility = View.VISIBLE
        binding.includedHeader.imageViewPinDotCircle3.visibility = View.INVISIBLE
        binding.includedHeader.imageViewPinDotCircle4.visibility = View.INVISIBLE
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

    private fun getLiftData(callback: (List<LiftInfo>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            if (Utils.isTimeDifferenceGreaterThanProvidedMinutes(
                    LocalDateTime.parse(PreferenceProvider.lastLiftInfoJahorinaFetchTime), LocalDateTime.now(), 10)
            ) {
                try {
                    val parsedLifts = parseLiftData()
                    setParsedLiftsToPreference(parsedLifts)
                    withContext(Dispatchers.Main) {
                        callback(parsedLifts)
                    }
                } catch (e: Exception) {
                    if (Utils.isTimeDifferenceGreaterThanProvidedMinutes(
                            LocalDateTime.parse(PreferenceProvider.lastLiftInfoJahorinaFetchTime), LocalDateTime.now(), 1440)
                    ) {
                        bindingEmptyState.noInternet.visibility = View.VISIBLE
                        binding.includedHeader.appNavHeader.visibility = View.GONE
                        Log.e("FetchLiftData", "Error fetching lift data: ${e.localizedMessage}", e)
                    } else {
                        withContext(Dispatchers.Main) {
                            callback(setStringLiftsToListLifts())
                        }
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    callback(setStringLiftsToListLifts())
                }
            }
        }
    }

    private fun parseLiftData(): List<LiftInfo> {
        val doc = Jsoup.connect("https://peakvisor.com/ski/bosnia-and-herzegovina/republika-srpska/jahorina/live").get()
        return doc.select("div.lift").map { lift ->
            val statusIconUrl = lift.select("div.lift-details-info:has(div.lift-details-info__header:contains(Status)) div.lift-details-info__content").text()
            val liftType = lift.select("div.lift-type use").attr("xlink:href").split("#").last()
            val liftName = lift.select("div.lift-name span").text()
            val liftWorkingHours = lift.select("div.lift-opening-hours span").text()
            LiftInfo(liftName, liftType, statusIconUrl, liftWorkingHours)
        }
    }

    private fun setParsedLiftsToPreference(lifts: List<LiftInfo>) {
        val liftsAsString = lifts.joinToString("|") { "${it.name},${it.type},${it.inFunction},${it.openingHours}" }
        PreferenceProvider.liftsJahorina = liftsAsString
        PreferenceProvider.lastLiftInfoJahorinaFetchTime = LocalDateTime.now().toString()
    }

    private fun setStringLiftsToListLifts() : List<LiftInfo> {
        return PreferenceProvider.liftsJahorina.split('|').map {
            val values = it.split(',')
            LiftInfo(values[0], values[1], values[2], values[3])
        }
    }
}


