package com.neoapps.skijahorina.common

import com.neoapps.skijahorina.features.skicenter.data.ResortRefreshPolicy

object SnowDepthFormatter {

    private val invalidDepthPattern = Regex("^-?\\s*cm$", RegexOption.IGNORE_CASE)

    fun formatForDisplay(raw: String): String =
        normalizeForStorage(raw).ifBlank { "—" }

    fun normalizeForStorage(raw: String): String {
        if (!ResortRefreshPolicy.isInSeason()) return ""
        val trimmed = raw.trim()
        if (isInvalidDepth(trimmed)) return ""
        return trimmed
    }

    private fun isInvalidDepth(raw: String): Boolean {
        if (raw.isEmpty()) return true
        val compact = raw.replace("\\s".toRegex(), "")
        if (compact == "-" || invalidDepthPattern.matches(compact)) return true
        if (!Regex("\\d").containsMatchIn(raw)) return true
        return false
    }
}
