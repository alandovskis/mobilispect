package com.mobilispect.backend.data.gtfs

import kotlinx.serialization.Serializable

@Serializable
data class GTFSAgency(
    val agency_id: String,
    val agency_name: String
)