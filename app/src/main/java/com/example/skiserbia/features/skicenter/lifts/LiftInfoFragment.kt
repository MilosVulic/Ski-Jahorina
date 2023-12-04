package com.example.skiserbia.features.skicenter.lifts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skiserbia.common.WebScarpingServiceImpl
import com.example.skiserbia.databinding.FragmentLiftInfoBinding
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LiftInfoFragment : Fragment() {

    private var bindingProp: FragmentLiftInfoBinding? = null
    private val binding get() = bindingProp!!

    private val skiCenterUrl: LiftInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentLiftInfoBinding.inflate(inflater, container, false)

        binding.liftsRecyclerView.layoutManager = LinearLayoutManager(context)

        val call = WebScarpingServiceImpl.getService(skiCenterUrl.skiCenter).scrapeWebPage(skiCenterUrl.skiCenter)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val htmlContent = responseBody.string()
                        val skiLiftDetailsList = parseHtmlToSkiLiftDetails(htmlContent)
                        val listAdapter = LiftInfoAdapter(skiLiftDetailsList)
                        binding.liftsRecyclerView.adapter = listAdapter
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })

        return binding.root
    }

    private fun parseHtmlToSkiLiftDetails(html: String): List<LiftInfo> {
        val document: Document = Jsoup.parse(html)
        val detailsList = ArrayList<LiftInfo>()

        val table: Element? = document.select("table.views-table").first()

        if (table != null) {
            val rows: List<Element> = table.select("tr")

            for (row in rows) {
                val columns: List<Element> = row.select("td")

                if (columns.size == 6) {
                    val name = columns[0].text()
                    val type = columns[1].text()
                    val inFunction = columns[4].text()
                    val lastChange = columns[5].text()

                    val skiLiftDetails = LiftInfo(name, type, inFunction, lastChange)
                    detailsList.add(skiLiftDetails)
                }
            }
        }

        return detailsList
            .filter { it.type.isNotEmpty() }
    }
}