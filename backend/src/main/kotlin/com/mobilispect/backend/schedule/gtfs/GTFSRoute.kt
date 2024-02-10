package com.mobilispect.backend.schedule.gtfs

import com.mobilispect.backend.schedule.agency.FeedLocalAgencyID
import com.mobilispect.backend.schedule.agency.FeedLocalAgencyIDSerializer
import com.mobilispect.backend.schedule.route.FeedLocalRouteID
import com.mobilispect.backend.schedule.route.FeedLocalRouteIDSerializer
import kotlinx.serialization.Serializable

@Serializable
class GTFSRoute(
    @Serializable(with = FeedLocalRouteIDSerializer::class)
    val route_id: FeedLocalRouteID,
    val route_short_name: String,
    val route_long_name: String,

    @Serializable(with = FeedLocalAgencyIDSerializer::class)
    val agency_id: FeedLocalAgencyID? = null,
)
