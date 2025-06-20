package com.mobilispect.backend.schedule.gtfs

import com.mobilispect.backend.schedule.route.FeedLocalRouteID
import com.mobilispect.backend.schedule.route.FeedLocalRouteIDSerializer
import kotlinx.serialization.Serializable

@Serializable
class GTFSTrip(
    @Serializable(with = FeedLocalRouteIDSerializer::class)
    val route_id: FeedLocalRouteID,

    val service_id: String,
    val trip_id: String,
    val trip_headsign: String,
)
