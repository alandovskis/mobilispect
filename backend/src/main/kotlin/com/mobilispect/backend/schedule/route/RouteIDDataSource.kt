package com.mobilispect.backend.schedule.route

interface RouteIDDataSource {
    fun routeIDs(feedID: String): Result<Map<FeedLocalRouteID, OneStopRouteID>>
}
