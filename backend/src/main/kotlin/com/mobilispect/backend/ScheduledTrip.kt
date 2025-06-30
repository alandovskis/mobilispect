package com.mobilispect.backend

import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(value = "scheduled_trips")
data class ScheduledTrip(
    val id: String,
    val routeID: String,
    val dates: Collection<LocalDate>,
    val direction: String,
    val versions: Collection<String>
)
