package com.neoapps.skijahorina.common

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

object CacheTimestampFormatter {

    private val displayFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    private val displayWithDateFormatter =
        DateTimeFormatter.ofPattern("d MMM · HH:mm", Locale.getDefault())

    fun formatForDisplay(raw: String?): String? {
        val dateTime = parse(raw) ?: return null
        val zone = ZoneId.systemDefault()
        val local = dateTime.atZone(zone).toLocalDateTime()
        val today = LocalDateTime.now(zone).toLocalDate()
        return if (local.toLocalDate() == today) {
            local.format(displayFormatter)
        } else {
            local.format(displayWithDateFormatter)
        }
    }

    fun parse(raw: String?): Instant? {
        if (raw.isNullOrBlank()) return null
        return try {
            Instant.parse(raw)
        } catch (_: DateTimeParseException) {
            try {
                OffsetDateTime.parse(raw).toInstant()
            } catch (_: DateTimeParseException) {
                try {
                    LocalDateTime.parse(raw).atZone(ZoneId.systemDefault()).toInstant()
                } catch (_: DateTimeParseException) {
                    null
                }
            }
        }
    }

    /** Prefer API timestamp when present; otherwise local fetch time. */
    fun bestTimestamp(apiUpdatedAt: String?, localFetchTime: String?): String? {
        val api = formatForDisplay(apiUpdatedAt)
        if (api != null) return api
        return formatForDisplay(localFetchTime)
    }
}
