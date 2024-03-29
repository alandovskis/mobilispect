package com.mobilispect.backend.schedule.gtfs

import com.mobilispect.backend.schedule.route.FeedLocalRouteID
import com.mobilispect.backend.schedule.route.OneStopRouteID
import com.mobilispect.backend.schedule.route.RouteIDDataSource

/**
 * A [RouteIDDataSource] suitable for testing.
 */
internal class TestRouteIDDataSource : RouteIDDataSource {
    private val routeIDMap = mutableMapOf<FeedLocalRouteID, OneStopRouteID>()

    init {
        routeIDMap[FeedLocalRouteID("1")] = OneStopRouteID("r-f2566-1")
        routeIDMap[FeedLocalRouteID("T1")] = OneStopRouteID("r-f2566-t1")
        routeIDMap[FeedLocalRouteID("115")] = OneStopRouteID("r-f2565-115")
    }

    override fun routeIDs(feedID: String): Result<Map<FeedLocalRouteID, OneStopRouteID>> = Result.success(routeIDMap)
}
