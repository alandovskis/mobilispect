package com.mobilispect.backend

import org.springframework.data.mongodb.core.mapping.Document
import java.time.ZonedDateTime

@Document(value = "scheduled_stops")
data class ScheduledStop(
    /**
     * A globally unique trip ID.
     */
    val tripID: String,

    /**
     * A globally unique stop ID (in OneStop ID format).
     */
    val stopID: String,
    val departsAt: ZonedDateTime? = null,
    val arrivesAt: ZonedDateTime? = null,
    val stopSequence: Int,
    val versions: Collection<String>,
)
