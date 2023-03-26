package com.mobilispect.backend.data.schedule

import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(value = "scheduled_trips")
data class ScheduledTrip(
    val _id: String,
    val routeID: String,
    val dates: Collection<LocalDate>,
    val direction: String,
    val version: String
)
