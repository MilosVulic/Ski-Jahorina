package com.example.skiserbia.features

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.skiserbia.R
import com.example.skiserbia.common.PreferenceProvider
import com.example.skiserbia.databinding.FragmentSettingsBinding
import com.example.skiserbia.main.MainActivity
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

        binding.ripple5.setOnClickListener {
            val dialogLanguage = createLanguageDialog()!!
            clickedLanguageChange = false
            AppLocale.desiredLocale = Locale(PreferenceProvider.language)
            Reword.reword(dialogLanguage.findViewById(R.id.languageDialogConstraint))
            dialogLanguage.show()
        }

        return binding.root
    }

    private fun setUpFragmentName() {
        (activity as MainActivity).supportActionBar?.title = ""
        val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)
        title1TextView.visibility = View.VISIBLE
        title1TextView.text = resources.getText(R.string.settings)
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

        val radioButtonEnglish = dialog?.findViewById<RadioButton>(R.id.radioButtonEnglish)
        val radioButtonSerbian = dialog?.findViewById<RadioButton>(R.id.radioButtonSerbian)
        val radioButtonRussian = dialog?.findViewById<RadioButton>(R.id.radioButtonRussian)

        val imageViewCircleCloseDialog =
            dialog?.findViewById<CircleImageView>(R.id.imageViewCircleCloseDialog)


        when (PreferenceProvider.language) {
            "en" -> radioGroupEnglish?.check(R.id.radioButtonEnglish)
            "sr" -> radioGroupSerbian?.check(R.id.radioButtonSerbian)
            "ru" -> radioGroupRussian?.check(R.id.radioButtonRussian)
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
}