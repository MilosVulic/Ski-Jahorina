package com.neoapps.skiserbia.features.skicenter.slopes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.neoapps.skiserbia.R
import com.neoapps.skiserbia.databinding.FragmentSlopeInfoBinding
import com.neoapps.skiserbia.databinding.IncludeEmptyListPlaceholderBinding
import com.neoapps.skiserbia.main.MainActivity

class SlopeInfoFragment : Fragment() {

    private var bindingProp: FragmentSlopeInfoBinding? = null
    private val binding get() = bindingProp!!


    private var bindingPropEmptyState: IncludeEmptyListPlaceholderBinding? = null
    private val bindingEmptyState get() = bindingPropEmptyState!!
    private val slopes: SlopeInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentSlopeInfoBinding.inflate(inflater, container, false)
        bindingPropEmptyState = bindingProp!!.includeEmptylistPlaceholder
        setUpFragmentName()
        binding.slopesRecyclerView.layoutManager = LinearLayoutManager(context)

        if (slopes.slopes.isNotEmpty()) {
            val slopeList = slopes.slopes.split('|').map {
                val values = it.split(',')
                SlopeInfo(values[0], values[1], values[2], SlopeCategory.valueOf(values[3]), values[4])
            }
            val mutableSlopeList: MutableList<Any> = slopeList.toMutableList()
            val listAdapter = SlopeInfoAdapter(mutableSlopeList)
            binding.slopesRecyclerView.adapter = listAdapter
            loadNativeAds(listAdapter, mutableSlopeList)
        }

        if (slopes.slopes.isEmpty()) {
            bindingEmptyState.emptyListState.visibility = View.VISIBLE
            binding.slopesRecyclerView.visibility = View.GONE
        } else {
            bindingEmptyState.emptyListState.visibility = View.GONE
            binding.slopesRecyclerView.visibility = View.VISIBLE
        }

        return binding.root
    }

    private fun loadNativeAds(adapter: SlopeInfoAdapter, mutableSlopeList: MutableList<Any>) {
        Log.d("SlopeInfoFragment", "loadNativeAds called")

        if (!adapter.adLoaded) {
            val adLoader = AdLoader.Builder(requireContext(), "ca-app-pub-3940256099942544/2247696110")
                .forNativeAd { nativeAd ->
                    mutableSlopeList.add(nativeAd)

                    // Add native ad only at the third position
                    adapter.addNativeAd(nativeAd)

                    Log.d("SlopeInfoFragment", "Added native ad to list. Ad Title: ${nativeAd.headline}")
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.e("SlopeInfoFragment", "Ad failed to load: ${loadAdError.message}")
                        // Handle ad loading failure
                    }
                })
                .build()

            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    private fun setUpFragmentName() {
        (activity as MainActivity).supportActionBar?.title = ""
        val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)

        if (title1TextView != null) {
            title1TextView.visibility = View.VISIBLE
            title1TextView.text = resources.getText(R.string.slopes_lowercase)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingProp = null
        bindingPropEmptyState = null
    }
}