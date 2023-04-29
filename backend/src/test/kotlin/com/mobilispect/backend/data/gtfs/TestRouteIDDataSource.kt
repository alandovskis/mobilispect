package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.route.FeedLocalRouteID
import com.mobilispect.backend.data.route.OneStopRouteID
import com.mobilispect.backend.data.route.RouteIDDataSource
import com.mobilispect.backend.data.route.RouteIDMap

/**
 * A [RouteIDDataSource] suitable for testing.
 */
internal class TestRouteIDDataSource : RouteIDDataSource {
    private val routeIDMap = RouteIDMap()

    init {
        routeIDMap.add(routeID = FeedLocalRouteID("1"), onestopID = OneStopRouteID("r-f2566-1"))
        routeIDMap.add(
            routeID = FeedLocalRouteID("T1"), onestopID = OneStopRouteID("r-f2566-t1")
        )
        routeIDMap.add(routeID = FeedLocalRouteID("115"), onestopID = OneStopRouteID("r-f2565-115"))
    }

    override fun routeIDs(feedID: String): Result<RouteIDMap> = Result.success(routeIDMap)
}
