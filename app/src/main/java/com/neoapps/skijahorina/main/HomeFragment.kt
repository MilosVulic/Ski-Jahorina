package com.neoapps.skijahorina.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.neoapps.skijahorina.R
import com.neoapps.skijahorina.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        hideTitle()

        binding.cardViewSlopesInfo.setOnClickListener {
            checkForAppUpdateAndNavigate()
        }

        binding.cardViewLiftInfo.setOnClickListener {
            checkForAppUpdateAndNavigate()
        }

        binding.cardViewForecastInfo.setOnClickListener {
            checkForAppUpdateAndNavigate()
        }

        binding.cardViewMap.setOnClickListener {
            checkForAppUpdateAndNavigate()
        }

        binding.cardViewUsefulInformation.setOnClickListener {
            checkForAppUpdateAndNavigate()
        }

        binding.cardViewSomethingInfo.setOnClickListener {
            checkForAppUpdateAndNavigate()
        }
        return binding.root
    }

    private fun hideTitle() {
        val ac = activity as MainActivity
        ac.supportActionBar?.title = ""
        val toolbar = ac.findViewById<Toolbar>(R.id.toolbar)

        if (null != toolbar) {
            val title1TextView = ac.findViewById<TextView>(R.id.title1)
            title1TextView.visibility = View.GONE
        }
    }

    private fun checkForAppUpdateAndNavigate() {
        val ac = activity as MainActivity
        val appUpdateInfoTask = ac.appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                showUpdateSnackbar()
            } else {
                try {
                    findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionSkiInfo())
                } catch (_: Exception) {
                    findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionSkiInfo())
                }
            }
        }

        appUpdateInfoTask.addOnFailureListener {
            findNavController().navigate(com.neoapps.skijahorina.NavigationGraphDirections.actionSkiInfo())
        }
    }

    private fun showUpdateSnackbar() {
        val ac = activity as MainActivity
        val snackbar = Snackbar.make(requireView(), resources.getString(R.string.update_text), Snackbar.LENGTH_LONG)
        val snackbarLayout: View = snackbar.view
        snackbarLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorSnackBarBackground))

        val textView = snackbarLayout.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_about_snackbar, 0, 0, 0)
        textView.compoundDrawablePadding = resources.getDimensionPixelOffset(R.dimen.snackbar_warning)

        snackbar.setAction(R.string.update) { ac.checkForUpdates() }
        snackbar.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.colorFocusedField))

        snackbar.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}