package com.mobilispect.common.data.routes

/**
 * A reference to a route in OneStopID format
 *
 * Reference: https://www.transit.land/documentation/onestop-id-scheme/
 *
 * Ex:
 */
data class RouteRef(
    private val idSuffix: String,
) {
    val id: String = "r-$idSuffix"
}
