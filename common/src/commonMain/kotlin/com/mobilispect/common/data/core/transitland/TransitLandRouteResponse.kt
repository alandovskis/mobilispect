package com.mobilispect.common.data.core.transitland

/**
 * A route returned by the transit.land API.
 */
data class TransitLandRouteResponse(
    val routes: Collection<TransitLandRoute>,
)