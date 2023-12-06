package com.example.skiserbia.common

import java.time.Duration
import java.time.LocalDateTime

object Utils {

    fun isTimeDifferenceGreaterThanProvidedMinutes(dateTime1: LocalDateTime, dateTime2: LocalDateTime, minutes: Int): Boolean {
        val duration = Duration.between(dateTime1, dateTime2)
        val minutesDifference = duration.toMinutes()

        return minutesDifference > minutes
    }
}