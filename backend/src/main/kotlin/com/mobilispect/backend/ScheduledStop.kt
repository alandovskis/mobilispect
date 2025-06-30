package com.mobilispect.backend

import org.springframework.data.mongodb.core.mapping.Document
import java.time.ZonedDateTime

@Document(value = "scheduled_stops")
data class ScheduledStop(
    val tripID: String,
    val stopID: String,
    val departsAt: ZonedDateTime? = null,
    val arrivesAt: ZonedDateTime? = null,
    val stopSequence: Int,
    val version: String,
)
