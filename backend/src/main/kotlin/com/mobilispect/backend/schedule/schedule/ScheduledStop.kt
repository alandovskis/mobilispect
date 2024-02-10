package com.mobilispect.backend.schedule.schedule

import org.springframework.data.mongodb.core.mapping.Document

@Document(value = "scheduled_stops")
data class ScheduledStop(
    val tripID: String,
    val stopID: String,
    val departsAt: DateTimeOffset? = null,
    val arrivesAt: DateTimeOffset? = null,
    val stopSequence: Int,
    val version: String,
)
