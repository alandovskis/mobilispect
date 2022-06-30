package com.mobilispect.common.data.routes

import com.google.gson.annotations.SerializedName

/**
 * A route returned by the transit.land API.
 */
data class TransitLandRouteResponse(
    val routes: Collection<TransitLandRoute>,
)

data class TransitLandRoute(
    @SerializedName("route_long_name")
    val longName: String,

    @SerializedName("route_short_name")
    val shortName: String,
)