package com.mobilispect.common.data.routes

/**
 * A reference to a route in OneStopID format
 *
 * Reference: https://www.transit.land/documentation/onestop-id-scheme/
 *
 */
data class RouteRef(
    private val geohash: String,
    val routeNumber: String,
) {
    val id: String = "r-$geohash-$routeNumber"
}
