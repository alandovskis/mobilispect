@file:Suppress("PropertyName", "ConstructorParameterNaming") // For _id and medianHeadway_min

package com.mobilispect.backend

import org.springframework.data.mongodb.core.mapping.Document

@Document(value = "routes")
data class Route(
    val _id: String,
    val shortName: String,
    val longName: String,
    val agencyID: String,
    val headwayHistory: List<HeadwayEntry>
)

data class HeadwayEntry(val medianHeadway_min: Double)
