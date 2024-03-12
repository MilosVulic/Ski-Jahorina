package com.neoapps.skiserbia.features.settings

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.neoapps.skiserbia.NavigationGraphDirections
import com.neoapps.skiserbia.R
import com.neoapps.skiserbia.common.PreferenceProvider
import com.neoapps.skiserbia.databinding.FragmentSettingsBinding
import com.neoapps.skiserbia.main.MainActivity
import de.hdodenhof.circleimageview.CircleImageView
import dev.b3nedikt.app_locale.AppLocale
import dev.b3nedikt.reword.Reword
import java.util.Locale

class SettingsFragment : Fragment() {

    private var bindingProp: FragmentSettingsBinding? = null
    private val binding get() = bindingProp!!
    private var clickedLanguageChange = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentSettingsBinding.inflate(inflater, container, false)
        setUpFragmentName()
        themeSettings()

        // changing application language
        binding.changeApplicationLanguage.setOnClickListener {
            val dialogLanguage = createLanguageDialog()!!
            clickedLanguageChange = false
            AppLocale.desiredLocale = Locale(PreferenceProvider.language)
            Reword.reword(dialogLanguage.findViewById(R.id.languageDialogConstraint))
            dialogLanguage.show()
        }

        // changing dark/light mode
        binding.switchDarkLightTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                PreferenceProvider.darkMode = true
                binding.imageViewTheme.setImageResource(R.drawable.ic_dark_mode)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                PreferenceProvider.darkMode = false
                binding.imageViewTheme.setImageResource(R.drawable.ic_light_mode)
            }
        }

        binding.about.setOnClickListener {
            findNavController().navigate(NavigationGraphDirections.actionAbout())
        }

        binding.helpCenter.setOnClickListener {
            findNavController().navigate(NavigationGraphDirections.actionHelpCenter())
        }

        // Opening therms of service
        binding.termsAndConditions.setOnClickListener {
            val uri: Uri = Uri.parse("https://doc-hosting.flycricket.io/ski-serbia/0ac3ee8e-bcd8-4e7e-91b2-ffc9e9674037/terms")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        // Opening rate application
        binding.rate.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + requireContext().packageName)
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + requireContext().packageName)
                    )
                )
            }
        }

        return binding.root
    }

    private fun setUpFragmentName() {
        (activity as MainActivity).supportActionBar?.title = ""
        val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)

        if (title1TextView != null) {
            title1TextView.visibility = View.VISIBLE
            title1TextView.text = resources.getText(R.string.settings)
        }
    }

    private fun createLanguageDialog(): Dialog? {
        val dialog = context?.let { Dialog(it) }
        dialog?.setCancelable(true)
        dialog?.requestWindowFeature(Window.FEATURE_CONTEXT_MENU)
        dialog?.setContentView(R.layout.dialog_language_choose)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val radioGroupEnglish = dialog?.findViewById<RadioGroup>(R.id.radioEnglish)
        val radioGroupSerbian = dialog?.findViewById<RadioGroup>(R.id.radioSerbian)
        val radioGroupRussian = dialog?.findViewById<RadioGroup>(R.id.radioRussian)
        val radioGroupGerman = dialog?.findViewById<RadioGroup>(R.id.radioGerman)

        val radioButtonEnglish = dialog?.findViewById<RadioButton>(R.id.radioButtonEnglish)
        val radioButtonSerbian = dialog?.findViewById<RadioButton>(R.id.radioButtonSerbian)
        val radioButtonRussian = dialog?.findViewById<RadioButton>(R.id.radioButtonRussian)
        val radioButtonGerman = dialog?.findViewById<RadioButton>(R.id.radioButtonGerman)

        val imageViewCircleCloseDialog =
            dialog?.findViewById<CircleImageView>(R.id.imageViewCircleCloseDialog)


        when (PreferenceProvider.language) {
            "en" -> radioGroupEnglish?.check(R.id.radioButtonEnglish)
            "sr" -> radioGroupSerbian?.check(R.id.radioButtonSerbian)
            "ru" -> radioGroupRussian?.check(R.id.radioButtonRussian)
            "de" -> radioGroupGerman?.check(R.id.radioButtonGerman)
        }


        radioGroupEnglish?.setOnCheckedChangeListener { _, _ ->
            PreferenceProvider.language = "en"

            when {
                radioButtonSerbian?.isChecked!! -> {
                    radioGroupSerbian?.clearCheck()
                    radioGroupEnglish.check(R.id.radioButtonEnglish)
                    clickedLanguageChange = true
                }
                radioButtonRussian?.isChecked!! -> {
                    radioGroupRussian?.clearCheck()
                    radioGroupEnglish.check(R.id.radioButtonEnglish)
                    clickedLanguageChange = true
                }
                radioButtonGerman?.isChecked!! -> {
                    radioGroupGerman?.clearCheck()
                    radioGroupEnglish.check(R.id.radioButtonEnglish)
                    clickedLanguageChange = true
                }
            }

            if (!clickedLanguageChange) {
                AppLocale.desiredLocale = Locale(PreferenceProvider.language)
                Reword.reword(binding.root)
                (activity as MainActivity).supportActionBar?.title = ""

                val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)
                title1TextView.visibility = View.VISIBLE
                title1TextView.text  = resources.getText(R.string.settings)
                dialog.dismiss()
            }
        }


        radioGroupSerbian?.setOnCheckedChangeListener { _, _ ->
            PreferenceProvider.language = "sr"

            when {
                radioButtonEnglish?.isChecked!! -> {
                    radioGroupEnglish?.clearCheck()
                    radioGroupSerbian.check(R.id.radioButtonSerbian)
                    clickedLanguageChange = true
                }
                radioButtonRussian?.isChecked!! -> {
                    radioGroupRussian?.clearCheck()
                    radioGroupSerbian.check(R.id.radioButtonSerbian)
                    clickedLanguageChange = true
                }
                radioButtonGerman?.isChecked!! -> {
                    radioGroupGerman?.clearCheck()
                    radioGroupSerbian.check(R.id.radioButtonSerbian)
                    clickedLanguageChange = true
                }
            }

            if (!clickedLanguageChange) {
                AppLocale.desiredLocale = Locale(PreferenceProvider.language)
                Reword.reword(binding.root)
                (activity as MainActivity).supportActionBar?.title = ""

                val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)
                title1TextView.visibility = View.VISIBLE
                title1TextView.text  = resources.getText(R.string.settings)
                dialog.dismiss()
            }
        }


        radioGroupRussian?.setOnCheckedChangeListener { _, _ ->
            PreferenceProvider.language = "ru"

            when {
                radioButtonEnglish?.isChecked!! -> {
                    radioGroupEnglish?.clearCheck()
                    radioGroupRussian.check(R.id.radioButtonRussian)
                    clickedLanguageChange = true
                }
                radioButtonSerbian?.isChecked!! -> {
                    radioGroupSerbian?.clearCheck()
                    radioGroupRussian.check(R.id.radioButtonRussian)
                    clickedLanguageChange = true
                }
                radioButtonGerman?.isChecked!! -> {
                    radioGroupGerman?.clearCheck()
                    radioGroupRussian.check(R.id.radioButtonRussian)
                    clickedLanguageChange = true
                }
            }


            if (!clickedLanguageChange) {
                AppLocale.desiredLocale = Locale(PreferenceProvider.language)
                Reword.reword(binding.root)
                (activity as MainActivity).supportActionBar?.title = ""

                val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)
                title1TextView.visibility = View.VISIBLE
                title1TextView.text  = resources.getText(R.string.settings)
                dialog.dismiss()
            }
        }

        radioGroupGerman?.setOnCheckedChangeListener { _, _ ->
            PreferenceProvider.language = "de"

            when {
                radioButtonEnglish?.isChecked!! -> {
                    radioGroupEnglish?.clearCheck()
                    radioGroupGerman.check(R.id.radioButtonGerman)
                    clickedLanguageChange = true
                }
                radioButtonSerbian?.isChecked!! -> {
                    radioGroupSerbian?.clearCheck()
                    radioGroupGerman.check(R.id.radioButtonGerman)
                    clickedLanguageChange = true
                }
                radioButtonRussian?.isChecked!! -> {
                    radioGroupRussian?.clearCheck()
                    radioGroupGerman.check(R.id.radioButtonGerman)
                    clickedLanguageChange = true
                }
            }


            if (!clickedLanguageChange) {
                AppLocale.desiredLocale = Locale(PreferenceProvider.language)
                Reword.reword(binding.root)
                (activity as MainActivity).supportActionBar?.title = ""

                val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)
                title1TextView.visibility = View.VISIBLE
                title1TextView.text  = resources.getText(R.string.settings)
                dialog.dismiss()
            }
        }

        imageViewCircleCloseDialog?.setOnClickListener {
            dialog.dismiss()
        }
        return dialog
    }

    private fun themeSettings() {
        binding.switchDarkLightTheme.isChecked = PreferenceProvider.darkMode
        if (PreferenceProvider.darkMode) {
            binding.imageViewTheme.setImageResource(R.drawable.ic_dark_mode)
        } else {
            binding.imageViewTheme.setImageResource(R.drawable.ic_light_mode)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingProp = null
    }
}