package com.mobilispect.backend.data.gtfs

import kotlinx.serialization.Serializable

@Serializable
class GTFSTrip(
    val route_id: String,
    val service_id: String,
    val trip_id: String,
    val trip_headsign: String,
)
