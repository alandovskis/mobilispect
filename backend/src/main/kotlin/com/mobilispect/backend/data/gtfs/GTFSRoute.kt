package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.route.FeedLocalRouteID
import com.mobilispect.backend.data.route.FeedLocalRouteIDSerializer
import kotlinx.serialization.Serializable

@Serializable
class GTFSRoute(
    @Serializable(with = FeedLocalRouteIDSerializer::class)
    val route_id: FeedLocalRouteID,
    val route_short_name: String,
    val route_long_name: String,
    val agency_id: String
)
