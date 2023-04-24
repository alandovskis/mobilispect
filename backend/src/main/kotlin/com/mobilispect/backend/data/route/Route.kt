@file:Suppress("PropertyName", "ConstructorParameterNaming") // For _id and medianHeadway_min

package com.mobilispect.backend.data.route

import com.mobilispect.backend.data.agency.OneStopAgencyID
import org.springframework.data.mongodb.core.mapping.Document

@Document(value = "routes")
data class Route(
    val _id: OneStopRouteID,
    val shortName: String,
    val longName: String,
    val agencyID: OneStopAgencyID,
    val version: String,
    val headwayHistory: List<HeadwayEntry>
)

data class HeadwayEntry(val medianHeadway_min: Double)
