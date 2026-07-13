package com.neoapps.skijahorina.common

import com.neoapps.skijahorina.features.skicenter.lifts.LiftInfo

enum class LiftStatus {
    OPEN,
    CLOSED,
    ON_HOLD,
}

fun LiftInfo.liftStatus(): LiftStatus {
    val status = inFunction.trim()
    return when {
        status.equals("unknown", ignoreCase = true) -> LiftStatus.ON_HOLD
        isOpenStatus(status) -> LiftStatus.OPEN
        isClosedStatus(status) -> LiftStatus.CLOSED
        else -> LiftStatus.CLOSED
    }
}

fun isOpenStatus(inFunction: String): Boolean =
    when (inFunction) {
        "/img/resorts/lift-status-open.svg",
        "/img/resorts/lift-status-opened.svg",
        "open", "Open", "opened", "Opened" -> true
        else -> false
    }

fun isClosedStatus(inFunction: String): Boolean =
    when (inFunction) {
        "/img/resorts/lift-status-close.svg",
        "/img/resorts/lift-status-closed.svg",
        "close", "Close", "closed", "Closed" -> true
        else -> false
    }

fun List<LiftInfo>.openCount(): Int = count { it.liftStatus() == LiftStatus.OPEN }

fun List<LiftInfo>.sortedByStatusThenName(): List<LiftInfo> =
    sortedWith(
        compareBy<LiftInfo> {
            when (it.liftStatus()) {
                LiftStatus.OPEN -> 0
                LiftStatus.ON_HOLD -> 1
                LiftStatus.CLOSED -> 2
            }
        }.thenBy { it.name.lowercase() }
    )
