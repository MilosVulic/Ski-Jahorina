package com.neoapps.skiserbia.features.skicenter.slopes

object SlopeCategoryMapper {

    fun mapToSlopeCategory(category: String): SlopeCategory {
        return when (category) {
            "Easy" -> SlopeCategory.EASY
            "Moderate" -> SlopeCategory.MODERATE
            "Difficult" -> SlopeCategory.DIFFICULT
            "Teška" -> SlopeCategory.DIFFICULT
            "Srednja" -> SlopeCategory.MODERATE
            "Laka" -> SlopeCategory.EASY
            else -> SlopeCategory.EASY
        }
    }
}