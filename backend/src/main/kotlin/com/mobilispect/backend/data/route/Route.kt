@file:Suppress("PropertyName", "ConstructorParameterNaming") // For _id and medianHeadway_min

package com.mobilispect.backend.data.route

import com.mobilispect.backend.batch.Entity
import org.springframework.data.mongodb.core.mapping.Document

@Document(value = "routes")
data class Route(
    override val _id: String,
    val shortName: String,
    val longName: String,
    val agencyID: String,
    override val version: String,
    val headwayHistory: List<HeadwayEntry>
) : Entity

data class HeadwayEntry(val medianHeadway_min: Double)
