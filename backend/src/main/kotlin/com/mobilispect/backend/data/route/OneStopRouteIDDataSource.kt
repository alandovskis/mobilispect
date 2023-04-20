package com.mobilispect.backend.data.route

interface OneStopRouteIDDataSource {
    fun routeIDs(feedID: String): Result<RouteIDMap>
}
