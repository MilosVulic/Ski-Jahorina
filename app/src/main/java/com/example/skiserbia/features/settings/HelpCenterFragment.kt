package com.example.skiserbia.features.settings

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.skiserbia.R
import com.example.skiserbia.databinding.FragmentHelpCenterBinding
import com.example.skiserbia.main.MainActivity

class HelpCenterFragment : Fragment() {

    private var openedFirst = false
    private var openedSecond = false
    private var openedThird = false
    private var openedForth = false

    private var bindingProp: FragmentHelpCenterBinding? = null
    private val binding get() = bindingProp!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingProp = FragmentHelpCenterBinding.inflate(inflater, container, false)

        val title1TextView = (activity as MainActivity).findViewById<TextView>(R.id.title1)
        title1TextView.visibility = View.VISIBLE
        title1TextView.text = resources.getString(R.string.help)
        title1TextView.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))

        binding.constraintFirst.setOnClickListener {
            if (openedFirst) {
                binding.expandableLayoutFirst.collapse()
                val anim = createAnimCollapse(binding.imageViewDownFirst)
                anim.start()
                openedFirst = false
                binding.textView14.setTextColor(ResourcesCompat.getColor(resources, R.color.colorHelpCenter, null))
                binding.imageViewDownFirst.setColorFilter(ResourcesCompat.getColor(resources, R.color.colorDropDown, null))

            } else {
                binding.expandableLayoutFirst.expand()
                val anim = createAnimExpand(binding.imageViewDownFirst)
                anim.start()
                openedFirst = true
                binding.textView14.setTextColor(ResourcesCompat.getColor(resources, R.color.colorFocusedField, null))
                binding.imageViewDownFirst.setColorFilter(ResourcesCompat.getColor(resources, R.color.colorFocusedField, null))
            }
        }

        binding.constraintSecond.setOnClickListener {
            if (openedSecond) {
                binding.expandableLayoutSecond.collapse()
                val anim = createAnimCollapse(binding.imageViewDownSecond)
                anim.start()
                openedSecond = false
                binding.textViewEntriesQuestion.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.colorHelpCenter,
                        null
                    )
                )
                binding.imageViewDownSecond.setColorFilter(ResourcesCompat.getColor(resources, R.color.colorDropDown, null))

            } else {
                binding.expandableLayoutSecond.expand()
                val anim = createAnimExpand(binding.imageViewDownSecond)
                anim.start()
                openedSecond = true
                binding.textViewEntriesQuestion.setTextColor(ResourcesCompat.getColor(resources, R.color.colorFocusedField, null))
                binding.imageViewDownSecond.setColorFilter(ResourcesCompat.getColor(resources, R.color.colorFocusedField, null))
            }
        }


        binding.constraintThird.setOnClickListener {
            if (openedThird) {
                binding.expandableLayoutThird.collapse()
                val anim = createAnimCollapse(binding.imageViewDownThird)
                anim.start()
                openedThird = false
                binding.textViewThirdQuestion.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.colorHelpCenter,
                        null
                    )
                )
                binding.imageViewDownThird.setColorFilter(ResourcesCompat.getColor(resources, R.color.colorDropDown, null))

            } else {
                binding.expandableLayoutThird.expand()
                val anim = createAnimExpand(binding.imageViewDownThird)
                anim.start()
                openedThird = true
                binding.textViewThirdQuestion.setTextColor(ResourcesCompat.getColor(resources, R.color.colorFocusedField, null))
                binding.imageViewDownThird.setColorFilter(ResourcesCompat.getColor(resources, R.color.colorFocusedField, null))
            }
        }


        binding.constraintForth.setOnClickListener {
            if (openedForth) {
                binding.expandableLayoutForth.collapse()
                val anim = createAnimCollapse(binding.imageViewDownForth)
                anim.start()
                openedForth = false
                binding.textViewForthQuestion.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.colorHelpCenter,
                        null
                    )
                )
                binding.imageViewDownForth.setColorFilter(ResourcesCompat.getColor(resources, R.color.colorDropDown, null))

            } else {
                binding.expandableLayoutForth.expand()
                val anim = createAnimExpand(binding.imageViewDownForth)
                anim.start()
                openedForth = true
                binding.textViewForthQuestion.setTextColor(ResourcesCompat.getColor(resources, R.color.colorFocusedField, null))
                binding.imageViewDownForth.setColorFilter(ResourcesCompat.getColor(resources, R.color.colorFocusedField, null))
            }
        }


        return binding.root
    }

    private fun createAnimCollapse(imageView: ImageView): ObjectAnimator {
        val anim: ObjectAnimator = ObjectAnimator.ofFloat(
            imageView,
            "rotation",
            180.0f,
            0.0f
        )
        anim.duration = 300
        return anim
    }


    private fun createAnimExpand(imageView: ImageView): ObjectAnimator {
        val anim: ObjectAnimator = ObjectAnimator.ofFloat(
            imageView,
            "rotation",
            0.0f,
            180.0f
        )
        anim.duration = 300
        return anim
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingProp = null
    }
}