package com.mobilispect.data.transit_land

import com.google.gson.annotations.SerializedName

data class TransitLandRoute(
    @SerializedName("route_long_name")
    val longName: String,

    @SerializedName("route_short_name")
    val shortName: String,
)