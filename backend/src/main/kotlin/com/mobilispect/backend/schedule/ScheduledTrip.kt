package com.mobilispect.backend.schedule

import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(value = "scheduled_trips")
data class ScheduledTrip(
    val uid: String,
    val routeID: String,
    val dates: Collection<LocalDate>,
    val direction: String,
    val versions: Collection<String>
)
