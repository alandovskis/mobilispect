package com.mobilispect.backend

import com.mobilispect.backend.schedule.route.RouteIDDataSource

/**
 * A [RouteIDDataSource] suitable for testing.
 */
internal class TestRouteIDDataSource : RouteIDDataSource {
    private val routeIDMap = mutableMapOf<String, String>()

    init {
        routeIDMap["1"] = "r-f2566-1"
        routeIDMap["T1"] = "r-f2566-t1"
        routeIDMap["115"] = "r-f2565-115"
    }

    override fun routeIDs(feedID: String): Result<Map<String, String>> = Result.success(routeIDMap)
}
