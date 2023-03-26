package com.mobilispect.backend.data.schedule

data class ScheduledStop(
    val tripID: String,
    val stopID: String,
    val departsAt: DateTimeOffset? = null,
    val arrivesAt: DateTimeOffset? = null,
    val stopSequence: Int,
    val version: String,
)
