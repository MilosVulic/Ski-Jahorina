package com.example.skiserbia.common

import androidx.core.content.ContextCompat
import com.example.skiserbia.R
import com.example.skiserbia.features.skicenter.slopes.SlopeCategory
import de.hdodenhof.circleimageview.CircleImageView

object IconMarkCategorySetter {


    fun setBackground(category: SlopeCategory, view: CircleImageView) {
        when (category.toString()) {
            SlopeCategory.EASY.name -> {
                view.circleBackgroundColor = ContextCompat.getColor(view.context, R.color.easyColor)
                view.borderColor = ContextCompat.getColor(view.context, R.color.easyColor)
                view.borderWidth = 1
                view.setColorFilter(ContextCompat.getColor(view.context, R.color.easyColor))
            }

            SlopeCategory.MODERATE.name -> {
                view.circleBackgroundColor = ContextCompat.getColor(view.context, R.color.cancelColor)
                view.borderColor = ContextCompat.getColor(view.context, R.color.cancelColor)
                view.borderWidth = 1
                view.setColorFilter(ContextCompat.getColor(view.context, R.color.cancelColor))
            }

            SlopeCategory.DIFFICULT.name -> {
                view.circleBackgroundColor = ContextCompat.getColor(view.context, R.color.difficultColor)
                view.borderColor = ContextCompat.getColor(view.context, R.color.difficultColor)
                view.borderWidth = 1
                view.setColorFilter(ContextCompat.getColor(view.context, R.color.difficultColor))
            }
        }
    }
}