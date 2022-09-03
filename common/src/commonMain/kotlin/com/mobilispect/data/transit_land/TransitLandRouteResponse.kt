package com.mobilispect.data.transit_land

/**
 * A route returned by the transit.land API.
 */
data class TransitLandRouteResponse(
    val routes: Collection<TransitLandRoute>,
)