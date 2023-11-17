package com.example.skiserbia.features.skicenter.slopes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skiserbia.common.WebScarpingServiceImpl
import com.example.skiserbia.databinding.FragmentSlopeInfoBinding
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SlopeInfoFragment : Fragment() {

    private var bindingProp: FragmentSlopeInfoBinding? = null
    private val binding get() = bindingProp!!
    private val skiCenterUrl: SlopeInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentSlopeInfoBinding.inflate(inflater, container, false)

        binding.slopesRecyclerView.layoutManager = LinearLayoutManager(context)

        val call = WebScarpingServiceImpl.getService(skiCenterUrl.skiCenter).scrapeWebPage(skiCenterUrl.skiCenter)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val htmlContent = responseBody.string()
                        val slopeLiftDetailsList = parseHtmlToSkiSlopeDetails(htmlContent)
                        val listAdapter = SlopeInfoAdapter(slopeLiftDetailsList)
                        binding.slopesRecyclerView.adapter = listAdapter
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })

        return binding.root
    }

    private fun parseHtmlToSkiSlopeDetails(html: String): List<SlopeInfo> {
        val document: Document = Jsoup.parse(html)
        val detailsList = ArrayList<SlopeInfo>()

        val rows = document.select("table.views-table tbody tr")

        for (row in rows) {
            val columns = row.select("td")

            if (columns.size == 5) {
                val name = columns[0].select("strong").text()
                val mark = columns[1].select("span").text()
                val category = columns[2].select("span").text()
                val open = columns[3].text()
                val lastUpdate = columns[4].text()

                val slopeLiftDetails = SlopeInfo(name, mark, open, SlopeCategoryMapper.mapToSlopeCategory(category), lastUpdate)
                Log.d("Nesto ", "item number  " + detailsList.size + " " + slopeLiftDetails.toString())
                detailsList.add(slopeLiftDetails)
            }
        }

        return detailsList
            .filter { it.mark.isNotEmpty() && !it.mark.contains("," ) && !it.mark.contains(".") }
            .distinctBy { it.mark }
    }
}