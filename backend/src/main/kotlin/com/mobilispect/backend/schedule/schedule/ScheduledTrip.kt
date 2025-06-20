package com.mobilispect.backend.schedule.schedule

import com.mobilispect.backend.schedule.route.OneStopRouteID
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(value = "scheduled_trips")
@Suppress("ConstructorParameterNaming") // For _id
data class ScheduledTrip(
    val _id: String,
    val routeID: OneStopRouteID,
    val dates: Collection<LocalDate>,
    val direction: String,
    val version: String
)
