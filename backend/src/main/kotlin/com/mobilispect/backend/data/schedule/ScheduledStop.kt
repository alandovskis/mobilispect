package com.mobilispect.backend.data.schedule

import java.time.LocalDateTime

data class ScheduledStop(
    val routeID: String,
    val stopID: String,
    val departsAt: LocalDateTime? = null,
    val arrivesAt: LocalDateTime? = null,
    val direction: String
)