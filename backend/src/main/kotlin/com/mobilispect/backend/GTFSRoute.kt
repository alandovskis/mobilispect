package com.mobilispect.backend

import kotlinx.serialization.Serializable


@Serializable
class GTFSRoute(
    val route_id: String,
    val route_short_name: String,
    val route_long_name: String,
    val agency_id: String,
)
