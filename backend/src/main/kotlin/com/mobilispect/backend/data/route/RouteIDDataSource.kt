package com.mobilispect.backend.data.route

interface RouteIDDataSource {
    fun routeIDs(feedID: String): Result<Map<FeedLocalRouteID, OneStopRouteID>>
}
