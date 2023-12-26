package com.neoapps.skiserbia.features.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.neoapps.skiserbia.R
import com.neoapps.skiserbia.databinding.FragmentAboutBinding
import com.neoapps.skiserbia.main.MainActivity


class AboutFragment : Fragment() {

    private var bindingProp: FragmentAboutBinding? = null
    private val binding get() = bindingProp!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentAboutBinding.inflate(inflater, container, false)

        val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)
        title1TextView.visibility = View.VISIBLE
        title1TextView.text = resources.getString(R.string.about)

        settingAnimationSocial()

        binding.imageViewEmailSocial.setOnClickListener {
            val recepientEmail = resources.getString(R.string.about_email)
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
                data = Uri.parse("mailto:$recepientEmail")
            }
            startActivity(emailIntent)
        }

        binding.imageViewYoutubeAbout.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://ski-serbia.carrd.co/")
            )
            startActivity(browserIntent)
        }

        binding.imageViewTwitterAbout.setOnClickListener {
            val appLink = "twitter://user?screen_name=NeoappsServices"
            val packageName = "com.twitter.android"
            val webLink = "https://twitter.com/NeoappsServices"
            openLink(appLink, packageName, webLink)
        }

        binding.imageViewInstagramAbout.setOnClickListener {
            val packageName = "com.instagram.android"
            val webLink = "https://www.instagram.com/neoapps_services/"
            openLink(webLink, packageName, webLink)
        }

        return binding.root
    }

    private fun settingAnimationSocial() {
        val animation1: Animation =
            TranslateAnimation(Resources.getSystem().displayMetrics.widthPixels.toFloat(), 0.0f, 0.0f, 0.0f)
        val animation2: Animation =
            TranslateAnimation(Resources.getSystem().displayMetrics.widthPixels.toFloat() + 100.0f, 0.0f, 0.0f, 0.0f)
        val animation3: Animation =
            TranslateAnimation(Resources.getSystem().displayMetrics.widthPixels.toFloat() + 200.0f, 0.0f, 0.0f, 0.0f)
        val animation4: Animation =
            TranslateAnimation(Resources.getSystem().displayMetrics.widthPixels.toFloat() + 300.0f, 0.0f, 0.0f, 0.0f)
        animation1.duration = 900
        animation2.duration = 1000
        animation3.duration = 1100
        animation4.duration = 1200
        binding.imageViewTwitterAbout.startAnimation(animation1)
        binding.imageViewInstagramAbout.startAnimation(animation2)
        binding.imageViewYoutubeAbout.startAnimation(animation3)
        binding.imageViewEmailSocial.startAnimation(animation4)
    }

    private fun openLink(appLink: String, packageName: String, webLink: String) {
        try {
            val uri = Uri.parse(appLink)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = uri
            intent.`package` = packageName
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } catch (ae: ActivityNotFoundException) {
            val uri = Uri.parse(webLink)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = uri
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingProp = null
    }
}